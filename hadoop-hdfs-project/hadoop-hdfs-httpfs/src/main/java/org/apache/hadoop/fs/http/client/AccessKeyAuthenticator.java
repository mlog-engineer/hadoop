package org.apache.hadoop.fs.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.authentication.client.AuthenticatedURL;
import org.apache.hadoop.security.authentication.client.AuthenticatedURL.Token;
import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.apache.hadoop.security.authentication.client.Authenticator;
import org.apache.hadoop.security.authentication.client.ConnectionConfigurator;

public class AccessKeyAuthenticator implements Authenticator {

  /**
   * Name of the additional parameter that carries the 'user.name' value.
   */
  public static final String ACCESSKEY = "accesskey";

  private static final String ACCESSKEY_EQ = ACCESSKEY + "=";

  private ConnectionConfigurator connConfigurator;

  private Configuration configuration;

  public AccessKeyAuthenticator() {

  }

  public AccessKeyAuthenticator(Configuration configuration) {
    this.configuration = configuration;
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * Sets a {@link ConnectionConfigurator} instance to use for configuring connections.
   *
   * @param configurator the {@link ConnectionConfigurator} instance.
   */
  @Override
  public void setConnectionConfigurator(ConnectionConfigurator configurator) {
    connConfigurator = configurator;
  }

  @Override
  public void authenticate(URL url, Token token) throws IOException, AuthenticationException {
    if (!token.isSet()) {
      String strUrl = url.toString();
      String paramSeparator = (strUrl.contains("?")) ? "&" : "?";
      strUrl += paramSeparator + ACCESSKEY_EQ + getAccesskey();
      url = new URL(strUrl);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      if (connConfigurator != null) {
        conn = connConfigurator.configure(conn);
      }
      conn.setRequestMethod("OPTIONS");
      conn.connect();
      AuthenticatedURL.extractToken(conn, token);
    }
  }

  private String getAccesskey() {
    return configuration.get("httpfs.authentication.accesskey", "anonymous");
  }
}
