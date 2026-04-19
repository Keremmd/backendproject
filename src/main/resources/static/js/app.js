/* EgeQuiz — küçük yardımcı betikler. Framework yok, vanilla JS. */

// Tema (light/dark) toggle — tercih localStorage'a saklanır.
(function initTheme() {
  const stored = localStorage.getItem('egequiz-theme');
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
  const theme = stored || (prefersDark ? 'dark' : 'light');
  document.documentElement.setAttribute('data-theme', theme);

  document.addEventListener('DOMContentLoaded', () => {
    const btn = document.getElementById('themeToggle');
    if (!btn) return;
    btn.addEventListener('click', () => {
      const cur = document.documentElement.getAttribute('data-theme');
      const next = cur === 'dark' ? 'light' : 'dark';
      document.documentElement.setAttribute('data-theme', next);
      localStorage.setItem('egequiz-theme', next);
    });
  });
})();

/**
 * Emoji picker:
 *  - .emoji-picker container'ında data-target="#hiddenInputId" niteliği olur.
 *  - Her .emoji-option butonunda data-emoji="<emoji>" niteliği vardır.
 *  - Tıklayınca seçim hidden input'a yazılır + görsel olarak işaretlenir.
 *  - Yanındaki .emoji-picker-current .preview bölümü güncellenir.
 */
document.addEventListener('click', (e) => {
  const btn = e.target.closest('.emoji-option');
  if (!btn) return;
  e.preventDefault();
  const picker = btn.closest('.emoji-picker');
  const targetSel = picker?.dataset.target;
  picker.querySelectorAll('.emoji-option').forEach((b) => b.classList.remove('selected'));
  btn.classList.add('selected');
  const emoji = btn.dataset.emoji;
  if (targetSel) {
    const hidden = document.querySelector(targetSel);
    if (hidden) hidden.value = emoji;
  }
  const preview = picker.parentElement?.querySelector('.emoji-picker-current .preview');
  if (preview) preview.textContent = emoji;
});

// Quiz şıkları seçildiğinde container'a is-checked class ekle (estetik için).
document.addEventListener('change', (e) => {
  if (e.target && e.target.matches('input[type="radio"][name^="q_"]')) {
    const name = e.target.name;
    document.querySelectorAll(`input[name="${name}"]`).forEach((r) => {
      const label = r.closest('label');
      if (label) label.classList.toggle('is-checked', r.checked);
    });
  }
});

/**
 * Geri sayım sayacı.
 *  - data-timer="<saniye>" niteliği olan elemanı arar.
 *  - Süre bittiğinde "data-timer-form" id'li formu otomatik gönderir.
 *  - Gizli #duration alanına geçen süreyi yazar (sunucu kayıt için).
 */
(function initTimer() {
  document.addEventListener('DOMContentLoaded', () => {
    const el = document.querySelector('[data-timer]');
    if (!el) return;
    const total = parseInt(el.getAttribute('data-timer'), 10);
    let remaining = total;
    const formId = el.getAttribute('data-timer-form');
    const form = formId ? document.getElementById(formId) : null;
    const durationField = document.getElementById('duration');

    function fmt(s) {
      const m = Math.floor(s / 60).toString().padStart(2, '0');
      const ss = (s % 60).toString().padStart(2, '0');
      return `${m}:${ss}`;
    }

    function tick() {
      el.textContent = '⏱ ' + fmt(remaining);
      el.classList.toggle('warn',  remaining <= 60 && remaining > 15);
      el.classList.toggle('danger', remaining <= 15);
      // İlerleme barı varsa güncelle
      const bar = document.getElementById('quizProgressBar');
      if (bar) bar.style.width = Math.min(100, ((total - remaining) / total) * 100) + '%';
      if (durationField) durationField.value = String(total - remaining);

      if (remaining <= 0) {
        if (form) form.submit();
        return;
      }
      remaining -= 1;
      setTimeout(tick, 1000);
    }
    tick();
  });
})();
