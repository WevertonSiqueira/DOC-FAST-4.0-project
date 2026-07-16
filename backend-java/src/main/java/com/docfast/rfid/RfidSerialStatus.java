package com.docfast.rfid;

public record RfidSerialStatus(
    boolean enabled,
    boolean running,
    boolean portOpen,
    String port,
    int baudRate,
    String lastError,
    String lastSerialLine,
    RfidEvent lastEvent) {
}
