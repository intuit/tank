package com.intuit.tank.auth.sso;

import com.intuit.tank.auth.sso.models.Token;
import com.intuit.tank.auth.sso.models.UserInfo;
import com.intuit.tank.http.WebHttpClient;
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
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TankOidcAuthorization
 *
 * @author Shawn Park
 */
public class TankOidcAuthorizationTest {

    @Mock
    private WebHttpClient _webHttpClientMock;
    @Mock
    private TankConfig _tankConfigMock;
    @Mock
    private HierarchicalConfiguration _hierarchicalConfigurationMock;
    @Mock
    private OidcSsoConfig _oidcSsoConfigMock;
    @Mock
    private HttpResponse<String> _httpResponseMock;

    /**
     * Decoded ID TOKEN STUB
     {
         "sub":"50002336751",
         "iss":"https://federatesys.intuit.com",
         "exp":1632787987,
         "acr":"urn:oasis:names:tc:SAML:2.0:ac:classes:Telephony",
         "auth_time":1632780914,
         "name":"Test User",
         "groups":[
            "USA-Employees"
            ],
         "preferred_username":"userName",
         "given_name":"Test",
         "family_name":"User",
         "email":"test_user@intuit.com"
     }
     */
    private final String ID_TOKEN_STUB = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImFCWlY5TlhmRjZhRnBZUHBlYUkwWVVMZVhqcyJ9.eyJzdWIiOiI1MDAwMjMzNjc1MSIsImlzcyI6Imh0dHBzOi8vZmVkZXJhdGVzeXMuaW50dWl0LmNvbSIsImV4cCI6MTYzMjc4Nzk4NywiYWNyIjoidXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFjOmNsYXNzZXM6VGVsZXBob255IiwiYXV0aF90aW1lIjoxNjMyNzgwOTE0LCJuYW1lIjoiVGVzdCBVc2VyIiwiZ3JvdXBzIjpbIlVTQS1FbXBsb3llZXMiXSwicHJlZmVycmVkX3VzZXJuYW1lIjoidXNlck5hbWUiLCJnaXZlbl9uYW1lIjoiVGVzdCIsImZhbWlseV9uYW1lIjoiVXNlciIsImVtYWlsIjoidGVzdF91c2VyQGludHVpdC5jb20ifQ==.J7nDDhwhKLDQJtPp-KFiElW9kozTl2_advritySU4d4K1mh8zF8jkHdlCvIZuHNHKLT5cqqGpBONFHAO3-KP5ebgplm3IRtG7essdQbZTTSN0j7TaahomCV2fp4_KEQX2lgfAD4UkBrIFk9VlmmrUypZ55Zqmo96tsnC2Uv9Opmf3rFFjERPiHrDTu-6vqBIlJdzjYUuehTN6kzQM1uZAsgvACKMaJZaW8WP1zyp0aXuII3JHFavB7c-XSIX767uERIbOozsYW2RDDe8ZZuz7rdLVjdsgjDdigqBZ2QaTpdB6KbCNRH4JzBMZ1mwqgfIptSAdegHYK-ub7MMYoun2A";
    private final Token TOKEN_STUB = new Token();
    private final String AUTHORIZATION_CODE_STUB = "testAuthorizationCode";
    private final String ACCESS_TOKEN_STUB = "{\"access_token\":\"000000000000\",\"id_token\":\"ey0000000000000000\",\"token_type\":\"Bearer\",\"expires_in\":3599}\n";

    @InjectMocks
    private TankOidcAuthorization _sut;

    private AutoCloseable closeable;

    @BeforeEach
    public void SetUp() {
        closeable = MockitoAnnotations.openMocks(this);
        TOKEN_STUB.setIdToken(ID_TOKEN_STUB);
        TOKEN_STUB.setAccessToken(ACCESS_TOKEN_STUB);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    //region DecodeIdToken

    @Test
    public void DecodeIdToken_Given_ID_Token_Returns_Valid_UserInfo() {
        // Arrange + Act
        UserInfo userInfoResponse = _sut.DecodeIdToken(TOKEN_STUB);

        // Assert
        assertNotNull(userInfoResponse);
        assertEquals("userName", userInfoResponse.getUsername());
        assertEquals("test_user@intuit.com", userInfoResponse.getEmail());
    }

    @Test
    public void DecodeIdToken_Given_Invalid_Token_Throws_Exception() {
        // Arrange + Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {
            _sut.DecodeIdToken(null);
        });
    }

    //endregion

    //region GetAccessToken

    @Test
    public void GetAccessToken_Given_Authorization_Code_HTTP_Posts_To_Authentication_Server() throws IOException {
        // Arrange
        when(_tankConfigMock.getOidcSsoConfig()).thenReturn(_oidcSsoConfigMock);
        when(_oidcSsoConfigMock.getConfiguration()).thenReturn(_hierarchicalConfigurationMock);
        when(_oidcSsoConfigMock.getAuthorizationEndpoint()).thenReturn("Test-Auth-Url");
        when(_oidcSsoConfigMock.getClientSecret()).thenReturn("Test-Client-Secret");
        when(_oidcSsoConfigMock.getClientId()).thenReturn("Test-Client-Id");
        when(_oidcSsoConfigMock.getRedirectUrl()).thenReturn("Test-Redirect-Url");
        when(_webHttpClientMock.Post(any(String.class), any(Map.class))).thenReturn(_httpResponseMock);
        when(_httpResponseMock.body()).thenReturn(ACCESS_TOKEN_STUB);

        // Act
        _sut.GetAccessToken(AUTHORIZATION_CODE_STUB);

        // Assert
        verify(_webHttpClientMock, times(1)).Post(any(String.class), any(Map.class));
    }

    @Test
    public void GetAccessToken_Given_Authorization_Code_Returns_Access_Token() throws IOException {
        // Arrange
        when(_tankConfigMock.getOidcSsoConfig()).thenReturn(_oidcSsoConfigMock);
        when(_oidcSsoConfigMock.getConfiguration()).thenReturn(_hierarchicalConfigurationMock);
        when(_oidcSsoConfigMock.getAuthorizationEndpoint()).thenReturn("Test-Auth-Url");
        when(_oidcSsoConfigMock.getClientSecret()).thenReturn("Test-Client-Secret");
        when(_oidcSsoConfigMock.getClientId()).thenReturn("Test-Client-Id");
        when(_oidcSsoConfigMock.getRedirectUrl()).thenReturn("Test-Redirect-Url");
        when(_webHttpClientMock.Post(any(String.class), any(Map.class))).thenReturn(_httpResponseMock);
        when(_httpResponseMock.body()).thenReturn(ACCESS_TOKEN_STUB);

        // Act
        Token accessTokenResponse = _sut.GetAccessToken(AUTHORIZATION_CODE_STUB);

        // Assert
        assertNotNull(accessTokenResponse);
        assertNotNull(accessTokenResponse.getAccessToken());
        assertNotNull(accessTokenResponse.getIdToken());
    }

    @Test
    public void GetAccessToken_Given_Invalid_Authorization_Code_Throws_Exception() throws IllegalArgumentException{
        // Arrange + Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {
            _sut.GetAccessToken(null);
        });
    }

    @Test
    public void GetAccessToken_Given_Invalid_OidcConfig_Throws_Exception() throws IllegalArgumentException {
        // Arrange
        when(_tankConfigMock.getOidcSsoConfig()).thenReturn(_oidcSsoConfigMock);
        when(_oidcSsoConfigMock.getConfiguration()).thenReturn(null);

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {
            _sut.GetAccessToken(AUTHORIZATION_CODE_STUB);
        });
    }

    //endregion
}
