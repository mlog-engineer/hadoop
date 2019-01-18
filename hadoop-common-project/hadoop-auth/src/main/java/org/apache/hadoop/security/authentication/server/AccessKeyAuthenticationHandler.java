package org.apache.hadoop.security.authentication.server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.apache.hadoop.security.authentication.client.PseudoAuthenticator;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class AccessKeyAuthenticationHandler implements AuthenticationHandler {

  /**
   * Constant that identifies the authentication mechanism.
   */
  public static final String TYPE = "accesskey";

  public static final String ACCESSKEY_AUTH_CLASS = TYPE + ".auth.class.impl";

  /**
   * Constant for the configuration property that indicates if anonymous users are allowed.
   */
  public static final String ANONYMOUS_ALLOWED = TYPE + ".anonymous.allowed";

  private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

  private static final String TOKEN_AUTH = "AccessKeyAuth";

  private boolean acceptAnonymous;

  private AccessKeyAuthenticator accessKeyAuthenticator;

  /**
   * Creates a Hadoop pseudo authentication handler with the default auth-token
   * type, <code>simple</code>.
   */
  public AccessKeyAuthenticationHandler() {

  }


  /**
   * Initializes the authentication handler instance.
   * <p>
   * This method is invoked by the {@link AuthenticationFilter#init} method.
   *
   * @param config configuration properties to initialize the handler.
   *
   * @throws ServletException thrown if the handler could not be initialized.
   */
  @Override
  public void init(Properties config) throws ServletException {
    acceptAnonymous = Boolean.parseBoolean(config.getProperty(ANONYMOUS_ALLOWED, "false"));
    String authClass =
        config.getProperty(ACCESSKEY_AUTH_CLASS,acceptAnonymous?AnonymousAccessKeyAuthenticator.class.getName():null);
    if(authClass==null){
      throw new ServletException("httpfs.authentication.accesskey.auth.class.impl not set");
    }
    try {
      AccessKeyAuthenticator authenticator = (AccessKeyAuthenticator)
          Class.forName(authClass).newInstance();
      authenticator.init();
      this.accessKeyAuthenticator = authenticator;
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  /**
   * Returns if the handler is configured to support anonymous users.
   *
   * @return if the handler is configured to support anonymous users.
   */
  protected boolean getAcceptAnonymous() {
    return acceptAnonymous;
  }

  /**
   * Releases any resources initialized by the authentication handler.
   * <p>
   * This implementation does a NOP.
   */
  @Override
  public void destroy() {
  }

  /**
   * Returns the authentication type of the authentication handler, 'simple'.
   *
   * @return the authentication type of the authentication handler, 'simple'.
   */
  @Override
  public String getType() {
    return TYPE;
  }

  /**
   * This is an empty implementation, it always returns <code>TRUE</code>.
   *
   *
   *
   * @param token the authentication token if any, otherwise <code>NULL</code>.
   * @param request the HTTP client request.
   * @param response the HTTP client response.
   *
   * @return <code>TRUE</code>
   * @throws IOException it is never thrown.
   * @throws AuthenticationException it is never thrown.
   */
  @Override
  public boolean managementOperation(AuthenticationToken token,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException, AuthenticationException {
    return true;
  }

  private String getAccessKey(HttpServletRequest request) {
    List<NameValuePair> list = URLEncodedUtils.parse(request.getQueryString(), UTF8_CHARSET);
    if (list != null) {
      for (NameValuePair nv : list) {
        if ("accesskey".equals(nv.getName())) {
          return nv.getValue();
        }
      }
    }
    return null;
  }


  /**
   * Authenticates an HTTP client request.
   * <p>
   * It extracts the {@link PseudoAuthenticator#USER_NAME} parameter from the query string and creates
   * an {@link AuthenticationToken} with it.
   * <p>
   * If the HTTP client request does not contain the {@link PseudoAuthenticator#USER_NAME} parameter and
   * the handler is configured to allow anonymous users it returns the {@link AuthenticationToken#ANONYMOUS}
   * token.
   * <p>
   * If the HTTP client request does not contain the {@link PseudoAuthenticator#USER_NAME} parameter and
   * the handler is configured to disallow anonymous users it throws an {@link AuthenticationException}.
   *
   * @param request the HTTP client request.
   * @param response the HTTP client response.
   *
   * @return an authentication token if the HTTP client request is accepted and credentials are valid.
   *
   * @throws IOException thrown if an IO error occurred.
   * @throws AuthenticationException thrown if HTTP client request was not accepted as an authentication request.
   */
  @Override
  public AuthenticationToken authenticate(HttpServletRequest request, HttpServletResponse response)
      throws IOException, AuthenticationException {
    AuthenticationToken token = null;
    String accessKey = getAccessKey(request);
    if (accessKey == null) {
      if (getAcceptAnonymous()) {
        token = AuthenticationToken.ANONYMOUS;
      }
    } else {
      token = this.accessKeyAuthenticator.auth(accessKey);
    }
    if (token == null) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setHeader(WWW_AUTHENTICATE, TOKEN_AUTH);
    }
    return token;
  }
}
