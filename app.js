// Autosugestión FunTaDeHu - App Logic

const DEFAULT_PHRASES = [
  "Todos los días, en todos los sentidos, estoy mejor, mejor y mejor.",
  "Para ser eficaz y feliz, mantengo mi mente serena, enfocada y en paz.",
  "Tengo fe inquebrantable en mi capacidad de lograr cada una de mis metas.",
  "Mi mente subconsciente acepta pensamientos de éxito, salud y bienestar.",
  "Cada desafío es una oportunidad para crecer con sabiduría y determinación.",
  "Acepto lo que no puedo cambiar y actúo con energía en lo que sí controlo.",
  "Soy dueño de mis pensamientos y elijo cultivar armonía y seguridad.",
  "Merezco el éxito, la felicidad y la tranquilidad en todo lo que hago."
];

// State
let phrases = [];
let isRestMode = false;
let selectedInterval = 30; // minutes
let timerId = null;
let countdownIntervalId = null;
let nextNotificationTime = null;
let deferredPrompt = null;

// Initialize
window.addEventListener('DOMContentLoaded', () => {
  loadState();
  initServiceWorker();
  initInstallPrompt();
  initNetworkStatus();
  renderPhrases();
  updateUI();
  removeInjectedBadges();

  if (!isRestMode) {
    scheduleNextNotification();
  }
});

function removeInjectedBadges() {
  const clean = () => {
    document.querySelectorAll('[class*="netlify"], [id*="netlify"], [data-netlify-deploy-id], iframe[src*="netlify"], a[href*="netlify.com"]').forEach((el) => {
      try { el.remove(); } catch (e) {}
    });
    document.querySelectorAll('a, p, span, div, footer').forEach((el) => {
      if (el.textContent && el.textContent.toLowerCase().includes('powered by netlify')) {
        try { (el.closest('div') || el).remove(); } catch (e) {}
      }
    });
  };
  clean();
  setTimeout(clean, 500);
  setTimeout(clean, 1500);
  setTimeout(clean, 3000);

  // Observador en tiempo real para eliminar cualquier inyección tardía
  try {
    const observer = new MutationObserver((mutations) => {
      let shouldClean = false;
      for (const m of mutations) {
        if (m.addedNodes.length > 0) {
          shouldClean = true;
          break;
        }
      }
      if (shouldClean) clean();
    });
    observer.observe(document.body, { childList: true, subtree: true });
  } catch (e) {}
}

// PWA Install Prompt handling
function initInstallPrompt() {
  const isStandalone = window.matchMedia('(display-mode: standalone)').matches || window.navigator.standalone;
  const installCard = document.getElementById('installCard');

  if (isStandalone && installCard) {
    installCard.style.display = 'none'; // Already installed!
    return;
  }

  window.addEventListener('beforeinstallprompt', (e) => {
    // Prevent the mini-infobar from appearing on mobile
    e.preventDefault();
    deferredPrompt = e;
    if (installCard) installCard.style.display = 'block';
  });

  window.addEventListener('appinstalled', () => {
    deferredPrompt = null;
    if (installCard) installCard.style.display = 'none';
    showToast('🎉 ¡App instalada con éxito en tu teléfono!');
  });
}

function handleInstallClick() {
  if (deferredPrompt) {
    deferredPrompt.prompt();
    deferredPrompt.userChoice.then((choiceResult) => {
      if (choiceResult.outcome === 'accepted') {
        showToast('Instalando aplicación...');
      }
      deferredPrompt = null;
    });
  } else {
    // Detect iOS / Safari
    const isIOS = /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream;
    if (isIOS) {
      alert('Para instalar en iPhone/iPad:\n1. Toca el botón Compartir (el cuadrado con la flecha arriba ⎋).\n2. Selecciona "Agregar al inicio".');
    } else {
      alert('Para instalar en Android:\nToca los tres puntos (⋮) en la esquina superior derecha de tu navegador y elige "Instalar aplicación" o "Agregar a la pantalla principal".');
    }
  }
}

// Network status for 100% offline support
function initNetworkStatus() {
  const offlineBadge = document.getElementById('offlineBadge');
  function updateOnlineStatus() {
    if (!navigator.onLine) {
      if (offlineBadge) offlineBadge.style.display = 'inline-block';
      showToast('📶 Modo Offline: La app sigue funcionando 100% sin internet');
    } else {
      if (offlineBadge) offlineBadge.style.display = 'none';
    }
  }

  window.addEventListener('online', updateOnlineStatus);
  window.addEventListener('offline', updateOnlineStatus);
  updateOnlineStatus();
}

// Load state from localStorage
function loadState() {
  const savedPhrases = localStorage.getItem('funtadehu_phrases');
  if (savedPhrases) {
    try {
      phrases = JSON.parse(savedPhrases);
      if (!Array.isArray(phrases) || phrases.length === 0) {
        phrases = [...DEFAULT_PHRASES];
      }
    } catch (e) {
      phrases = [...DEFAULT_PHRASES];
    }
  } else {
    phrases = [...DEFAULT_PHRASES];
    savePhrases();
  }

  const savedRest = localStorage.getItem('funtadehu_rest_mode');
  isRestMode = savedRest === 'true';

  const savedInterval = localStorage.getItem('funtadehu_interval');
  if (savedInterval) {
    selectedInterval = savedInterval === 'test' ? 'test' : parseInt(savedInterval, 10);
    const select = document.getElementById('intervalSelect');
    if (select) select.value = savedInterval;
  }
}

function savePhrases() {
  localStorage.setItem('funtadehu_phrases', JSON.stringify(phrases));
}

function saveRestMode() {
  localStorage.setItem('funtadehu_rest_mode', isRestMode.toString());
}

// Service Worker for Notifications
function initServiceWorker() {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('./sw.js', { scope: './' })
      .then((reg) => {
        console.log('Service worker registered successfully:', reg.scope);
        reg.update();
      })
      .catch((err) => {
        console.warn('Service worker registration note:', err);
      });
  }

  const isLocal = ['localhost', '127.0.0.1'].includes(window.location.hostname);
  const localCard = document.getElementById('localNetworkCard');
  if (localCard) localCard.style.display = isLocal ? 'block' : 'none';
}

// UI Updates
function updateUI() {
  const badge = document.getElementById('statusBadge');
  const statusText = document.getElementById('statusText');
  const statusDesc = document.getElementById('statusDesc');
  const nextTimer = document.getElementById('nextTimer');

  if (isRestMode) {
    badge.className = 'status-badge resting';
    statusText.textContent = 'Modo Descanso (Pausado)';
    statusDesc.textContent = 'El funcionamiento y las notificaciones están desactivadas para que descanses tranquilo.';
    nextTimer.style.display = 'none';
  } else {
    badge.className = 'status-badge active';
    statusText.textContent = 'Modo Activo';
    statusDesc.textContent = 'El sistema está activo. Recibirás frases de autosugestión aleatorias a lo largo del día.';
    nextTimer.style.display = 'block';
  }

  document.getElementById('phrasesCount').textContent = phrases.length;
}

// Toggle Rest Mode
function toggleRestMode(rest) {
  isRestMode = rest;
  saveRestMode();
  updateUI();

  if (isRestMode) {
    clearTimeout(timerId);
    clearInterval(countdownIntervalId);
    showToast('⏸️ Modo Descanso activado. Notificaciones pausadas.');
  } else {
    scheduleNextNotification();
    showToast('▶️ Reactivado. Recibirás frases aleatorias.');
    requestNotificationPermission();
  }
}

// Change Interval
function changeInterval(val) {
  selectedInterval = val === 'test' ? 'test' : parseInt(val, 10);
  localStorage.setItem('funtadehu_interval', val);
  if (!isRestMode) {
    scheduleNextNotification();
    showToast('⏱️ Frecuencia actualizada.');
  }
}

// Scheduling
function scheduleNextNotification() {
  clearTimeout(timerId);
  clearInterval(countdownIntervalId);

  if (isRestMode) return;

  let delayMs = 0;
  if (selectedInterval === 'test') {
    // 30 seconds for test mode
    delayMs = 30 * 1000;
  } else {
    // Random jitter around selected interval (+/- 30%)
    const baseMinutes = selectedInterval;
    const minMinutes = Math.max(5, Math.floor(baseMinutes * 0.7));
    const maxMinutes = Math.floor(baseMinutes * 1.3);
    const randomMinutes = Math.floor(Math.random() * (maxMinutes - minMinutes + 1)) + minMinutes;
    delayMs = randomMinutes * 60 * 1000;
  }

  nextNotificationTime = Date.now() + delayMs;
  updateCountdown();
  countdownIntervalId = setInterval(updateCountdown, 1000);

  timerId = setTimeout(() => {
    deliverRandomPhrase();
    scheduleNextNotification(); // Schedule next loop
  }, delayMs);
}

function updateCountdown() {
  if (isRestMode || !nextNotificationTime) return;
  const remainingMs = Math.max(0, nextNotificationTime - Date.now());
  const totalSeconds = Math.floor(remainingMs / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  const formatted = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
  
  const el = document.getElementById('timerCountdown');
  if (el) el.textContent = formatted;
}

// Pick and Deliver Random Phrase
function deliverRandomPhrase() {
  if (phrases.length === 0) return;
  const randomIndex = Math.floor(Math.random() * phrases.length);
  const phrase = phrases[randomIndex];

  // Update current quote display
  const quoteEl = document.getElementById('currentQuote');
  if (quoteEl) quoteEl.textContent = `"${phrase}"`;

  // Play audio chime
  playGentleChime();

  // Show push notification
  showNotification('Autosugestión FunTaDeHu ✨', phrase);
}

// Request permission
async function requestNotificationPermission() {
  if ('Notification' in window && Notification.permission === 'default') {
    try {
      await Notification.requestPermission();
    } catch (e) {
      console.warn('Error requesting notification permission:', e);
    }
  }
}

// Show Notification (Handles both Desktop, Mobile Browser & Service Worker)
function showNotification(title, body) {
  if (!('Notification' in window)) {
    showToast(`✨ ${body}`);
    return;
  }

  if (Notification.permission === 'granted') {
    if (navigator.serviceWorker && navigator.serviceWorker.controller) {
      navigator.serviceWorker.controller.postMessage({
        type: 'SHOW_NOTIFICATION',
        title: title,
        body: body,
        icon: './logo.png'
      });
    } else {
      try {
        new Notification(title, {
          body: body,
          icon: './logo.png',
          tag: 'autosuggestion-phrase'
        });
      } catch (err) {
        // In some mobile browsers, new Notification outside Service Worker throws
        navigator.serviceWorker.ready.then((reg) => {
          reg.showNotification(title, {
            body: body,
            icon: './logo.png'
          });
        });
      }
    }
  } else if (Notification.permission !== 'denied') {
    Notification.requestPermission().then((permission) => {
      if (permission === 'granted') {
        showNotification(title, body);
      } else {
        showToast(`✨ ${body}`);
      }
    });
  } else {
    // If blocked, fallback to toast
    showToast(`✨ ${body}`);
  }
}

// Test Notification button
function triggerTestNotification() {
  requestNotificationPermission().then(() => {
    deliverRandomPhrase();
    showToast('🔔 Notificación de prueba enviada.');
  });
}

// Gentle 2-tone melodic chime synthesizer via Web Audio API
function playGentleChime() {
  try {
    const AudioCtx = window.AudioContext || window.webkitAudioContext;
    if (!AudioCtx) return;
    const ctx = new AudioCtx();

    // Notes: C5 (523.25Hz) and G5 (783.99Hz)
    const notes = [523.25, 783.99];
    notes.forEach((freq, idx) => {
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();

      const startTime = ctx.currentTime + (idx * 0.18);
      const endTime = startTime + 0.6;

      osc.type = 'sine';
      osc.frequency.setValueAtTime(freq, startTime);

      gain.gain.setValueAtTime(0, startTime);
      gain.gain.linearRampToValueAtTime(0.2, startTime + 0.05);
      gain.gain.exponentialRampToValueAtTime(0.001, endTime);

      osc.connect(gain);
      gain.connect(ctx.destination);

      osc.start(startTime);
      osc.stop(endTime);
    });
  } catch (e) {
    // Audio context may require user interaction first
  }
}

// Phrases List Management
function renderPhrases() {
  const container = document.getElementById('phrasesList');
  if (!container) return;
  container.innerHTML = '';

  phrases.forEach((phrase, index) => {
    const item = document.createElement('div');
    item.className = 'phrase-item';
    item.innerHTML = `
      <span class="phrase-text">${escapeHtml(phrase)}</span>
      <button class="btn-delete" title="Eliminar frase" onclick="deletePhrase(${index})">🗑️</button>
    `;
    container.appendChild(item);
  });

  const countEl = document.getElementById('phrasesCount');
  if (countEl) countEl.textContent = phrases.length;
}

function deletePhrase(index) {
  if (phrases.length <= 1) {
    showToast('⚠️ Debes mantener al menos una frase en la lista.');
    return;
  }
  const deleted = phrases.splice(index, 1);
  savePhrases();
  renderPhrases();
  showToast('Frase eliminada.');
}

// Modal handling
function openAddModal() {
  const modal = document.getElementById('addModal');
  const input = document.getElementById('newPhraseInput');
  input.value = '';
  modal.classList.add('open');
  input.focus();
}

function closeAddModal() {
  const modal = document.getElementById('addModal');
  modal.classList.remove('open');
}

function saveNewPhrase() {
  const input = document.getElementById('newPhraseInput');
  const text = input.value.trim();
  if (!text) {
    showToast('Por favor escribe una frase válida.');
    return;
  }

  phrases.unshift(text);
  savePhrases();
  renderPhrases();
  closeAddModal();
  showToast('✅ ¡Nueva frase agregada a la lista!');

  // Immediately display as current quote
  const quoteEl = document.getElementById('currentQuote');
  if (quoteEl) quoteEl.textContent = `"${text}"`;
}

// Helper: Toast
let toastTimeout = null;
function showToast(msg) {
  const toast = document.getElementById('toast');
  if (!toast) return;
  toast.textContent = msg;
  toast.classList.add('show');
  clearTimeout(toastTimeout);
  toastTimeout = setTimeout(() => {
    toast.classList.remove('show');
  }, 3200);
}

function escapeHtml(str) {
  return str.replace(/[&<>'"]/g, 
    tag => ({
      '&': '&amp;',
      '<': '&lt;',
      '>': '&gt;',
      "'": '&#39;',
      '"': '&quot;'
    }[tag] || tag)
  );
}

