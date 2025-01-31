package com.intuit.tank.config;

import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.auth.sso.TankSsoHandler;
import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * LoginFilterTest
 *
 * @author Shawn Park
 */
public class LoginFilterTest {

    @Mock
    private TankSecurityContext _tankSecurityContextMock;
    @Mock
    private HierarchicalConfiguration _hierarchicalConfigurationMock;
    @Mock
    private TankSsoHandler _tankSsoHandlerMock;
    @Mock
    private HttpServletRequest _httpServletRequestMock;
    @Mock
    private HttpServletResponse _httpServletResponseMock;
    @Mock
    private HttpSession _mockHttpSession;
    @Mock
    private FilterChain _filterChainResponseMock;
    @Mock
    private Principal _principalMock;
    @Mock
    private TankConfig _tankConfigMock;
    @Mock
    private OidcSsoConfig _oidcSsoConfigMock;

    private final String AUTH_CODE_STUB = "testAuthCodeParameter";
    private final String CONTEXT_PATH_STUB = "testContextPath";

    @InjectMocks
    private LoginFilter _sut;

    private AutoCloseable closeable;

    @BeforeEach
    public void SetUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void DoFilter_Given_Onload_Without_SSO_Config_Redirects_To_Standard_Login_Page() throws IOException, ServletException {
        // Arrange
        when(_tankConfigMock.getOidcSsoConfig()).thenReturn(_oidcSsoConfigMock);
        when(_oidcSsoConfigMock.getConfiguration()).thenReturn(null);
        when(_tankSecurityContextMock.getCallerPrincipal()).thenReturn(null);
        when(_httpServletRequestMock.getSession()).thenReturn(_mockHttpSession);
        when(_httpServletRequestMock.getContextPath()).thenReturn(CONTEXT_PATH_STUB);
        when(_httpServletRequestMock.getRequestURI()).thenReturn(CONTEXT_PATH_STUB);
        // Act
        _sut.doFilter(_httpServletRequestMock, _httpServletResponseMock, _filterChainResponseMock);

        // Assert
        verify(_httpServletResponseMock, times(1)).sendRedirect(any(String.class));
    }

    @Test
    public void DoFilter_Given_Authorization_Code_Parameter_Call_SSO_Handler() throws IOException, ServletException {
        // Arrange
        when(_tankSecurityContextMock.getCallerPrincipal()).thenReturn(_principalMock);
        when(_httpServletRequestMock.getParameter(any(String.class))).thenReturn(AUTH_CODE_STUB);
        when(_tankConfigMock.getOidcSsoConfig()).thenReturn(_oidcSsoConfigMock);
        when(_oidcSsoConfigMock.getConfiguration()).thenReturn(_hierarchicalConfigurationMock);
        when(_httpServletRequestMock.getContextPath()).thenReturn(CONTEXT_PATH_STUB);
        when(_httpServletRequestMock.getRequestURI()).thenReturn(CONTEXT_PATH_STUB);

        // Act
        _sut.doFilter(_httpServletRequestMock, _httpServletResponseMock, _filterChainResponseMock);

        // Assert
        verify(_tankSsoHandlerMock, times(1)).HandleSsoAuthorization(any(String.class));
    }

    @Test
    public void DoFilter_With_No_Authorization_Code_Parameter_Does_Not_Call_SSO_Handler() throws IOException, ServletException {
        // Arrange
        when(_tankSecurityContextMock.getCallerPrincipal()).thenReturn(_principalMock);
        when(_httpServletRequestMock.getContextPath()).thenReturn(CONTEXT_PATH_STUB);
        when(_httpServletRequestMock.getRequestURI()).thenReturn(CONTEXT_PATH_STUB);

        // Act
        _sut.doFilter(_httpServletRequestMock, _httpServletResponseMock, _filterChainResponseMock);

        // Assert
        verify(_tankSsoHandlerMock, times(0)).HandleSsoAuthorization(any(String.class));
    }

    @Test
    public void DoFilter_Given_IllegalArgumentException_Redirects() throws IOException, ServletException {
        // Arrange
        when(_tankConfigMock.getOidcSsoConfig()).thenReturn(_oidcSsoConfigMock);
        when(_tankSecurityContextMock.getCallerPrincipal()).thenReturn(_principalMock);
        when(_httpServletRequestMock.getParameter(any(String.class))).thenReturn(AUTH_CODE_STUB);
        when(_httpServletRequestMock.getSession()).thenReturn(_mockHttpSession);
        when(_httpServletRequestMock.getContextPath()).thenReturn(CONTEXT_PATH_STUB);
        when(_httpServletRequestMock.getRequestURI()).thenReturn(CONTEXT_PATH_STUB);
        doThrow(new IllegalArgumentException()).when(_tankSsoHandlerMock).HandleSsoAuthorization(any(String.class));

        // Act
        _sut.doFilter(_httpServletRequestMock, _httpServletResponseMock, _filterChainResponseMock);

        // Assert
        verify(_httpServletResponseMock, times(1)).sendRedirect(any(String.class));
    }
}
