package com.docfast.auth;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RfidAuthService {

  private final Map<String, RfidUserProfile> allowedCards = new HashMap<>();

  public RfidAuthService() {
    allowedCards.put("04:A1:B2:C3:D4", new RfidUserProfile(
        1L,
        "Operador Demo",
        "0001",
        "Montagem",
        "operador",
        "04:A1:B2:C3:D4",
        List.of("consultar:setor-proprio")));

    allowedCards.put("11:22:33:44:55", new RfidUserProfile(
        2L,
        "Supervisor Demo",
        "0002",
        "Qualidade",
        "supervisor",
        "11:22:33:44:55",
        List.of("consultar:setor-proprio", "consultar:setores-externos", "aprovar:documento")));
  }

  public RfidAuthResponse verify(RfidAuthRequest request) {
    String normalizedUid = normalizeUid(request.uid());
    RfidUserProfile profile = allowedCards.get(normalizedUid);

    if (profile == null) {
      return RfidAuthResponse.denied(
          normalizedUid,
          request.deviceId(),
          "Cartao RFID nao cadastrado",
          OffsetDateTime.now().toString());
    }

    return RfidAuthResponse.allowed(
        normalizedUid,
        request.deviceId(),
        profile,
        "Acesso autorizado",
        OffsetDateTime.now().toString());
  }

  private String normalizeUid(String uid) {
    if (uid == null) {
      return "";
    }
    return uid.trim().toUpperCase().replace(" ", "");
  }
}

