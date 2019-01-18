package org.apache.hadoop.security.authentication.server;

public interface AccessKeyAuthenticator {

  public AuthenticationToken auth(String accesskey);

  public void init();

  public void destroy();

}
