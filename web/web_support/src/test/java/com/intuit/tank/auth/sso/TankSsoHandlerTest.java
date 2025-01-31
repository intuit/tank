package com.intuit.tank.auth.sso;

import com.intuit.tank.admin.UserCreate;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.auth.sso.models.Token;
import com.intuit.tank.auth.sso.models.UserInfo;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TankSsoHandler
 *
 * @author Shawn Park
 */
public class TankSsoHandlerTest {

    @Mock
    private TankConfig _tankConfigMock;
    @Mock
    private OidcSsoConfig _oidcSsoConfigMock;
    @Mock
    private HierarchicalConfiguration _hierarchicalConfigurationMock;
    @Mock
    private TankOidcAuthorization _tankOidcAuthorizationMock;
    @Mock
    private TankSecurityContext _tankSecurityContextMock;
    @Mock
    private UserDao _userDaoMock;
    @Mock
    private UserCreate _userCreateMock;

    private final String AUTHORIZATION_CODE_STUB = "testAuthorizationCode";
    private Token TOKEN_STUB;
    private UserInfo USERINFO_STUB;
    private User USER_STUB;

    @InjectMocks
    private TankSsoHandler _sut;

    private AutoCloseable closeable;

    @BeforeEach
    public void SetUp() {
        closeable = MockitoAnnotations.openMocks(this);

        TOKEN_STUB = new Token();
        USERINFO_STUB = new UserInfo();
        USER_STUB = new User();

        TOKEN_STUB.setIdToken("testIdToken");
        TOKEN_STUB.setAccessToken("testAccessToken");
        USERINFO_STUB.setEmail("user@intuit.com");
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    //region GetOnLoadAuthorizationRequest Happy Path

    @Test
    public void GetOnLoadAuthorizationRequest_Given_AuthorizationCode_Call_To_Get_AccessToken() {
        // Arrange
        when(_tankConfigMock.getOidcSsoConfig()).thenReturn(_oidcSsoConfigMock);
        when(_oidcSsoConfigMock.getConfiguration()).thenReturn(_hierarchicalConfigurationMock);
        when(_oidcSsoConfigMock.getAuthorizationUrl()).thenReturn("https://www.authorization-url.com/auth");
        when(_oidcSsoConfigMock.getClientId()).thenReturn("client-id");
        when(_oidcSsoConfigMock.getRedirectUrl()).thenReturn("https://www.redirect-url.com");

        // Act
        var authorizationRequestString = _sut.GetOnLoadAuthorizationRequest();

        // Assert
        assertNotNull(authorizationRequestString);
        assertFalse(authorizationRequestString.isEmpty());
        assertFalse(authorizationRequestString.isBlank());
    }

    //endregion

    //region GetOnLoadAuthorizationRequest Negative Case

    @Test
    public void GetOnLoadAuthorizationRequest_Given_Null_Config_Throws_IllegalArgumentException() {
        // Arrange
        when(_tankConfigMock.getOidcSsoConfig()).thenReturn(_oidcSsoConfigMock);
        when(_oidcSsoConfigMock.getConfiguration()).thenReturn(null);

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {
            _sut.GetOnLoadAuthorizationRequest();
        });
    }

    //endregion

    //region HandleSsoAuthorization Happy Path

    @Test
    public void HandleSsoAuthorization_Given_AuthorizationCode_Call_To_Get_AccessToken() throws IOException {
        // Arrange
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);

        // Act
        _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB);

        // Assert
        verify(_tankOidcAuthorizationMock, times(1)).GetAccessToken(any(String.class));
    }

    @Test
    public void HandleSsoAuthorization_Given_AccessToken_Call_To_Decode_To_UserInfo() throws IOException {
        // Arrange
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);

        // Act
        _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB);

        // Assert
        verify(_tankOidcAuthorizationMock, times(1)).DecodeIdToken(any(Token.class));
    }

    @Test
    public void HandleSsoAuthorization_Any_SSO_User_Call_To_Check_If_User_Exists() throws IOException {
        // Arrange
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);

        // Act
        _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB);

        // Assert
        verify(_userDaoMock, times(1)).findByEmail(any(String.class));
    }

    @Test
    public void HandleSsoAuthorization_Given_New_User_Call_To_Create_New_User() throws IOException {
        // Arrange
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);
        when(_userDaoMock.findByEmail(any(String.class))).thenReturn(null);

        // Act
        _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB);

        // Assert
        verify(_userCreateMock, times(1)).CreateUser(any(UserInfo.class));
    }

    @Test
    public void HandleSsoAuthorization_Given_Existing_User_Does_Not_Call_To_Create_New_User() throws IOException {
        // Arrange
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);
        when(_userDaoMock.findByEmail(any(String.class))).thenReturn(USER_STUB);

        // Act
        _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB);

        // Assert
        verify(_userCreateMock, times(0)).CreateUser(any(UserInfo.class));
    }

    @Test
    public void HandleSsoAuthorization_Given_Any_User_Call_To_Populate_Security_Context() throws IOException {
        // Arrange
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);
        when(_userDaoMock.findByEmail(any(String.class))).thenReturn(USER_STUB);

        // Act
        _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB);

        // Assert
        verify(_tankSecurityContextMock, times(1)).ssoSecurityContext(any(User.class));
    }

    //endregion

    //region HandleSsoAuthorization Negative Case

    @Test
    public void HandleSsoAuthorization_Given_Invalid_AuthorizationCode_Throws_Exception() throws IllegalArgumentException {
        // Arrange + Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {
            _sut.HandleSsoAuthorization(null);
        });
    }

    @Test
    public void HandleSsoAuthorization_Given_Invalid_OidcConfig_Throws_Exception() throws IllegalArgumentException {
        // Arrange + Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {
            _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB);
        });
    }

    //endregion
}
