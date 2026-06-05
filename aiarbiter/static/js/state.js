const state = {
  phase: 'idle',       // idle | running | done
  language: 'ru',
  theme: 'dark',
  accentHue: 264,
  question: '',
  submittedQ: '',
  activeHistoryId: null,
  history: [],
  historyPage: 0,
  historyHasMore: false,
  selectedModels: { ...MODEL_DEFAULTS },
  aStatus: 'idle', bStatus: 'idle', jStatus: 'idle',
  aBody: '', bBody: '', jBody: '',
  aMeta: {}, bMeta: {},
  winner: null,
  error: '',
};

function setState(patch) {
  Object.assign(state, patch);
  render();
}

function resetResult() {
  setState({
    phase: 'idle',
    activeHistoryId: null,
    aStatus: 'idle', bStatus: 'idle', jStatus: 'idle',
    aBody: '', bBody: '', jBody: '',
    aMeta: {}, bMeta: {},
    winner: null,
    error: '',
    submittedQ: '',
  });
}
