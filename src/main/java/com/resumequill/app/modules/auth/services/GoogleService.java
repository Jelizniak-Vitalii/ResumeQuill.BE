package com.resumequill.app.modules.auth.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.resumequill.app.common.exceptions.UnauthorizedException;
import com.resumequill.app.modules.auth.models.GoogleUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleService {
  private static final Logger logger = LoggerFactory.getLogger(GoogleService.class);
  private final GoogleIdTokenVerifier verifier;

  public GoogleService(@Value("${google.client.id}") String clientId) {
    this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
      .setAudience(Collections.singletonList(clientId))
      .build();
  }

  public GoogleUserInfo verifyIdToken(String idTokenString) {
    try {
      GoogleIdToken idToken = verifier.verify(idTokenString);

      if (idToken == null) {
        logger.error("Invalid Google ID token");
        throw new UnauthorizedException("Invalid Google ID token");
      }

      GoogleIdToken.Payload payload = idToken.getPayload();

      return new GoogleUserInfo(
        payload.getSubject(),
        payload.getEmail(),
        (String) payload.get("given_name"),
        (String) payload.get("family_name"),
        (String) payload.get("picture")
      );
    } catch (UnauthorizedException e) {
      throw e;
    } catch (Exception e) {
      logger.error("Error verifying Google ID token", e);
      throw new UnauthorizedException("Failed to verify Google ID token");
    }
  }
}
