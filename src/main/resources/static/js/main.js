'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');
const googleAuthButton = document.querySelector('#google-auth-btn');

let stompClient = null;
let nickname = null;
let password = null;
let selectedUserId = null;
let token = null;

let userId = null; // Assuming userId is set somewhere in your application

// 1. Get token from localStorage
token = localStorage.getItem("token");

// 2. Check if token exists
if (token) {
  fetch("/api/v1/auth/validate", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    }
  })
  .then(response => {
    return response.text().then(text => {
      if (response.ok && text === "Token is valid") {
        console.log("✅ Token is valid");
      } else {
        console.log("body:", text);
        console.log("❌ Token is invalid, clearing storage...");
        localStorage.removeItem("token");
        // redirect to login page, show modal, etc.
      }
    });
  })
  .catch(error => {
    console.error("Error validating token:", error);
    localStorage.removeItem("token");
  });
} else {
  console.log("⚠️ No token found in localStorage");
}


window.onload = () => {
console.log("heyhye");

  const hash = window.location.hash;
  console.log(window.location);
  
  if (hash.startsWith("#token=")) {
    token = hash.substring("#token=".length);

    localStorage.setItem("token", token);

    // Clear the fragment to clean URL
    history.replaceState(null, null, "/index.html");

    // window.location.href = "/index.html";
    openApp();
    
  } 
  else {
    console.log(hash);
    
  }
};

async function openApp(){
    usernamePage.classList.add('hidden');
    chatPage.classList.remove('hidden');

    nickname = await fetch('/api/v1/user/me', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    }).then(response => {
        if (response.ok) {
            return response.json();  // parse JSON body
        } else {
            return response.text().then(text => {
                console.log(text);
                return null;
            });
        }
    }).then(data => {
        if (data) {
            return data.email;  // assuming response JSON has 'email' field
        }
        return null;
    });

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({ Authorization: `Bearer ${token}` }, onConnected, onError);
}




function getToken(email, password) {
	if (localStorage.getItem('token')) {
		return Promise.resolve(localStorage.getItem('token'));
	}
	document.querySelector('#chat-title').textContent = 'Connecting...' + email;
    
	try {
        token = fetch('/api/v1/user/authenticate', {
		method: 'POST',
		headers: {            
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({ email: email, password: password })
        }).then(response => {
            if (!response.ok) {
                console.log(response.text());
            
                throw new Error('Network response was not ok');
            }
            return response.json();
        }).then(data => {
            var token = data.token;
            localStorage.setItem('token', token);
            return token;
        });

        console.log('Token fetched:', token);
        return Promise.resolve(token);
    } catch (error) {
        console.log(error);
        
    }
    
    


}

async function connect(event) {
    event.preventDefault();
    nickname = document.querySelector('#nickname').value.trim();
    password = document.querySelector('#password').value.trim();

    if (nickname && password) {
        token = await getToken(nickname, password);
        if (!token) {
            console.log('Failed to fetch token');
            return;
        }

        openApp();

    }
    event.preventDefault();
}


async function onConnected() {
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/user/public`, onMessageReceived);
    
    stompClient.subscribe(`/user/${nickname}/queue/messageStatus`, onMessageRead);
    stompClient.subscribe(`/user/${nickname}/queue/sentMessage`, onMessageSent);

    stompClient.subscribe(`/user/${nickname}/topic`, message => {
        console.log('Message from /user/topic:', message);
        // handle received user info here
        userId = JSON.parse(message.body).id;
        console.log('User ID:', userId);
        
    });

    // register the connected user
    stompClient.send("/app/user.addUser",
        {
            Authorization: `Bearer ${token}` 
        },
        JSON.stringify({ })
    );


    document.querySelector('#connected-user-nickname').textContent = nickname;
    findAndDisplayConnectedUsers().then();
}

async function findAndDisplayConnectedUsers() {
    const connectedUsersResponse = await fetch('/api/v1/user/users', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    });
    let connectedUsers = await connectedUsersResponse.json();
    console.log('Connected users:', connectedUsers);
    connectedUsers = connectedUsers.filter(user => user.username !== nickname);
    const connectedUsersList = document.getElementById('connectedUsers');
    connectedUsersList.innerHTML = '';

    connectedUsers.forEach(user => {
        appendUserElement(user, connectedUsersList);
        if (connectedUsers.indexOf(user) < connectedUsers.length - 1) {
            const separator = document.createElement('li');
            separator.classList.add('separator');
            connectedUsersList.appendChild(separator);
        }
    });
}

function appendUserElement(user, connectedUsersList) {
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = 'user_id:' + user.id;
    console.log(listItem.id);
    

    const userImage = document.createElement('img');
    userImage.src = '../img/user_icon.png';
    userImage.alt = user.username;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.username;

    const receivedMsgs = document.createElement('span');
    receivedMsgs.textContent = '0';
    receivedMsgs.classList.add('nbr-msg', 'hidden');

    listItem.appendChild(userImage);
    listItem.appendChild(usernameSpan);
    listItem.appendChild(receivedMsgs);

    listItem.addEventListener('click', userItemClick);

    connectedUsersList.appendChild(listItem);
}

function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    messageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    selectedUserId = clickedUser.getAttribute('id').slice(8);
    fetchAndDisplayUserChat().then();

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
    nbrMsg.textContent = '0';

}

function displayMessage(senderId, content, id, isRead) {
    
    const messageContainer = document.createElement('div');
    messageContainer.id = "message_id-" + id; 
    messageContainer.classList.add('message');
    if (senderId === userId) {
        messageContainer.classList.add('sender');
        const checkmarks = document.createElement('span');
        checkmarks.textContent = '✔✔';
        checkmarks.classList.add('checkmarks')

        messageContainer.appendChild(checkmarks);

        if (isRead){
            messageContainer.classList.add('read');
        }
    } else {
        messageContainer.classList.add('receiver');

        if (!isRead){
            const chatMessage = {
                id: id
            };
            stompClient.send("/app/read", { Authorization: `Bearer ${token}` }, JSON.stringify(chatMessage));
        }
    }
    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);


}

async function fetchAndDisplayUserChat() {
    console.log(`Fetching chat for userId: ${userId}, selectedUserId: ${selectedUserId}`);
    
    var userChatResponse = null;
    
    try {
        userChatResponse = await fetch(
        `api/v1/chat/messages/${userId}/${selectedUserId}`
        , {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        }
    );
        if (!userChatResponse.ok) {
            throw new Error(`Failed to fetch user chat.`);

        }

    } catch (error) {        
        console.log(`Error fetching user chat: `, error);
        chatArea.innerHTML = '';
        return;
    }
    
    const userChat = await userChatResponse.json();
    chatArea.innerHTML = '';
    userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.content, chat.id, chat.isRead);
    });
    chatArea.scrollTop = chatArea.scrollHeight;
}


function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

 
function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    const tempId = `temp-${Date.now()}`;

    if (messageContent && stompClient) {
        const chatMessage = {
            recipientId: selectedUserId,
            content: messageInput.value.trim(),
            tempId: tempId
        };


        stompClient.send("/app/chat", { Authorization: `Bearer ${token}` }, JSON.stringify(chatMessage));
        
        displayMessage(userId, messageInput.value.trim(), tempId, false);
        messageInput.value = '';
    }
    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}


async function onMessageReceived(payload) {
    await findAndDisplayConnectedUsers();
    console.log('Message received', payload);
    const message = JSON.parse(payload.body);
    if (selectedUserId && selectedUserId == message.senderId) {
        displayMessage(message.senderId, message.content, message.id, message.isRead);
        chatArea.scrollTop = chatArea.scrollHeight;
    }
    else{
        console.log(`selected user id: ${selectedUserId}, senderid: ${message.senderId}`);
        
    }

    if (selectedUserId) {
        document.querySelector(`#user_id\\:${selectedUserId}`).classList.add('active');
    } else {
        messageForm.classList.add('hidden');
    }

    
    const notifiedUser = document.querySelector(`#user_id\\:${message.senderId}`);
    if (notifiedUser && !notifiedUser.classList.contains('active')) {
        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent = '';
    }
    
}

function onMessageRead(payload) {
    console.log('Message read', payload);
    const message = JSON.parse(payload.body);
    if (selectedUserId && selectedUserId == message.recipientId) {
        setMessageRead(message.id);
        chatArea.scrollTop = chatArea.scrollHeight;
    } 
}

async function onMessageSent(payload) {
    console.log('Message Sent', payload);
    const message = JSON.parse(payload.body);
    
    const tempId = message.tempId;
    const messageId = message.id;

    const messageDiv = document.getElementById('message_id-' + tempId);
    messageDiv.id = 'message_id-' + messageId;
}

function setMessageRead(id) {
    console.log(id);
    
    const message = document.querySelector("#message_id-"+id);
    message.classList.add('read');
}



function onLogout() {
    stompClient.send("/app/user.disconnectUser",
        {
            Authorization: `Bearer ${token}` 
        },
        JSON.stringify({ })
    );
    localStorage.removeItem('token');
    window.location.reload();
}

function onGoogle(event) {
    event.preventDefault();

    // Redirect browser to OAuth2 authorization endpoint (with prompt param if desired)
    window.location.href = "/oauth2/authorize/google";
}

// usernameForm.addEventListener('submit', connect, true); // step 1
usernameForm.addEventListener('submit', connect); // step 1
messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);
window.onbeforeunload = () => onLogout();

googleAuthButton.addEventListener('click', onGoogle);