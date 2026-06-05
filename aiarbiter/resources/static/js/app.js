function onNewChat() {
  state.question = '';
  resetResult();
}

function setQuestion(q) {
  state.question = q;
  render();
  const ta = document.getElementById('question-input');
  if (ta) { ta.value = q; autoResizeTextarea(ta); }
}

function setModel(role, value) {
  state.selectedModels = { ...state.selectedModels, [role]: value };
  try { localStorage.setItem('arbiter_models', JSON.stringify(state.selectedModels)); } catch {}
}

function pickHistory(id) {
  const item = state.history.find(h => String(h.id) === String(id));
  if (!item) return;
  setState({
    activeHistoryId: id,
    question: item.q,
    submittedQ: item.q,
    error: '',
    phase: 'done',
    aStatus: 'done', bStatus: 'done', jStatus: 'done',
    aBody: item.a?.body || '',
    bBody: item.b?.body || '',
    jBody: item.verdict || '',
    aMeta: { name: item.a?.name || '', latencyMs: item.latencyMs },
    bMeta: { name: item.b?.name || '', latencyMs: item.latencyMs },
    winner: item.winner || detectWinner(item.verdict) || null,
  });
}

async function onDeleteHistory(id) {
  await deleteHistory(id);
  state.history = state.history.filter(h => String(h.id) !== String(id));
  if (state.activeHistoryId === id) resetResult();
  else render();
}

async function logout() {
  await fetch('/api/auth/logout', { method: 'POST' });
  window.location.href = '/login.html?logout=true';
}

async function init() {
  // Load persisted tweaks and model selection
  loadTweaks();
  applyTheme();
  try {
    const saved = JSON.parse(localStorage.getItem('arbiter_models') || '{}');
    const MO = MODEL_OPTIONS;
    state.selectedModels = {
      workerA: (saved.workerA && MO[saved.workerA]) ? saved.workerA : MODEL_DEFAULTS.workerA,
      workerB: (saved.workerB && MO[saved.workerB]) ? saved.workerB : MODEL_DEFAULTS.workerB,
      judge:   (saved.judge   && MO[saved.judge])   ? saved.judge   : MODEL_DEFAULTS.judge,
    };
  } catch {}

  // Initial render
  render();

  // Load history from server
  try {
    const data = await loadHistory(0);
    const arr = Array.isArray(data) ? data : [];
    state.history = arr.map(h => ({ ...h, winner: h.winner || detectWinner(h.verdict) || null }));
    state.historyHasMore = arr.length === 30;
    state.historyPage = 0;
    if (arr.length > 0) pickHistory(arr[0].id);
    else render();
  } catch {}
}

document.addEventListener('DOMContentLoaded', init);
