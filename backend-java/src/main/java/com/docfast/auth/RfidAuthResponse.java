package com.docfast.auth;

public record RfidAuthResponse(
    boolean authorized,
    String uid,
    String deviceId,
    String message,
    String timestamp,
    RfidUserProfile user) {

  public static RfidAuthResponse allowed(String uid, String deviceId, RfidUserProfile user, String message, String timestamp) {
    return new RfidAuthResponse(true, uid, deviceId, message, timestamp, user);
  }

  public static RfidAuthResponse denied(String uid, String deviceId, String message, String timestamp) {
    return new RfidAuthResponse(false, uid, deviceId, message, timestamp, null);
  }
}

