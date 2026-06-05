function loadTweaks() {
  try {
    const saved = JSON.parse(localStorage.getItem('arbiter_tweaks') || '{}');
    if (saved.theme) state.theme = saved.theme;
    if (saved.language) state.language = saved.language;
    if (saved.accentHue) state.accentHue = saved.accentHue;
  } catch {}
}

function saveTweaks() {
  try {
    localStorage.setItem('arbiter_tweaks', JSON.stringify({
      theme: state.theme,
      language: state.language,
      accentHue: state.accentHue,
    }));
  } catch {}
}

function applyTheme() {
  const { theme, accentHue } = state;
  document.documentElement.dataset.theme = theme;
  document.documentElement.style.setProperty('--accent', `oklch(0.55 0.16 ${accentHue})`);
  document.documentElement.style.setProperty('--accent-soft',
    theme === 'dark' ? `oklch(0.30 0.08 ${accentHue})` : `oklch(0.95 0.04 ${accentHue})`);
  document.documentElement.style.setProperty('--accent-text',
    theme === 'dark' ? `oklch(0.82 0.13 ${accentHue})` : `oklch(0.40 0.18 ${accentHue})`);
  const hljsTheme = document.getElementById('hljs-theme');
  if (hljsTheme) {
    hljsTheme.href = theme === 'dark'
      ? 'https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github-dark.min.css'
      : 'https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github.min.css';
  }
}

function setTheme(theme) {
  state.theme = theme;
  applyTheme();
  saveTweaks();
  render();
}

function toggleLanguage() {
  state.language = state.language === 'ru' ? 'en' : 'ru';
  saveTweaks();
  render();
}
