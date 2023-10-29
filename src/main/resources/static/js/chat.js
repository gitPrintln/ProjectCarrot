/**
 * 
 */
 
 window.addEventListener('DOMContentLoaded', () => {
    
    var stompClient = null;
    var sender = $('#loginUser').val(); // 보내는 사람 senderNickName(현재 로그인 유저)
    var senderId = $('#loginUserId').attr('src').split("/")[3]; // 보내는 사람(로그인 유저의 Id를 따옴)
    var chatId = $('#chatId').val(); // 대화방의 id
    var chatPartner = $('#chatPartnerProfileId').attr('src'); // 상대방 채팅 이미지 src를 가져오기 위함. 채팅 상단바 채팅정보에 있는 정보(/img/user/chatProfileId)
    if(chatPartner != null){ // 채팅 상대방이 있는 경우에만 접속함.
        var chatPartnerId = chatPartner.split("/")[3];
        // invoke when DOM(Documents Object Model; HTML(<head>, <body>...etc) is ready
        $(document).ready(connect());
    }
    // SockJS 연결
    function connect() {
            // map URL using SockJS 
            var socket = new SockJS('/chat');
            var url = '/user/queue/messages/' + chatId;
            var notificationUrl = '/user/notification/' + chatId + "/" + chatPartnerId;
            // webSocket 대신 SockJS을 사용하므로 Stomp.client()가 아닌 Stomp.over()를 사용함
            stompClient = Stomp.over(socket);
            // connect(header, connectCallback(==연결에 성공하면 실행되는 메서드))
            stompClient.connect({}, function() { 
                autofocus();
                alarm(); // redis에 채팅방에 접속했음을 저장하고 상대에게는 채팅방에 들어왔음을 알림(안읽은 메세지를 읽음으로)
                
                // url: 채팅방 참여자들에게 공유되는 경로(message용)
                // callback(function()): 클라이언트가 서버(Controller broker)로부터 메시지를 수신했을 때(== STOMP send()가 실행되었을 때) 실행
                stompClient.subscribe(url, function(output) { // 메세지에 관한 구독
                    // html <body>에 append할 메시지 contents
                    showBroadcastMessage(createTextNode(JSON.parse(output.body)));
                    autofocus();
                    updateList();
                });
                
                // notificationUrl: 채팅방 참여자들에게 공유되는 경로(알림용) 읽음/안읽음 등
                stompClient.subscribe(notificationUrl, function(output){
                    responseAlarmCheck(output.body); // 어떤 종류의 알람인지 확인 후 그에 맞는 처리
                });
                }, 
                    // connect() 에러 발생 시 실행
                        function (err) {
                            console.log('error' + err);
            });
 
        };
    
    // 메세지 보내는 버튼 클릭 시 
    // webSocket broker 에게 JSON 타입 메시지 데이터를 전송
    function sendChat(json){
        stompClient.send("/app/chat/" + chatId, {}, JSON.stringify(json));
    }
    const btnSend = document.querySelector('#btnSend');
    const messageInput = document.querySelector('#message');
    
    btnSend.addEventListener('click', send);
    // JSON 타입으로 보내기 위해 포장, 그리고 후처리
    function send(){
        var message = $('#message').val();
        sendChat({
            'sender': sender,
            'message': message,
            'sendTime': getCurrentTime()
        });
        $('#message').val('');
        btnSend.disabled = true;
        btnSend.style.color = "silver";
        btnSend.style.backgroundColor = "white";
    }
    
    
    // 채팅 주고 받는 현재 시간
    function getCurrentTime(){
        return new Date().toLocaleString();
    }
    
    if(chatPartner != null){ // 채팅 상대방이 있는 경우에만 활성화함.
    // btn 활성화 버튼(아무것도 입력되지 않으면 보내기 버튼이 안됨) & enterkey 이벤트
    messageInput.addEventListener('keyup', activateBtn);
    }
    function activateBtn(event){
        const messageValue = document.querySelector('#message').value;
        if(messageValue == ''){
            btnSend.disabled = true;
            btnSend.style.color = "silver";
            btnSend.style.backgroundColor = "white";
        } else{
            btnSend.disabled = false;
            btnSend.style.color = "white";
            btnSend.style.backgroundColor = "blue";
            
            // 입력이 가능한 상태에서 enter 키로도 보낼수 있게
            if (event.key === "Enter") { // enter만 하면 전송
               event.preventDefault();
               send(); // 엔터 키로도 메시지 전송 함수 호출
            }
        }
    }
    
    if(chatPartner != null){ // 채팅 상대방이 있는 경우에만 활성화함.
    // 채팅 입력창에 포커싱을 맞출때 상대방에게 읽음 알림 보내기
    messageInput.addEventListener('focus', alarm);
    }
    
    // HTML 형태의 메시지를 화면에 출력해줌
    // 해당되는 id 태그의 모든 하위 내용들을 message가 추가된 내용으로 갱신해줌
    function showBroadcastMessage(message) {
        $('#content').html($('#content').html() + message);
    }
    // 받은 메시지인지 내가 보낸 메시지인지 구별하여 출력하기 위함, message.replace(/ /g, "&nbsp;"): 공백도 그대로 표현
    function createTextNode(messageObj) {
        if(messageObj.sender == sender){ // 내가 채팅을 보낼 경우(오른쪽)
            return '<div style="display: flex; flex-direction: row;">'
                    + '<div id="messageContent">'
                        + '<div style="text-align: right; align-self: flex-end; width: 380px;">'
                            + '<div>' + messageObj.message.replace(/ /g, "&nbsp;") + '</div>'
                            + '<div style="font-size:10px; color:grey;">' + messageObj.sendTime + '</div>'
                            + '<div id="reads" style="color:dodgerblue;">' + messageObj.read + '</div><br/>'
                        + '</div>'
                    + '</div>'
                 + '</div>';
        } else { // 상대 채팅을 받을 경우(왼쪽)
            return '<div style="display: flex; flex-direction: row;" id="newResponseHistory">'
                    + '<div style="width: 40px; text-align: left; align-self: flex-start;" id="messageProfile">' 
                        + `<img class="rounded-circle" width="40" height="40" src="${ chatPartner }">`
                    + '</div>'
                    + '<div id="messageContent">'
                        + '<div style="text-align: left; align-self: flex-start; width:380px;" id="newHistory">'
                            + '<div>' + messageObj.message.replace(/ /g, "&nbsp;") + '</div>'
                            + '<div style="font-size:10px; color:grey;">' + messageObj.sendTime + '</div><br/>'
                        + '</div>'
                    + '</div>'
                + '</div>';
        }
    }
    
    // 메세지를 주고받을 때 자동으로 스크롤 위치를 맞춰줌.
    function autofocus(){
        $chatHistory = $('#content');
        $chatHistory.scrollTop($chatHistory[0].scrollHeight);
    }
    
    // Redis에 채팅방에 접속중인 유저로 저장하고 채팅방에 들어왔음을 알릴 때
    // 채팅창에 포커싱 맞춰졌을 때 읽음으로 간주하고 상대에게 읽음을 알릴 때
    function alarm(){
        const json = {'userNick': sender, 'userId': senderId, 'partnerId': chatPartnerId};
        stompClient.send("/app/chatNotification/" + chatId, {}, JSON.stringify(json));
    }
    
    // 어떤 종류의 알람인지(읽음/안읽음을 위한 처리, 로그인 알람, 채팅방 리스트 변경)
    function responseAlarmCheck(str){
        if(str === "ChatPartner's Notification"){
               chatPartnerAlarm();
        } 
    };
    
    // ChatList 새로운 채팅이 갱신될 때마다 AJAX로 갱신
    function updateList(){
        axios
        .get('/api/chatList/'+senderId)
        .then(response => { 
            updateChatList(response.data);
        })
        .catch(err => { console.log(err) });
    }

    // 읽음/안읽음을 위한 처리(읽었으면 1->0으로 지워줌)
    function chatPartnerAlarm(){
       console.log("상대방로그인입니다.");
       const read = document.querySelectorAll('#reads');
       for (var i = 0; i < read.length; ++i) {
             read[i].style.visibility = 'hidden';
             read[i].removeAttribute('id');
       }
    }
    
    // 채팅 리스트 갱신
    function updateChatList(data){
        const chatList = document.querySelector('#chatList');
        let str = '';
           str += '<table style="width: 100%;">'
                +    '<tbody>';
        for(let c of data){
            str +=    `<tr class="listElement" style="border: 1px solid pink; height: 50px;" onclick="location.href='/chat?chatId=${ c.id }'">`    
                +         '<td width="15%">';
            if(c.sellerId != senderId){
            str +=             `<img src="${ '/img/user/'+ c.sellerId }" alt="해당 글의 상대 프사"/>`;
            } else{
            str +=             `<img src="${ '/img/user/'+ c.partnerId }" alt="해당 글의 상대 프사"/>`;
            }
            str +=         '</td>'
                +         '<td width="80%">'
                +             `<span> ${c.lastChat}</span>`
                +             `<span style="float: right; font-size: 8px; color: gray;">${c.lastTime}</span>`
                +         '</td>'
                +     '</tr>'
        }
           str +=     '</tbody>'
                +  '</table>';
        
        chatList.innerHTML = str;
    }
});

// 페이지를 이탈하는 순간, 상대에게 알리기 위해 => redis loginUser 제외, 궁극적으로 안읽음 상태로 남기위해(X)
window.addEventListener('beforeunload', function(event) {
    var chid = $('#chatId').val(); // 대화방의 id
    
    if(chid != null){ // 채팅방이 있는 경우에만 활성화
    var useid = $('#loginUserId').attr('src').split("/")[3];
    axios.get('/chat/redis/logOut', {
        params: {
            'chatId': chid, 
            'userId': useid
            }
        })
        .then(response => {
            console.log('redis logout 완료');
        })
        .catch(err=>{console.log(err)});
    }
});
