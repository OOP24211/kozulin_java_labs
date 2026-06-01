var currentUsername = sessionStorage.getItem("username");
if (!currentUsername) {
    window.location.href = "/login.html";
}

document.getElementById("user-greeting").textContent = currentUsername;

var stompClient = null;
var currentRoom = null;
var currentSub = null;
var userSub = null;
var selectedFile = null;

function doLogout() {
    if (currentRoom && stompClient && stompClient.connected) {
        stompClient.send("/app/chat.leave/" + currentRoom, {}, JSON.stringify({ sender: currentUsername }));
    }
    sessionStorage.removeItem("username");
    window.location.href = "/login.html";
}

function connect() {
    return new Promise(function(resolve) {
        if (stompClient && stompClient.connected) { resolve(); return; }
        var socket = new SockJS("/ws");
        stompClient = Stomp.over(socket);
        stompClient.debug = null;
        stompClient.connect({}, function() { resolve(); });
    });
}

async function joinRoom(room) {
    if (!room) return;
    await connect();
    if (currentSub) currentSub.unsubscribe();
    if (userSub) userSub.unsubscribe();
    if (currentRoom) {
        stompClient.send("/app/chat.leave/" + currentRoom, {}, JSON.stringify({ sender: currentUsername }));
    }
    currentRoom = room;
    document.getElementById("messages").innerHTML = "";
    document.getElementById("current-room-title").textContent = "# " + room;
    document.querySelectorAll("#room-list li").forEach(function(li) {
        li.classList.toggle("active", li.dataset.room === room);
    });
    var res = await fetch("/api/rooms/" + room + "/history");
    var history = await res.json();
    history.forEach(function(m) { addMessage(m.sender, m.content, m.timestamp); });
    currentSub = stompClient.subscribe("/topic/room/" + room, function(msg) {
        var m = JSON.parse(msg.body);
        addMessage(m.sender, m.content, m.timestamp);
    });
    userSub = stompClient.subscribe("/topic/room/" + room + "/users", function(msg) {
        renderUsers(JSON.parse(msg.body));
    });
    stompClient.send("/app/chat.join/" + room, {}, JSON.stringify({ sender: currentUsername }));
}

function renderUsers(users) {
    var list = document.getElementById("user-list");
    list.innerHTML = "";
    users.forEach(function(u) {
        var li = document.createElement("li");
        li.textContent = u;
        list.appendChild(li);
    });
}

async function joinRoomByInput() {
    var room = document.getElementById("room-input").value.trim();
    if (!room) return;
    document.getElementById("room-input").value = "";
    await fetch("/api/rooms", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: room })
    });
    addRoomToSidebar(room);
    joinRoom(room);
}

function addRoomToSidebar(room) {
    var list = document.getElementById("room-list");
    for (var i = 0; i < list.children.length; i++) {
        if (list.children[i].dataset.room === room) return;
    }
    var li = document.createElement("li");
    li.textContent = "# " + room;
    li.dataset.room = room;
    li.onclick = function() { joinRoom(room); };
    list.appendChild(li);
}

async function loadRooms() {
    var res = await fetch("/api/rooms");
    var rooms = await res.json();
    rooms.forEach(function(r) { addRoomToSidebar(r); });
}

function sendMessage() {
    var input = document.getElementById("input");
    var text = input.value.trim();
    if (!text || !currentRoom) return;
    stompClient.send("/app/chat.room/" + currentRoom, {}, JSON.stringify({ sender: currentUsername, content: text }));
    input.value = "";
}

function onFileSelected() {
    var fileInput = document.getElementById("file-input");
    var file = fileInput.files[0];
    if (!file || !currentRoom) { fileInput.value = ""; return; }
    selectedFile = file;
    var preview = document.getElementById("file-modal-preview");
    preview.innerHTML = "";
    var ext = file.name.split(".").pop().toLowerCase();
    if (["png","jpg","jpeg","gif","webp","svg"].indexOf(ext) !== -1) {
        var img = document.createElement("img");
        img.src = URL.createObjectURL(file);
        preview.appendChild(img);
    } else {
        preview.textContent = "📄";
    }
    document.getElementById("file-modal-name").textContent = file.name;
    document.getElementById("file-caption").value = "";
    document.getElementById("file-modal").style.display = "flex";
    document.getElementById("file-caption").focus();
}

function cancelFile() {
    document.getElementById("file-modal").style.display = "none";
    document.getElementById("file-input").value = "";
    selectedFile = null;
}

async function sendFile() {
    if (!selectedFile || !currentRoom) return;
    var caption = document.getElementById("file-caption").value.trim();
    var formData = new FormData();
    formData.append("file", selectedFile);
    formData.append("sender", currentUsername);
    var res = await fetch("/api/rooms/" + currentRoom + "/upload", { method: "POST", body: formData });
    var data = await res.json();
    var content = "[file:" + data.url + ":" + data.name + "]";
    if (caption) content += ":caption:" + caption;
    stompClient.send("/app/chat.room/" + currentRoom, {}, JSON.stringify({ sender: currentUsername, content: content }));
    document.getElementById("file-modal").style.display = "none";
    document.getElementById("file-input").value = "";
    selectedFile = null;
}

function addDeletedPlaceholder(parent, text) {
    var err = document.createElement("div");
    err.className = "msg-deleted";
    err.textContent = text;
    parent.appendChild(err);
}

function addMessage(sender, content, timestamp) {
    var div = document.createElement("div");
    div.className = "msg" + (sender === currentUsername ? " own" : "");
    var time = timestamp ? new Date(timestamp).toLocaleTimeString() : "";
    var meta = document.createElement("div");
    meta.className = "meta";
    meta.textContent = sender + " - " + time;
    div.appendChild(meta);

    if (content.indexOf("[file:") === 0) {
        var captionText = "";
        var inner;
        var captionIdx = content.indexOf("]:caption:");
        if (captionIdx !== -1) {
            inner = content.slice(6, captionIdx);
            captionText = content.slice(captionIdx + 10);
        } else {
            inner = content.slice(6, content.length - 1);
        }
        var colonIdx = inner.indexOf(":");
        var url = inner.slice(0, colonIdx);
        var name = inner.slice(colonIdx + 1);
        var ext = name.split(".").pop().toLowerCase();
        if (["png","jpg","jpeg","gif","webp","svg"].indexOf(ext) !== -1) {
            var img = document.createElement("img");
            img.className = "msg-image";
            img.src = url;
            img.alt = name;
            img.onerror = function() {
                img.style.display = "none";
                addDeletedPlaceholder(div, "Фото удалено с сервера");
            };
            div.appendChild(img);
        } else {
            var a = document.createElement("a");
            a.className = "msg-file";
            a.href = url;
            a.target = "_blank";
            a.textContent = "📄 " + name;
            fetch(url, { method: "HEAD" }).then(function(r) {
                if (!r.ok) {
                    a.style.display = "none";
                    addDeletedPlaceholder(div, "Файл удалён с сервера");
                }
            });
            div.appendChild(a);
        }
        if (captionText) {
            var cap = document.createElement("div");
            cap.className = "msg-caption";
            cap.textContent = captionText;
            div.appendChild(cap);
        }
    } else {
        var textDiv = document.createElement("div");
        textDiv.className = "text";
        textDiv.textContent = content;
        div.appendChild(textDiv);
    }
    var box = document.getElementById("messages");
    box.appendChild(div);
    box.scrollTop = box.scrollHeight;
}

function openLightbox(src) {
    document.getElementById("lightbox-img").src = src;
    document.getElementById("lightbox").style.display = "flex";
}

function closeLightbox() {
    document.getElementById("lightbox").style.display = "none";
}

document.addEventListener("keydown", function(e) {
    if (e.key === "Escape") closeLightbox();
});

loadRooms();
