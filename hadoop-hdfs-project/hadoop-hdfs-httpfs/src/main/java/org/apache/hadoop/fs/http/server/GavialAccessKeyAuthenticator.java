package org.apache.hadoop.fs.http.server;

import org.apache.hadoop.security.authentication.server.AccessKeyAuthenticationHandler;
import org.apache.hadoop.security.authentication.server.AccessKeyAuthenticator;
import org.apache.hadoop.security.authentication.server.AuthenticationToken;

public class GavialAccessKeyAuthenticator implements AccessKeyAuthenticator {

  @Override
  public AuthenticationToken auth(String accesskey) {
    if(accesskey==null||!accesskey.equals("abc")) {
      return null;
    }
    AuthenticationToken token = new AuthenticationToken("tiger","tiger",
        AccessKeyAuthenticationHandler.TYPE);
    return token;
  }

  @Override
  public void init() {

  }

  @Override
  public void destroy() {

  }
}
