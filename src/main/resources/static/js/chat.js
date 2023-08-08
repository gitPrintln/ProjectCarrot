/**
 * 
 */
 
 window.addEventListener('DOMContentLoaded', () => {
    
    var stompClient = null;
    var sender = $('#loginUser').val();
    var partner = 11;
    // invoke when DOM(Documents Object Model; HTML(<head>, <body>...etc) is ready
    $(document).ready(connect());
    
    function connect() {
            // map URL using SockJS 
            var socket = new SockJS('/chat');
            var url = '/user/queue/messages';
            // webSocket 대신 SockJS을 사용하므로 Stomp.client()가 아닌 Stomp.over()를 사용함
            stompClient = Stomp.over(socket);
            // connect(header, connectCallback(==연결에 성공하면 실행되는 메서드))
            stompClient.connect({}, function() { 
                // url: 채팅방 참여자들에게 공유되는 경로
                // callback(function()): 클라이언트가 서버(Controller broker)로부터 메시지를 수신했을 때(== STOMP send()가 실행되었을 때) 실행
                stompClient.subscribe(url, function(output) { // 메세지에 관한 구독
                    // html <body>에 append할 메시지 contents
                    showBroadcastMessage(createTextNode(JSON.parse(output.body)));
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
        stompClient.send("/app/chat/" + partner, {}, JSON.stringify(json));
    }
    const btnSend = document.querySelector('#btnSend');
    const messageInput = document.querySelector('#message');
    
    btnSend.addEventListener('click', send);
    
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
    }
    
    // 채팅 주고 받는 현재 시간
    function getCurrentTime(){
        return new Date().toLocaleString();
    }
    
    // btn 활성화 버튼 이벤트
    messageInput.addEventListener('keyup', activateBtn);
    function activateBtn(){
        const messageValue = document.querySelector('#message').value;
        if(messageValue == ''){
            btnSend.disabled = true;
            btnSend.style.color = "silver";
        } else{
            btnSend.disabled = false;
            btnSend.style.color = "blue";
        }
    }
    
    // HTML 형태의 메시지를 화면에 출력해줌
    // 해당되는 id 태그의 모든 하위 내용들을 message가 추가된 내용으로 갱신해줌
    function showBroadcastMessage(message) {
        $("#content").html($("#content").html() + message);
    }
    
    function createTextNode(messageObj) {
            if(messageObj.sender == sender){ // 채팅을 보내는 사람
                return '<div style="width: 500px;"><div style="text-align: right; margin-left: 480px; width:270px;" id="newHistory"><div style="text-align: right; width:270px;">' +
            messageObj.message+
            '</div><div style="width: 270px; text-align: right: font-size:13px; color:grey;">' +
            messageObj.sendTime +
            '</div><div id="reads" style="color:dodgerblue;">1</div></div></div>';
            } else { // 채팅을 받는 사람
            return '<div id="newResponseHistory" class="alert alert-info"><div style="width: 40px; margin-right: 15px; display: inline-block; float: left;">' +
            '<img class="rounded-circle" width="40" height="40" src="' +
            chatWithImage + 
            '" style="margin-right:10px;"></div><div style="width: 270px; text-align: left; display: inline-block;"><div>' +
            messageObj.message+
            '</div><div style="font-size:13px; color:grey;">' +
            messageObj.sendTime +
            '</div><br/><br/></div></div>';
            }
    }
    
    
    
    
    
    
    
    
    
    
});