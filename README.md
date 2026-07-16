# DOC FAST 4.0

Plataforma industrial para digitalizacao, controle de versao e consulta de desenhos tecnicos no fluxo de producao.

## Contexto

O projeto foi pensado para o ambiente SENAI/TSEA, onde a disponibilidade rapida da documentacao tecnica no posto de trabalho e essencial para reduzir retrabalho, evitar uso de copias desatualizadas e melhorar a rastreabilidade das consultas.

## Problema

- Desenhos tecnicos e procedimentos ainda circulam em papel, com risco de versoes obsoletas.
- A consulta depende de arquivos espalhados, pouco auditaveis e sem integracao com o chao de fabrica.
- Falhas de rede e de hardware podem interromper o acesso aos documentos.

## Solucao

O DOC FAST 4.0 centraliza a documentacao tecnica e entrega acesso por RFID, com:

- controle de revisao e aprovacao
- consulta auditada e rastreavel
- visualizacao de PDFs no posto de trabalho
- operacao offline ou degradada
- integracao com Arduino, frontend web, backend Java e servicos Python
- tela inicial de bloqueio pedindo para aproximar o cartao antes de liberar o sistema

## Equipe


- Nome 1 - funcao
- Nome 2 - funcao
- Nome 3 - funcao
- Nome 4 - funcao

## Tecnologias por Camada

### Frontend

- HTML5
- CSS3 puro
- JavaScript vanilla
- PDF.js para renderizacao dos documentos
- Web Speech API para comandos de voz
- Service Worker e Cache API para modo offline
- Nenhuma biblioteca npm obrigatoria no estado atual do projeto

### Backend Java

- Java 21 LTS
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- Hibernate
- Actuator para health-check e metricas
- Flyway para migracoes do schema
- SQLite JDBC
- jSerialComm para leitura da serial do Arduino

### Backend Python

- Python 3.12+
- FastAPI
- Uvicorn para execucao local
- Nao ha outras dependencias obrigatorias no estado atual do projeto

### Banco de Dados

- SQLite para o MVP
- Caminho de migracao para PostgreSQL quando o volume de acessos crescer

### Hardware

- Arduino Uno / Nano
- Modulo RFID RC522
- Cartoes MIFARE 13.56 MHz
- Comunicacao por serial USB com o backend no PC

### Bibliotecas do Arduino

Instale no Arduino IDE:

- MFRC522 by Miguel Balboa

Ja vem com o Arduino IDE:

- SPI

## Dependencias Para Instalar

### No Arduino IDE

1. Abra `Library Manager`
2. Procure por `MFRC522`
3. Instale a biblioteca `MFRC522 by Miguel Balboa`

### No backend Java

As dependencias ja estao declaradas no `pom.xml` e sao baixadas pelo Maven automaticamente:

- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-actuator`
- `flyway-core`
- `sqlite-jdbc`
- `hibernate-community-dialects`
- `jSerialComm`

### No backend Python

Instale com:

```bash
pip install fastapi uvicorn
```

Se quiser rodar em ambiente limpo, use:

```bash
pip install -r backend-python/requirements.txt
```

## Arquitetura

```text
Operador -> Cartao RFID -> Arduino Uno/Nano -> USB Serial -> API Java -> SQLite -> Frontend -> PDF.js
                                                           |
                                                           -> API Python (busca, voz, relatorios)
```

- RFID e a autenticacao primaria.
- Login manual e fallback para casos de perda, dano no cartao ou falha do leitor.
- Comunicacao entre servicos deve usar HTTPS/TLS interno e tokens de curta duracao.

## Pre-requisitos

- Java 21
- Maven
- Python 3.12
- SQLite
- Arduino IDE
- Git
- Navegador moderno
- Biblioteca `MFRC522` no Arduino IDE

## Estrutura de Pastas

```text
/frontend
  /css
  /js
  /assets
  /pages
  sw.js

/backend-java
  /controllers
  /services
  /repositories
  /models
  /security

/backend-python
  /api
  /voice
  /ai
  /reports
  /search

/database
  database.db
  /migrations

/firmware
  arduino_rfid.ino

/docs
  manual_usuario
  manual_instalacao
  matriz_permissoes
```

## Instalacao e Execucao

### 1. Clonar o projeto

```bash
git clone <url-do-repositorio>
cd Tcc
```

### 2. Configurar o banco

- criar ou abrir o arquivo SQLite
- aplicar as migracoes em `database/migrations`

### 3. Subir o backend Java

```bash
cd backend-java
mvn spring-boot:run
```

### 4. Subir o backend Python

```bash
cd backend-python
uvicorn main:app --reload
```

### 5. Configurar o Arduino

- abrir o firmware em `firmware/arduino_rfid.ino`
- gravar o codigo no Arduino Uno ou Nano
- instalar a biblioteca `MFRC522` no Arduino IDE antes de compilar
- deixar o RC522 conectado conforme a tabela de pinos

### 6. Iniciar o frontend

- abrir `frontend/index.html` diretamente ou
- usar um servidor local, como `live-server`

## Fluxo de Uso

1. O sistema abre bloqueado na tela "Aproxime o cartao para acessar o sistema".
2. O operador aproxima o cartao RFID.
3. O Arduino envia a leitura pela serial USB para o backend Java.
4. O backend valida usuario, perfil e permissao.
5. O frontend libera as telas do sistema.
6. O usuario consulta documentos, pesquisa, voz, auditoria e configuracoes.
7. A consulta e registrada no historico.
8. Se houver falha de rede ou serial, o sistema permanece bloqueado ou degrada com seguranca.

## Reiniciar Tudo

### Parar backend e frontend antigos

```powershell
Get-CimInstance Win32_Process -Filter "Name='java.exe' OR Name='python.exe'" |
  Where-Object { $_.CommandLine -match 'backend-java|http.server 5500' } |
  ForEach-Object { Stop-Process -Id $_.ProcessId -Force }
```

### Subir backend Java

```powershell
cd C:\Users\wevertondonato\OneDrive\Documentos\Tcc\backend-java
java -jar target\backend-java-0.1.0-SNAPSHOT.jar
```

### Subir frontend

```powershell
cd C:\Users\wevertondonato\OneDrive\Documentos\Tcc\frontend
python -m http.server 5500 --bind 127.0.0.1
```

## Autenticacao RFID Real

O fluxo RFID agora usa Arduino Uno/Nano com o modulo RC522:

- o firmware le o UID do cartao
- o UID e enviado pela serial USB para o backend Java
- o backend Java responde com `authorized`, dados do usuario e mensagem
- o mesmo endpoint de autenticacao continua valido para inspecao manual e logs

### UID de teste

- `04:A1:B2:C3:D4` -> operador demo
- `11:22:33:44:55` -> supervisor demo

### Como testar sem hardware

1. Suba o backend Java.
2. Abra o frontend.
3. Informe o UID em `UID do cartao`.
4. Clique em `Validar UID`.

### Como testar com o Arduino

1. Abra `firmware/arduino_rfid.ino` no Arduino IDE.
2. Grave o firmware no Arduino Uno ou Nano com o RC522 conectado.
3. Configure o backend Java para ler a porta serial.
4. Deixe o backend lendo a porta e nao abra o Monitor Serial ao mesmo tempo.
5. Aproxime um cartao cadastrado e confira o evento no backend.

### Ligacao fisica do RC522 no Arduino Uno / Nano

Use alimentacao em 3.3V, nunca 5V.

| RC522 | Arduino |
|---|---|
| SDA / SS | D10 |
| SCK | D13 |
| MOSI | D11 |
| MISO | D12 |
| RST | D9 |
| 3.3V | 3V3 |
| GND | GND |

Se o seu modulo tiver pinos diferentes, mantenha o SPI padrao do Arduino e altere apenas `SS_PIN` e `RST_PIN` se necessario.

### O backend precisa estar rodando no PC

No arquivo `backend-java/src/main/resources/application.yml`, o backend ja fica ouvindo em `0.0.0.0:8080`.
O Arduino nao fala direto com a internet; o backend Java precisa ler a porta serial USB do Arduino.

### Ativando a leitura serial

No arquivo `backend-java/src/main/resources/application.yml`, ajuste:

```yaml
rfid:
  serial:
    enabled: true
    port: COM3
    baud-rate: 115200
```

Troque `COM3` pela porta que aparece no Windows.

### O que conferir se nao funcionar

- O Arduino precisa estar conectado por cabo USB ao computador.
- O backend Java precisa estar com `rfid.serial.enabled=true` e a porta correta.
- A porta serial nao pode estar aberta ao mesmo tempo no Monitor Serial e no backend.
- O Windows precisa reconhecer a porta COM.
- A porta `8080` precisa estar liberada no firewall do Windows se voce usar o frontend no navegador.
- O RC522 precisa estar alimentado em 3.3V.
- Os cabos SPI precisam estar corretos.
- O serial do Arduino deve mostrar `UID:...` quando o cartao for aproximado.

## Objetivo do Projeto

Entregar um sistema industrial moderno, seguro e resiliente para demonstrar desenvolvimento web, Java, Python, RFID, IoT, acessibilidade e conceitos de Industria 4.0 aplicados ao chao de fabrica.

## Documentacao de Instalacao

Veja o passo a passo completo em [docs/manual_instalacao_execucao.md](docs/manual_instalacao_execucao.md).
