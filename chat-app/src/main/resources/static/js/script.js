    let stompClient = null;
    let currentRoom = null;
    let currentSub = null;

    function connect() {
        return new Promise((resolve) => {
            if (stompClient && stompClient.connected) { resolve(); return; }
            const socket = new SockJS('/ws');
            stompClient = Stomp.over(socket);
            stompClient.debug = null;
            stompClient.connect({}, () => resolve());
        });
    }

    async function joinRoom() {
        const room = document.getElementById('room').value.trim();
        if (!room) return;

        await connect();

        // Отписываемся от предыдущей комнаты
        if (currentSub) currentSub.unsubscribe();
        currentRoom = room;

        document.getElementById('messages').innerHTML = '';
        document.getElementById('status').textContent = '✅ Комната: ' + room;

        // Загружаем историю
        const res = await fetch(`/api/rooms/${room}/history`);
        const history = await res.json();
        history.forEach(m => addMessage(m.sender, m.content, m.timestamp));

        // Подписываемся на новые сообщения
        currentSub = stompClient.subscribe(`/topic/room/${room}`, (msg) => {
            const m = JSON.parse(msg.body);
            addMessage(m.sender, m.content, m.timestamp);
        });
    }

    function sendMessage() {
        const input = document.getElementById('input');
        const text = input.value.trim();
        const sender = document.getElementById('username').value.trim() || 'Аноним';
        if (!text || !currentRoom) return;

        stompClient.send(`/app/chat.room/${currentRoom}`, {}, JSON.stringify({
            sender: sender,
            content: text
        }));
        input.value = '';
    }

    function addMessage(sender, content, timestamp) {
        const me = document.getElementById('username').value.trim();
        const div = document.createElement('div');
        div.className = 'msg' + (sender === me ? ' own' : '');
        const time = timestamp ? new Date(timestamp).toLocaleTimeString() : '';
        div.innerHTML = `<div class="meta">${sender} · ${time}</div><div class="text">${content}</div>`;
        const box = document.getElementById('messages');
        box.appendChild(div);
        box.scrollTop = box.scrollHeight;
    }