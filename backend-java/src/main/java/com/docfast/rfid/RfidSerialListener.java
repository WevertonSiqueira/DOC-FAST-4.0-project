package com.docfast.rfid;

import com.docfast.auth.RfidAuthRequest;
import com.docfast.auth.RfidAuthResponse;
import com.docfast.auth.RfidAuthService;
import com.fazecast.jSerialComm.SerialPort;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

@Component
public class RfidSerialListener {

  private static final Logger log = LoggerFactory.getLogger(RfidSerialListener.class);

  private final RfidSerialProperties properties;
  private final RfidAuthService authService;
  private final RfidEventStore eventStore;

  private volatile boolean running;
  private Thread worker;
  private SerialPort serialPort;

  public RfidSerialListener(RfidSerialProperties properties, RfidAuthService authService, RfidEventStore eventStore) {
    this.properties = properties;
    this.authService = authService;
    this.eventStore = eventStore;
  }

  @PostConstruct
  public void start() {
    if (!properties.isEnabled()) {
      log.info("Leitura serial RFID desativada. Habilite rfid.serial.enabled=true para usar o Arduino.");
      eventStore.setRunning(false);
      eventStore.setPortOpen(false);
      eventStore.setLastError("RFID serial desativado");
      return;
    }

    serialPort = SerialPort.getCommPort(properties.getPort());
    serialPort.setBaudRate(properties.getBaudRate());
    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);

    if (!serialPort.openPort()) {
      log.error("Nao foi possivel abrir a porta serial {}", properties.getPort());
      eventStore.setRunning(false);
      eventStore.setPortOpen(false);
      eventStore.setLastError("Nao foi possivel abrir a porta " + properties.getPort());
      return;
    }

    eventStore.setRunning(true);
    eventStore.setPortOpen(true);
    eventStore.setLastError(null);
    running = true;
    worker = new Thread(this::readLoop, "rfid-serial-listener");
    worker.setDaemon(true);
    worker.start();
    log.info("Leitura serial RFID iniciada em {}", properties.getPort());
  }

  private void readLoop() {
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(serialPort.getInputStream(), StandardCharsets.UTF_8))) {
      String line;
      while (running && (line = reader.readLine()) != null) {
        handleLine(line.trim());
      }
    } catch (Exception ex) {
      if (running) {
        log.error("Falha na leitura serial RFID", ex);
        eventStore.setLastError(ex.getMessage());
      }
    } finally {
      if (serialPort != null && serialPort.isOpen()) {
        serialPort.closePort();
      }
      eventStore.setPortOpen(false);
      eventStore.setRunning(false);
    }
  }

  private void handleLine(String line) {
    if (!line.isBlank()) {
      eventStore.setLastSerialLine(line);
    }

    if (line.isBlank() || !line.startsWith("UID:")) {
      return;
    }

    String uid = line.substring(4).trim();
    if (uid.isEmpty()) {
      return;
    }

    RfidAuthResponse response = authService.verify(new RfidAuthRequest(uid, "arduino-usb"));
    eventStore.update(new RfidEvent(
        uid,
        "arduino-usb",
        response.authorized(),
        response.message(),
        OffsetDateTime.now().toString(),
        response));

    log.info("UID {} -> {}", uid, response.authorized() ? "AUTORIZADO" : "NEGADO");
  }

  @PreDestroy
  public void stop() {
    running = false;
    if (serialPort != null && serialPort.isOpen()) {
      serialPort.closePort();
    }
    eventStore.setRunning(false);
    eventStore.setPortOpen(false);
  }
}
