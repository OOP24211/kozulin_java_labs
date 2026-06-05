function renderComposer() {
  const { question, phase, selectedModels, language } = state;
  const T = STRINGS[language];
  const MO = MODEL_OPTIONS;
  const firstModel = Object.values(MO)[0];
  const mA = MO[selectedModels.workerA] || firstModel;
  const mB = MO[selectedModels.workerB] || firstModel;
  const mJ = MO[selectedModels.judge]   || firstModel;
  const disabled = phase === 'running';

  const modelOptions = Object.entries(MO).map(([k, m]) =>
    `<option value="${k}">${escHtml(m.name)}</option>`
  ).join('');

  const presets = T.presets.map(p =>
    `<button class="preset" onclick="setQuestion(${JSON.stringify(p)})">${escHtml(p)}</button>`
  ).join('');

  return `
    <div>
      <div class="composer">
        <div class="model-selector-row">
          <label class="model-field">
            <span class="model-field-label">${T.worker_a_model}</span>
            <select class="model-select" onchange="setModel('workerA', this.value)">
              ${Object.entries(MO).map(([k, m]) => `<option value="${k}"${selectedModels.workerA === k ? ' selected' : ''}>${escHtml(m.name)}</option>`).join('')}
            </select>
          </label>
          <label class="model-field">
            <span class="model-field-label">${T.worker_b_model}</span>
            <select class="model-select" onchange="setModel('workerB', this.value)">
              ${Object.entries(MO).map(([k, m]) => `<option value="${k}"${selectedModels.workerB === k ? ' selected' : ''}>${escHtml(m.name)}</option>`).join('')}
            </select>
          </label>
          <label class="model-field">
            <span class="model-field-label">${T.judge_model}</span>
            <select class="model-select" onchange="setModel('judge', this.value)">
              ${Object.entries(MO).map(([k, m]) => `<option value="${k}"${selectedModels.judge === k ? ' selected' : ''}>${escHtml(m.name)}</option>`).join('')}
            </select>
          </label>
        </div>
        <textarea id="question-input"
          placeholder="${escHtml(T.placeholder)}"
          rows="2"
          ${disabled ? 'disabled' : ''}>${escHtml(question)}</textarea>
        <div class="composer-foot">
          <span class="model-pill"><span class="dot" style="background:${mA.color}"></span>${escHtml(mA.name)}</span>
          <span class="model-pill"><span class="dot" style="background:${mB.color}"></span>${escHtml(mB.name)}</span>
          <span class="model-pill">${icon('gavel', 11)}<span style="margin-left:2px">${escHtml(mJ.name)}</span></span>
          <button class="ask-btn" onclick="submitQuestion()" ${disabled || !question.trim() ? 'disabled' : ''}>
            ${T.ask}
          </button>
        </div>
      </div>
      <div class="presets">${presets}</div>
    </div>
  `;
}
