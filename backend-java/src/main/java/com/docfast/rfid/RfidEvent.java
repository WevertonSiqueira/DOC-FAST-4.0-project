package com.docfast.rfid;

import com.docfast.auth.RfidAuthResponse;

public record RfidEvent(
    String uid,
    String source,
    boolean authorized,
    String message,
    String timestamp,
    RfidAuthResponse response) {
}
