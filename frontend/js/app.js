const accessGate = document.getElementById("accessGate");
const appShell = document.getElementById("appShell");
const connectionStatus = document.getElementById("connectionStatus");
const gateConnectionStatus = document.getElementById("gateConnectionStatus");
const rfidStatus = document.getElementById("rfidStatus");
const gateRfidStatus = document.getElementById("gateRfidStatus");
const dashboardMessage = document.getElementById("dashboardMessage");
const currentUserName = document.getElementById("currentUserName");
const currentUserMatricula = document.getElementById("currentUserMatricula");
const currentUserSector = document.getElementById("currentUserSector");
const currentUserRole = document.getElementById("currentUserRole");
const toggleOffline = document.getElementById("toggleOffline");
const lockSystem = document.getElementById("lockSystem");
const uidForm = document.getElementById("uidForm");
const uidInput = document.getElementById("uidInput");
const authResult = document.getElementById("authResult");
const lastUidStatus = document.getElementById("lastUidStatus");
const lastLineStatus = document.getElementById("lastLineStatus");
const apiUrlInput = document.getElementById("apiUrl");
const testButton = document.getElementById("testBackend");
const navButtons = document.querySelectorAll(".nav-btn");
const screens = document.querySelectorAll(".screen");

let isOffline = false;
let backendOnline = false;
let backendKnown = false;
let unlockedUser = null;
let gatePollingTimer = null;
let healthPollingTimer = null;
let healthFailures = 0;

function renderConnectionState() {
  const online = backendOnline && !isOffline;
  const statusLabel = !backendKnown
    ? "Conectando"
    : online
      ? "Online"
      : "Offline";
  const backendLabel = !backendKnown
    ? "Verificando backend"
    : backendOnline
      ? "Backend online"
      : "Backend offline";
  const rfidLabel = unlockedUser
    ? "Autenticado"
    : !backendKnown
      ? "Verificando conexão"
      : backendOnline
        ? "Aguardando cartão"
        : "Backend offline";

  connectionStatus.textContent = statusLabel;
  connectionStatus.style.color = online ? "var(--ok)" : "var(--warn)";
  gateConnectionStatus.textContent = backendLabel;
  gateConnectionStatus.style.color = backendOnline ? "var(--ok)" : "var(--warn)";
  rfidStatus.textContent = rfidLabel;
  gateRfidStatus.textContent = unlockedUser
    ? "Acesso liberado"
    : !backendKnown
      ? "Verificando conexão"
      : backendOnline
        ? "Aguardando cartão"
        : "Backend offline";
}

function setUnlockedUser(user) {
  unlockedUser = user;
  currentUserName.textContent = user?.nome || "Operador";
  currentUserMatricula.textContent = user?.matricula || "-";
  currentUserSector.textContent = user?.setor || "-";
  currentUserRole.textContent = user?.perfil || "-";
  dashboardMessage.textContent = `Bem-vindo, ${user?.nome || "operador"}. O sistema está liberado.`;
  accessGate.classList.add("access-gate--hidden");
  appShell.classList.remove("app-shell--locked");
  renderConnectionState();
}

function lockSystemView(message = "Encoste o cartão para acessar o sistema.") {
  unlockedUser = null;
  currentUserName.textContent = "Não autenticado";
  currentUserMatricula.textContent = "-";
  currentUserSector.textContent = "-";
  currentUserRole.textContent = "-";
  dashboardMessage.textContent = message;
  accessGate.classList.remove("access-gate--hidden");
  appShell.classList.add("app-shell--locked");
  authResult.textContent = message;
  renderConnectionState();
}

function getApiUrl() {
  return (apiUrlInput?.value || "http://localhost:8080").replace(/\/$/, "");
}

async function fetchJson(url, options) {
  const response = await fetch(url, options);
  if (response.status === 204) {
    return null;
  }
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  return response.json();
}

async function verifyRfid(uid) {
  return fetchJson(`${getApiUrl()}/api/auth/rfid/verify`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ uid, deviceId: "browser-test" })
  });
}

async function readLastEvent() {
  return fetchJson(`${getApiUrl()}/api/rfid/last-event`);
}

async function checkBackendHealth() {
  try {
    await fetchJson(`${getApiUrl()}/api/health`);
    backendOnline = true;
    healthFailures = 0;
    backendKnown = true;
  } catch {
    healthFailures += 1;
    backendKnown = true;
    if (healthFailures >= 3) {
      backendOnline = false;
    }
  }
  renderConnectionState();
}

async function checkRfidStatus() {
  if (!backendOnline) {
    return;
  }
  try {
    const status = await fetchJson(`${getApiUrl()}/api/rfid/status`);
    const serialOk = status?.running && status?.portOpen && !status?.lastError;
    gateRfidStatus.textContent = serialOk
      ? "Leitor RFID pronto"
      : status?.lastError
        ? `RFID: ${status.lastError}`
        : "Aguardando cartão";
    if (status?.lastSerialLine) {
      lastLineStatus.textContent = `Última linha serial: ${status.lastSerialLine}`;
    }
    if (status?.lastEvent?.uid) {
      lastUidStatus.textContent = `Último UID: ${status.lastEvent.uid}`;
    }
    if (status?.lastEvent?.response?.user) {
      setUnlockedUser(status.lastEvent.response.user);
      authResult.textContent = `Autorizado via RFID: ${status.lastEvent.response.user.nome}`;
    } else if (status?.lastEvent && !status?.lastEvent?.authorized) {
      authResult.textContent = `Negado: ${status.lastEvent.message || "cartão não autorizado"}`;
    }
  } catch {
    gateRfidStatus.textContent = "RFID indisponível";
  }
}

function showScreen(screenName) {
  screens.forEach((screen) => {
    screen.classList.toggle("screen--active", screen.dataset.screen === screenName);
  });
  navButtons.forEach((button) => {
    button.classList.toggle("nav-btn--active", button.dataset.screen === screenName);
  });
}

function startPolling() {
  stopPolling();
  gatePollingTimer = window.setInterval(async () => {
    if (isOffline || !backendOnline) {
      return;
    }
    try {
      const event = await readLastEvent();
      renderConnectionState();
      if (event?.authorized && event?.response?.user) {
        setUnlockedUser(event.response.user);
        authResult.textContent = `Autorizado via RFID: ${event.response.user.nome}`;
      }
    } catch {
      healthFailures += 1;
      if (healthFailures >= 3) {
        backendOnline = false;
      }
      renderConnectionState();
    }
  }, 1500);
}

function stopPolling() {
  if (gatePollingTimer) {
    window.clearInterval(gatePollingTimer);
    gatePollingTimer = null;
  }
}

uidForm?.addEventListener("submit", async (event) => {
  event.preventDefault();
  authResult.textContent = "Validando...";

  try {
    const result = await verifyRfid(uidInput.value);
    if (result.authorized && result.user) {
      setUnlockedUser(result.user);
      authResult.textContent = `Autorizado: ${result.user.nome} (${result.user.perfil})`;
    } else {
      authResult.textContent = `Negado: ${result.message}`;
      lockSystemView("Cartão não autorizado. Aproxime um cartão válido.");
    }
    backendOnline = true;
    renderConnectionState();
  } catch {
    authResult.textContent = "Falha ao conectar com o backend Java.";
    backendOnline = false;
    renderConnectionState();
  }
});

testButton?.addEventListener("click", async () => {
  uidInput.value = "04:A1:B2:C3:D4";
  uidForm?.requestSubmit();
});

toggleOffline?.addEventListener("click", () => {
  isOffline = !isOffline;
  if (isOffline) {
    authResult.textContent = "Modo offline ativado.";
  }
  renderConnectionState();
});

lockSystem?.addEventListener("click", () => {
  lockSystemView();
});

navButtons.forEach((button) => {
  button.addEventListener("click", () => showScreen(button.dataset.screen));
});

if ("serviceWorker" in navigator) {
  window.addEventListener("load", () => {
    navigator.serviceWorker.register("./sw.js").catch(() => {
      console.warn("Service worker nao registrado.");
    });
  });
}

lockSystemView();
renderConnectionState();
checkBackendHealth().then(() => {
  healthPollingTimer = window.setInterval(checkBackendHealth, 3000);
  window.setInterval(checkRfidStatus, 2000);
  startPolling();
});
