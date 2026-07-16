package com.docfast.auth;

public record ApiResponse(boolean ok, String message) {
  public static ApiResponse ok(String message) {
    return new ApiResponse(true, message);
  }
}

