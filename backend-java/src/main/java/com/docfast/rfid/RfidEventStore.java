package com.docfast.rfid;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RfidEventStore {

  private final AtomicReference<RfidEvent> lastEvent = new AtomicReference<>();
  private final AtomicBoolean running = new AtomicBoolean(false);
  private final AtomicBoolean portOpen = new AtomicBoolean(false);
  private final AtomicReference<String> lastError = new AtomicReference<>(null);
  private final AtomicReference<String> lastSerialLine = new AtomicReference<>(null);

  public void update(RfidEvent event) {
    lastEvent.set(event);
  }

  public RfidEvent getLastEvent() {
    return lastEvent.get();
  }

  public void setRunning(boolean value) {
    running.set(value);
  }

  public boolean isRunning() {
    return running.get();
  }

  public void setPortOpen(boolean value) {
    portOpen.set(value);
  }

  public boolean isPortOpen() {
    return portOpen.get();
  }

  public void setLastError(String value) {
    lastError.set(value);
  }

  public String getLastError() {
    return lastError.get();
  }

  public void setLastSerialLine(String value) {
    lastSerialLine.set(value);
  }

  public String getLastSerialLine() {
    return lastSerialLine.get();
  }
}
