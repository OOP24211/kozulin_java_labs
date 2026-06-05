async function runArbiter(task, models) {
  const res = await fetch('/api/run', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ task, workerA: models.workerA, workerB: models.workerB, judge: models.judge }),
  });
  if (res.status === 401) { window.location.href = '/login.html'; return null; }
  if (!res.ok) throw new Error('Сервер вернул ' + res.status);
  return res.json();
}

async function loadHistory(page = 0) {
  const res = await fetch(`/api/history?page=${page}`);
  if (res.status === 401) { window.location.href = '/login.html'; return []; }
  return res.json();
}

async function deleteHistory(id) {
  await fetch(`/api/history/${id}`, { method: 'DELETE' });
}

async function loadMoreHistory() {
  const nextPage = state.historyPage + 1;
  const data = await loadHistory(nextPage);
  const arr = Array.isArray(data) ? data : [];
  setState({
    history: [...state.history, ...arr],
    historyHasMore: arr.length === 30,
    historyPage: nextPage,
  });
}

function detectWinner(verdict) {
  if (!verdict) return null;
  const v = verdict.toLowerCase();
  if (/(победитель|winner)[\s\S]{0,30}[аa]/i.test(v)) return 'A';
  if (/(победитель|winner)[\s\S]{0,30}[бb]/i.test(v)) return 'B';
  return null;
}

async function submitQuestion() {
  const task = state.question.trim();
  if (!task || state.phase === 'running') return;

  const models = state.selectedModels;
  const MO = MODEL_OPTIONS;

  setState({
    phase: 'running',
    submittedQ: task,
    activeHistoryId: null,
    error: '',
    aBody: '', bBody: '', jBody: '',
    aStatus: 'thinking', bStatus: 'thinking', jStatus: 'thinking',
    aMeta: { name: MO[models.workerA]?.name || '', color: MO[models.workerA]?.color, glyph: MO[models.workerA]?.glyph, selectedModelKey: models.workerA },
    bMeta: { name: MO[models.workerB]?.name || '', color: MO[models.workerB]?.color, glyph: MO[models.workerB]?.glyph, selectedModelKey: models.workerB },
    winner: null,
  });

  const t0 = performance.now();
  try {
    const data = await runArbiter(task, models);
    if (!data) return;
    const elapsed = Math.round(performance.now() - t0);
    const w = detectWinner(data.verdict || '');

    setState({
      phase: 'done',
      aStatus: 'done', bStatus: 'done', jStatus: 'done',
      aBody: data.workerAResponse || '',
      bBody: data.workerBResponse || '',
      jBody: data.verdict || '',
      aMeta: { name: data.workerAName || '', color: MO[models.workerA]?.color, glyph: MO[models.workerA]?.glyph, tokens: data.workerATokens, latencyMs: elapsed },
      bMeta: { name: data.workerBName || '', color: MO[models.workerB]?.color, glyph: MO[models.workerB]?.glyph, tokens: data.workerBTokens, latencyMs: elapsed },
      winner: w,
      history: [{
        id: String(Date.now()),
        q: task,
        ts: new Date().toISOString(),
        winner: w,
        a: { name: data.workerAName, body: data.workerAResponse },
        b: { name: data.workerBName, body: data.workerBResponse },
        verdict: data.verdict,
        latencyMs: elapsed,
      }, ...state.history].slice(0, 30),
    });
  } catch (e) {
    setState({
      error: 'Ошибка: ' + e.message,
      aStatus: 'idle', bStatus: 'idle', jStatus: 'idle',
      phase: 'idle',
    });
  }
}
