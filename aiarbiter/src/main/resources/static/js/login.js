document.addEventListener('DOMContentLoaded', () => {
  const params = new URLSearchParams(location.search);
  if (params.has('logout')) {
    showMsg('You have been logged out', 'success');
    history.replaceState(null, '', location.pathname);
  }
});

function switchTab(tab) {
  document.getElementById('form-login').style.display = tab === 'login' ? 'flex' : 'none';
  document.getElementById('form-register').style.display = tab === 'register' ? 'flex' : 'none';
  document.getElementById('tab-login').classList.toggle('active', tab === 'login');
  document.getElementById('tab-register').classList.toggle('active', tab === 'register');
  hideMsg();
}

function showMsg(text, type) {
  const el = document.getElementById('msg');
  el.textContent = text;
  el.className = 'msg show ' + type;
}

function hideMsg() {
  document.getElementById('msg').className = 'msg';
}

async function handleLogin(e) {
  e.preventDefault();
  const form = e.target;
  const btn = form.querySelector('button');
  hideMsg();

  if (!/^[A-Za-z0-9_]+$/.test(form.username.value)) {
    showMsg('Username must contain only Latin letters, digits and underscores', 'error');
    return;
  }

  btn.disabled = true;

  const body = new URLSearchParams();
  body.append('username', form.username.value);
  body.append('password', form.password.value);

  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: body.toString(),
      redirect: 'follow',
    });
    if (res.ok) {
      window.location.href = '/';
    } else {
      showMsg('Invalid login or password', 'error');
    }
  } catch {
    showMsg('Connection error', 'error');
  } finally {
    btn.disabled = false;
  }
}

async function handleRegister(e) {
  e.preventDefault();
  const form = e.target;
  const btn = form.querySelector('button');
  hideMsg();

  if (!/^[A-Za-z0-9_]+$/.test(form.username.value)) {
    showMsg('Username must contain only Latin letters, digits and underscores', 'error');
    return;
  }
  if (!/^[A-Za-z0-9!@#$%^&*()_+\-=\[\]{};':"|,.<>\/?`~\\]+$/.test(form.password.value)) {
    showMsg('Password must contain only Latin letters, digits and special characters', 'error');
    return;
  }

  btn.disabled = true;

  try {
    const res = await fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: form.username.value,
        password: form.password.value,
      }),
    });
    if (res.ok) {
      showMsg('Account created! Please log in.', 'success');
      form.reset();
      setTimeout(() => switchTab('login'), 1200);
    } else {
      const data = await res.json().catch(() => ({}));
      showMsg(data.detail || 'Registration error', 'error');
    }
  } catch {
    showMsg('Connection error', 'error');
  } finally {
    btn.disabled = false;
  }
}
