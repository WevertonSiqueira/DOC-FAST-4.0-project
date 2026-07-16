package com.docfast.web;

import com.docfast.auth.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @GetMapping("/api/health")
  public ApiResponse health() {
    return ApiResponse.ok("Doc Fast API online");
  }
}

