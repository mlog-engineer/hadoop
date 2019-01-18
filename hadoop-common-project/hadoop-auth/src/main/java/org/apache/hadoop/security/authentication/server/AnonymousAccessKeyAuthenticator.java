package org.apache.hadoop.security.authentication.server;

public class AnonymousAccessKeyAuthenticator implements AccessKeyAuthenticator {

  @Override
  public AuthenticationToken auth(String accesskey) {
    AuthenticationToken token = new AuthenticationToken("anonymous","anonymous",
        AccessKeyAuthenticationHandler.TYPE);
    return token;
  }

  @Override
  public void init() {
    //nothing to do
  }

  @Override
  public void destroy() {
    //nothing to do
  }
}
