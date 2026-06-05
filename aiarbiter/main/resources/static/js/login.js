const params = new URLSearchParams(location.search);
if (params.has('error')) showMsg('Неверный логин или пароль', 'error');
if (params.has('logout')) showMsg('Вы вышли из системы', 'success');

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
  btn.disabled = true;
  hideMsg();

  const body = new URLSearchParams();
  body.append('username', form.username.value);
  body.append('password', form.password.value);

  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: body.toString(),
      redirect: 'manual',
    });
    if (res.status === 200 || res.status === 302 || res.type === 'opaqueredirect') {
      window.location.href = '/';
    } else {
      showMsg('Неверный логин или пароль', 'error');
    }
  } catch {
    showMsg('Ошибка соединения', 'error');
  } finally {
    btn.disabled = false;
  }
}

async function handleRegister(e) {
  e.preventDefault();
  const form = e.target;
  const btn = form.querySelector('button');
  btn.disabled = true;
  hideMsg();

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
      showMsg('Аккаунт создан! Войдите.', 'success');
      form.reset();
      setTimeout(() => switchTab('login'), 1200);
    } else {
      const data = await res.json().catch(() => ({}));
      showMsg(data.detail || 'Ошибка регистрации', 'error');
    }
  } catch {
    showMsg('Ошибка соединения', 'error');
  } finally {
    btn.disabled = false;
  }
}
