function formatAgo(ts, language) {
  if (!ts) return '';
  const diff = Math.max(0, Date.now() - (typeof ts === 'string' ? new Date(ts).getTime() : ts));
  const m = Math.floor(diff / 60000);
  const h = Math.floor(m / 60);
  const d = Math.floor(h / 24);
  if (language === 'ru') {
    if (m < 1) return 'только что';
    if (m < 60) return `${m} мин`;
    if (h < 24) return `${h} ч`;
    return `${d} дн`;
  }
  if (m < 1) return 'just now';
  if (m < 60) return `${m}m`;
  if (h < 24) return `${h}h`;
  return `${d}d`;
}

function renderSidebar() {
  const { history, activeHistoryId, language, theme, historyHasMore } = state;
  const T = STRINGS[language];

  const historyItems = history.map(h => `
    <div class="history-item${activeHistoryId === h.id ? ' active' : ''}" data-id="${h.id}">
      <button class="h-body" onclick="pickHistory('${h.id}')">
        <div class="h-q">${escHtml(h.q)}</div>
        <div class="h-meta">
          <span>${formatAgo(h.ts, language)}</span>
          ${h.winner ? `<span style="opacity:0.5">·</span><span class="h-winner">${h.winner}</span>` : ''}
        </div>
      </button>
      <button class="h-del" onclick="onDeleteHistory('${h.id}')" title="${language === 'ru' ? 'Удалить' : 'Delete'}">×</button>
    </div>
  `).join('');

  const loadMore = historyHasMore
    ? `<button style="margin:4px 8px 0;padding:7px;border-radius:7px;border:1px solid var(--border);background:none;color:var(--text-faint);font-size:12px;cursor:pointer;width:calc(100% - 16px)" onclick="loadMoreHistory()">
        ${language === 'ru' ? 'Загрузить ещё' : 'Load more'}
       </button>`
    : '';

  return `
    <aside class="sidebar">
      <button class="new-btn" onclick="onNewChat()">
        <span>${T.new_chat}</span>
      </button>
      <div class="history-label">${T.history}</div>
      <div class="history">
        ${history.length === 0 ? `<div style="padding:8px 12px;font-size:12px;color:var(--text-faint)">${language === 'ru' ? 'Пока пусто' : 'Empty'}</div>` : ''}
        ${historyItems}
        ${loadMore}
      </div>
      <div class="sidebar-footer">
        <div class="theme-toggle" role="tablist">
          <button class="${theme === 'light' ? 'active' : ''}" onclick="setTheme('light')" aria-label="Light">${icon('sun', 13)}</button>
          <button class="${theme === 'dark' ? 'active' : ''}" onclick="setTheme('dark')" aria-label="Dark">${icon('moon', 13)}</button>
        </div>
        <button class="lang-toggle" onclick="toggleLanguage()">${language === 'ru' ? 'RU' : 'EN'}</button>
        <button class="lang-toggle" style="margin-left:4px" onclick="logout()" title="${language === 'ru' ? 'Выйти' : 'Logout'}">↪</button>
      </div>
    </aside>
  `;
}
