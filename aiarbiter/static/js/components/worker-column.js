function renderWorkerColumn(side, meta, body, status, language) {
  const T = STRINGS[language];
  const MO = MODEL_OPTIONS;
  const m = (meta.selectedModelKey && MO[meta.selectedModelKey]) || { color: meta.color || '#888', glyph: meta.glyph || '?', name: meta.name || '' };
  const displayName = meta.name || m.name;
  const isWinner = state.phase === 'done' && state.winner === side;

  let bodyHtml = '';
  if (!body && status === 'idle') {
    bodyHtml = `<div style="color:var(--text-faint);font-size:13px">—</div>`;
  } else if (!body && status === 'thinking') {
    bodyHtml = `<div class="skeleton">
      <div class="skeleton-line" style="width:85%"></div>
      <div class="skeleton-line" style="width:92%"></div>
      <div class="skeleton-line" style="width:70%"></div>
      <div class="skeleton-line" style="width:88%"></div>
    </div>`;
  } else if (body) {
    const html = DOMPurify.sanitize(marked.parse(body));
    bodyHtml = `<div class="md-body">${html}</div>`;
  }

  return `
    <div class="col${isWinner ? ' winner' : ''}">
      <div class="col-head">
        <span class="side-tag">${side}</span>
        <div class="model-meta">
          <div class="model-logo" style="background:${m.color}">${m.glyph}</div>
          <div class="model-name">${escHtml(displayName)}</div>
        </div>
      </div>
      <div class="col-body${!body && status === 'idle' ? ' empty' : ''}" id="col-body-${side}">
        ${bodyHtml}
      </div>
    </div>
  `;
}
