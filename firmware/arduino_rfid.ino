/*
  DOC FAST 4.0
  Arduino Uno / Nano + RC522

  Fluxo real:
  - o Arduino le o UID do cartao RFID
  - o UID e enviado pela serial USB para o computador
  - o backend Java escuta a porta serial e autentica o UID

  Bibliotecas:
  - SPI
  - MFRC522

  Ligacao recomendada no Arduino Uno / Nano:
  - RC522 SDA/SS -> D10
  - RC522 RST    -> D9
  - RC522 MOSI   -> D11
  - RC522 MISO   -> D12
  - RC522 SCK    -> D13
  - RC522 3.3V   -> 3.3V
  - RC522 GND    -> GND
*/

#include <SPI.h>
#include <MFRC522.h>

#define SS_PIN 10
#define RST_PIN 9

MFRC522 mfrc522(SS_PIN, RST_PIN);
String lastUid = "";

String uidToString() {
  String uid = "";
  for (byte i = 0; i < mfrc522.uid.size; i++) {
    if (i > 0) {
      uid += ":";
    }
    if (mfrc522.uid.uidByte[i] < 0x10) {
      uid += "0";
    }
    uid += String(mfrc522.uid.uidByte[i], HEX);
  }
  uid.toUpperCase();
  return uid;
}

void setup() {
  Serial.begin(115200);
  SPI.begin();
  pinMode(SS_PIN, OUTPUT);
  digitalWrite(SS_PIN, HIGH);
  mfrc522.PCD_Init();
  mfrc522.PCD_AntennaOn();
  Serial.println("DOC FAST RFID pronto");
  mfrc522.PCD_DumpVersionToSerial();
}

void loop() {
  if (!mfrc522.PICC_IsNewCardPresent() || !mfrc522.PICC_ReadCardSerial()) {
    delay(50);
    return;
  }

  String currentUid = uidToString();
  if (currentUid != lastUid) {
    Serial.print("UID:");
    Serial.println(currentUid);
    lastUid = currentUid;
  }

  mfrc522.PICC_HaltA();
  mfrc522.PCD_StopCrypto1();
  delay(500);
}
