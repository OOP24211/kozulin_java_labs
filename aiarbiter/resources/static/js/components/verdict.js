function renderVerdict() {
  const { jBody, jStatus, winner, language } = state;
  const T = STRINGS[language];

  let titleText = '';
  if (jStatus === 'thinking') titleText = T.judging;
  else if (jStatus === 'done') titleText = winner ? (language === 'ru' ? `Победитель — Воркер ${winner}` : `Winner — Worker ${winner}`) : (language === 'ru' ? 'Победитель не определён' : 'No winner');
  else titleText = language === 'ru' ? 'Ожидание ответов' : 'Awaiting answers';

  let bodyHtml = '';
  if (jStatus === 'thinking') {
    bodyHtml = `<div class="skeleton" style="max-width:520px">
      <div class="skeleton-line" style="width:90%"></div>
      <div class="skeleton-line" style="width:75%"></div>
      <div class="skeleton-line" style="width:85%"></div>
    </div>`;
  } else if (jBody) {
    const html = DOMPurify.sanitize(marked.parse(jBody));
    bodyHtml = `<div class="verdict-body" id="verdict-body">${html}</div>`;
  }

  const winnerBadge = jStatus === 'done' && winner ? `
    <div class="winner-badge">
      <span class="badge-letter">${winner}</span>
      <span>${T.winner}</span>
    </div>` : '';

  return `
    <div class="verdict">
      <div class="verdict-head">
        <div class="verdict-icon">${icon('gavel', 18)}</div>
        <div class="verdict-title-row">
          <div class="verdict-eyebrow">${T.verdictEyebrow} · ${T.judge}</div>
          <div class="verdict-title">${titleText}</div>
        </div>
        ${winnerBadge}
      </div>
      ${bodyHtml}
    </div>
  `;
}
