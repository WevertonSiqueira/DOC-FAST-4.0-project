# Manual de Instalacao e Execucao

Este guia mostra como colocar o DOC FAST 4.0 para rodar em qualquer maquina Windows com Arduino Uno/Nano + RC522.

## 1. Pre-requisitos

Instale no computador:

- Java 21
- Arduino IDE 2.x
- Python 3.x para servir o frontend localmente, se desejar
- Navegador moderno, como Chrome ou Edge
- Biblioteca `MFRC522` no Arduino IDE

Opcional, mas util:

- Maven, se voce quiser recompilar o backend

## 2. Hardware

### Componentes

- Arduino Uno ou Nano
- RC522
- Cartao RFID MIFARE 13.56 MHz
- Cabo USB do Arduino
- Jumpers macho-macho

### Ligacao do RC522

| RC522 | Arduino Uno/Nano |
|---|---|
| SDA / SS | D10 |
| SCK | D13 |
| MOSI | D11 |
| MISO | D12 |
| RST | D9 |
| 3.3V | 3.3V |
| GND | GND |

Importante:

- o RC522 deve ser alimentado em 3.3V
- nao use 5V

## 3. Gravar o Arduino

1. Abra o arquivo `firmware/arduino_rfid.ino`
2. No Arduino IDE, selecione a placa correta:
   - `Arduino Uno` ou `Arduino Nano`
3. No Library Manager, instale a biblioteca `MFRC522 by Miguel Balboa`
4. Selecione a porta COM do Arduino
5. Grave o sketch
6. Abra o Monitor Serial em `115200`

Quando um cartão for aproximado, o Arduino deve mostrar algo como:

```text
UID:04:A1:B2:C3:D4
```

## 4. Configurar o backend Java

O backend lê a serial do Arduino.

Edite `backend-java/src/main/resources/application.yml`:

```yaml
rfid:
  serial:
    enabled: true
    port: COM3
    baud-rate: 115200
```

Troque `COM3` pela porta real do Arduino.

## 5. Como descobrir a porta COM

No Windows:

1. Abra o Gerenciador de Dispositivos
2. Vá em `Portas (COM e LPT)`
3. Veja a entrada do Arduino

Exemplo:

- `Arduino Uno (COM3)`

## 6. Subir o backend

### Se voce tiver o jar pronto

Use este comando:

```powershell
cd C:\Users\wevertondonato\OneDrive\Documentos\Tcc\backend-java
java -jar target\backend-java-0.1.0-SNAPSHOT.jar
```

### Se voce quiser recompilar

Se o Maven estiver instalado:

```powershell
cd C:\Users\wevertondonato\OneDrive\Documentos\Tcc\backend-java
mvn spring-boot:run
```

### Dependencias Java usadas pelo projeto

O Maven baixa automaticamente as bibliotecas declaradas no `pom.xml`:

- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-actuator`
- `flyway-core`
- `sqlite-jdbc`
- `hibernate-community-dialects`
- `jSerialComm`

## 7. Subir o frontend

Nao use `file://` como primeira opcao. Rode via servidor local:

```powershell
cd C:\Users\wevertondonato\OneDrive\Documentos\Tcc\frontend
python -m http.server 5500
```

Depois abra:

- `http://localhost:5500`

### Dependencias do frontend

O frontend atual nao exige `npm install`.
Ele usa apenas:

- HTML
- CSS
- JavaScript puro
- APIs nativas do navegador, como `fetch`, `Service Worker` e `Cache API`

## 8. Ordem correta de inicializacao

1. Conecte o Arduino no USB
2. Grave o sketch no Arduino
3. Feche o Serial Monitor do Arduino IDE
4. Suba o backend Java
5. Suba o frontend
6. Aproxime o cartao

## 9. O que voce deve ver

### No backend

- `Doc Fast API online`
- leitura serial RFID iniciada

### No frontend

- tela de bloqueio com a mensagem `Aproxime o cartão para acessar o sistema`
- depois, ao ler um cartão autorizado, as telas principais sao liberadas

## 10. UID de teste

O backend de demonstracao aceita:

- `04:A1:B2:C3:D4`
- `11:22:33:44:55`

## 11. Problemas comuns

### Backend nao sobe

- verifique se a porta `8080` nao esta ocupada
- se estiver, feche o processo Java antigo

### Arduino nao envia UID

- confira a ligacao do RC522
- confira se o Monitor Serial nao esta aberto ao mesmo tempo que o backend
- confirme o baud `115200`

### Frontend fica em offline

- confirme se o backend respondeu em `http://localhost:8080/api/health`
- recarregue a pagina depois de subir o backend

### Arduino compila, mas nao le o cartao

- confirme se a biblioteca `MFRC522` esta instalada no Arduino IDE
- confirme a ligacao do RC522 em `3.3V`, `GND`, `D9`, `D10`, `D11`, `D12`, `D13`
- teste com um cartao MIFARE 13.56 MHz
- confirme se o Monitor Serial nao esta travando a porta `COM3`

### Porta COM errada

- ajuste `rfid.serial.port` para a porta correta no `application.yml`

## 12. Validacao rapida

Para validar em pouco tempo:

1. Abra `http://localhost:8080/api/health`
2. Abra `http://localhost:5500`
3. Aproxime o cartao no RC522
4. Veja o UID lido no backend
5. Veja a liberacao das telas no frontend

## 13. Bibliotecas para instalar por parte

### Arduino IDE

- `MFRC522 by Miguel Balboa`
- `SPI` ja vem junto com o Arduino IDE

### Backend Java

Nao instalar manualmente. O Maven baixa:

- Spring Boot
- JPA
- Security
- Actuator
- Flyway
- SQLite JDBC
- jSerialComm

### Backend Python

Instale com:

```powershell
pip install fastapi uvicorn
```

Ou:

```powershell
pip install -r backend-python/requirements.txt
```

## 14. Como reiniciar tudo

### Parar os processos atuais

Use este bloco no PowerShell:

```powershell
Get-CimInstance Win32_Process -Filter "Name='java.exe' OR Name='python.exe'" |
  Where-Object { $_.CommandLine -match 'backend-java|http.server 5500' } |
  ForEach-Object { Stop-Process -Id $_.ProcessId -Force }
```

### Subir o backend Java de novo

```powershell
cd C:\Users\wevertondonato\OneDrive\Documentos\Tcc\backend-java
java -jar target\backend-java-0.1.0-SNAPSHOT.jar
```

### Subir o frontend de novo

```powershell
cd C:\Users\wevertondonato\OneDrive\Documentos\Tcc\frontend
python -m http.server 5500 --bind 127.0.0.1
```

### Ordem correta

1. Feche o Monitor Serial do Arduino IDE
2. Pare o backend e o frontend antigos
3. Suba o backend Java
4. Suba o frontend
5. Abra `http://localhost:5500`
6. Aproxime o cartao
