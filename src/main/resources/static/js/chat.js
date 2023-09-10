/**
 * 
 */
 
 window.addEventListener('DOMContentLoaded', () => {
    
    var stompClient = null;
    var sender = $('#loginUser').val(); // 보내는 사람 senderNickName(현재 로그인 유저)
    var chatId = $('#chatId').val(); // 대화방의 id
    var chatPartner = $('#chatPartnerProfileId').attr('src') // 상대방 채팅 이미지 src를 가져오기 위함. 채팅 상단바 채팅정보에 있는 정보(/img/user/chatProfileId)
    var chatPartnerId = chatPartner.split("/")[3];
    // invoke when DOM(Documents Object Model; HTML(<head>, <body>...etc) is ready
    $(document).ready(connect());
    
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
                loginAlarm(); // redis에 채팅방에 접속했음을 저장하고 상대에게는 채팅방에 들어왔음을 알림(안읽은 메세지를 읽음으로)
                
                // url: 채팅방 참여자들에게 공유되는 경로(message용)
                // callback(function()): 클라이언트가 서버(Controller broker)로부터 메시지를 수신했을 때(== STOMP send()가 실행되었을 때) 실행
                stompClient.subscribe(url, function(output) { // 메세지에 관한 구독
                    // html <body>에 append할 메시지 contents
                    showBroadcastMessage(createTextNode(JSON.parse(output.body)));
                    autofocus();
                });
                
                // notificationUrl: 채팅방 참여자들에게 공유되는 경로(알림용) 읽음/안읽음 등
                stompClient.subscribe(notificationUrl, function(){
                    // TODO: 상대방이 채팅방에 왔음을 알리는 경우, 1을 지워준다든지
                    
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
    
    
    // btn 활성화 버튼(아무것도 입력되지 않으면 보내기 버튼이 안됨) & enterkey 이벤트
    messageInput.addEventListener('keyup', activateBtn);
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
                            + '<div id="reads" style="color:dodgerblue;">1</div>'
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
    
    // Redis에 채팅방에 접속중인 유저로 저장하고 채팅방에 들어왔음을 알림(안읽은 메세지를 읽음으로)
    function loginAlarm(){
        const json = {'chatId': chatId, 'loginUser': sender};
        stompClient.send("/app/entrance/" + chatId, {}, JSON.stringify(json));
    }
    
});

// 페이지를 이탈하는 순간, 상대에게 알리기 위해 => redis loginUser 제외, 궁극적으로 안읽음 상태로 남기위해
window.addEventListener('beforeunload', function(event) {
    
});