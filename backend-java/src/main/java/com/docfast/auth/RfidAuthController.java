package com.docfast.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/rfid")
public class RfidAuthController {

  private final RfidAuthService rfidAuthService;

  public RfidAuthController(RfidAuthService rfidAuthService) {
    this.rfidAuthService = rfidAuthService;
  }

  @GetMapping("/health")
  public ResponseEntity<ApiResponse> health() {
    return ResponseEntity.ok(ApiResponse.ok("RFID auth service ready"));
  }

  @PostMapping("/verify")
  public ResponseEntity<RfidAuthResponse> verify(@RequestBody RfidAuthRequest request) {
    return ResponseEntity.ok(rfidAuthService.verify(request));
  }
}

