package org.apache.hadoop.fs.http.client;

import org.apache.hadoop.security.authentication.client.Authenticator;
import org.apache.hadoop.security.token.delegation.web.DelegationTokenAuthenticator;

public class AccessKeyDelegationTokenAuthenticator extends DelegationTokenAuthenticator {

  public AccessKeyDelegationTokenAuthenticator(
      Authenticator authenticator) {
    super(authenticator);
  }
}
