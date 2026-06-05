function escHtml(str) {
  if (!str) return '';
  return String(str).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

function render() {
  const { phase, language, submittedQ, jStatus, aStatus, bStatus, aBody, bBody, aMeta, bMeta, error, theme } = state;
  const T = STRINGS[language];

  // Sidebar
  document.getElementById('sidebar-root').innerHTML = renderSidebar();

  // Topbar
  const topbarTitle = submittedQ || (language === 'ru' ? 'Новый запрос' : 'New query');
  let topbarSub = '';
  if (phase === 'running') {
    topbarSub = (jStatus === 'idle' || jStatus === 'thinking') ? T.askingTwo : T.judging;
  } else if (phase === 'done') {
    topbarSub = language === 'ru' ? `Готово · вердикт от ${T.judge}` : `Done · verdict by ${T.judge}`;
  } else {
    topbarSub = language === 'ru' ? 'Введите запрос' : 'Ready';
  }
  document.getElementById('topbar-title').textContent = topbarTitle;
  document.getElementById('topbar-sub').textContent = topbarSub;

  // Composer
  document.getElementById('composer-root').innerHTML = renderComposer();
  // Restore textarea value and attach listener
  const ta = document.getElementById('question-input');
  if (ta) {
    ta.value = state.question;
    ta.addEventListener('input', e => { state.question = e.target.value; syncComposerBtn(); autoResizeTextarea(ta); });
    ta.addEventListener('keydown', e => { if (e.key === 'Enter' && (e.metaKey || e.ctrlKey)) { e.preventDefault(); submitQuestion(); } });
    autoResizeTextarea(ta);
  }

  // Error
  const errEl = document.getElementById('error-banner');
  if (errEl) { errEl.textContent = error; errEl.style.display = error ? 'block' : 'none'; }

  // Empty state
  const emptyEl = document.getElementById('empty-state');
  if (emptyEl) {
    emptyEl.style.display = phase === 'idle' && !error ? 'block' : 'none';
    const emptyTitle = document.getElementById('empty-title');
    const emptyBody = document.getElementById('empty-body');
    if (emptyTitle) emptyTitle.textContent = T.empty.title;
    if (emptyBody) emptyBody.textContent = T.empty.body;
  }

  // Section labels
  const workersLabel = document.getElementById('workers-label');
  const verdictLabel = document.getElementById('verdict-label');
  if (workersLabel) workersLabel.textContent = T.workers;
  if (verdictLabel) verdictLabel.textContent = T.verdict;

  // Results
  const resultsEl = document.getElementById('results-root');
  if (resultsEl) {
    if (phase !== 'idle') {
      resultsEl.style.display = 'block';
      const aMetaFull = { ...aMeta, selectedModelKey: aMeta.selectedModelKey || state.selectedModels.workerA };
      const bMetaFull = { ...bMeta, selectedModelKey: bMeta.selectedModelKey || state.selectedModels.workerB };
      document.getElementById('workers-root').innerHTML =
        `<div class="diff">
          ${renderWorkerColumn('A', aMetaFull, aBody, aStatus, language)}
          ${renderWorkerColumn('B', bMetaFull, bBody, bStatus, language)}
        </div>`;
      document.getElementById('verdict-root').innerHTML = renderVerdict();
      // Highlight code blocks
      document.querySelectorAll('.md-body pre code, #verdict-body pre code').forEach(el => hljs.highlightElement(el));
    } else {
      resultsEl.style.display = 'none';
    }
  }
}

function syncComposerBtn() {
  const btn = document.querySelector('.ask-btn');
  if (btn) btn.disabled = state.phase === 'running' || !state.question.trim();
}

function autoResizeTextarea(el) {
  el.style.height = 'auto';
  el.style.height = Math.min(el.scrollHeight, 240) + 'px';
}
