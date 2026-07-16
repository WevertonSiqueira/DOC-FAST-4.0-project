package com.docfast;

import com.docfast.rfid.RfidSerialProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RfidSerialProperties.class)
public class DocFastApplication {

  public static void main(String[] args) {
    SpringApplication.run(DocFastApplication.class, args);
  }
}
