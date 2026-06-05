function icon(name, size = 16) {
  const a = `width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"`;
  switch (name) {
    case 'plus':    return `<svg ${a}><path d="M12 5v14M5 12h14"/></svg>`;
    case 'sun':     return `<svg ${a}><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41"/></svg>`;
    case 'moon':    return `<svg ${a}><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>`;
    case 'gavel':   return `<svg ${a}><path d="M14 13l-7.5 7.5a2.12 2.12 0 0 1-3-3L11 10M16 4l4 4M14 6l4 4M9 11l4 4M2 22h16"/></svg>`;
    case 'check':   return `<svg ${a}><path d="M20 6L9 17l-5-5"/></svg>`;
    case 'clock':   return `<svg ${a}><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></svg>`;
    case 'hash':    return `<svg ${a}><path d="M4 9h16M4 15h16M10 3L8 21M16 3l-2 18"/></svg>`;
    case 'zap':     return `<svg ${a}><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/></svg>`;
    case 'copy':    return `<svg ${a}><rect x="9" y="9" width="13" height="13" rx="2"/><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/></svg>`;
    case 'share':   return `<svg ${a}><path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8M16 6l-4-4-4 4M12 2v13"/></svg>`;
    case 'trash':   return `<svg ${a}><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6M10 11v6M14 11v6M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>`;
    default: return '';
  }
}
