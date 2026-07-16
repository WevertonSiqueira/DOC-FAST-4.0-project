package com.docfast.rfid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/rfid")
public class RfidEventController {

  private final RfidEventStore eventStore;

  public RfidEventController(RfidEventStore eventStore) {
    this.eventStore = eventStore;
  }

  @GetMapping("/last-event")
  public ResponseEntity<RfidEvent> lastEvent() {
    RfidEvent event = eventStore.getLastEvent();
    if (event == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(event);
  }

  @GetMapping("/status")
  public RfidSerialStatus status() {
    return new RfidSerialStatus(
        true,
        eventStore.isRunning(),
        eventStore.isPortOpen(),
        "COM3",
        115200,
        eventStore.getLastError(),
        eventStore.getLastSerialLine(),
        eventStore.getLastEvent());
  }
}
