/**
 * 
 */
 
 
window.addEventListener('DOMContentLoaded', () => {
    statusFontColor(); // 글의 판매 상태에 따라 폰트색상 변경
    
    
    // 좋아요 -> 취소, 취소 -> 좋아요
    const btnHeart = document.querySelector('#btnHeart');
    btnHeart.addEventListener('click', function(){
        const postId = btnHeart.getAttribute('data-postId');
        const emptyHeart = document.querySelector('#emptyHeart');
        const fullHeart = document.querySelector('#fullHeart');
        const wishNum = document.querySelector('#wishNum');
        axios.get('/myPage/postLike', {
                params: {
                    postId: postId
                }
            })
            .then(likeStatus => {
                if(likeStatus.data === '좋아요'){
                    fullHeart.style.display = '';
                    emptyHeart.style.display = 'none';
                    alert('좋아요!!');
                    
                    // 전체 관심수 개수 반영해주기
                    axios.get('/sell/wishCount', {
                        params: {
                            postId: postId
                        }
                    }).then(wishCounts =>{
                        wishNum.textContent = '관심 ' + wishCounts.data;
                    }).catch(err => console.log(err + '전체 좋아요 개수 문제'));
                }else if(likeStatus.data === '좋아요 취소'){
                    fullHeart.style.display = 'none';
                    emptyHeart.style.display = '';
                    alert('좋아요 취소!!');
                    
                    // 전체 관심수 개수 반영해주기
                    axios.get('/sell/wishCount', {
                        params: {
                            postId: postId
                        }
                    }).then(wishCounts =>{
                        wishNum.textContent = '관심 ' + wishCounts.data;
                    }).catch(err => console.log(err + '전체 좋아요 개수 문제'));
                }else{
                    alert('로그인 후 이용 부탁드립니다.');
                }
            })
            .catch(err => console.log(err+'좋아요 에러 확인!!'));
    });
});
    // 채팅 연결하기
    function connectChat(event){
        var pid = event.target.getAttribute('data-pid');
        var sid = event.target.getAttribute('data-sid');
        var url = "/chat";
        var data = { postId: pid, sellerId: sid };
        // postmapping으로 연결할 url을 알아온 뒤 연결함.
        $.post(url, data, function(responseUrl) {
            window.location.href = responseUrl;
        });
    }
    // slide imgs 관련
    var slideIndex = 1;
    var mySlidesItems = document.getElementsByClassName('mySlides');
    indicatorsController();
    // carousel-indicators 컨트롤러
    function indicatorsController(){
        const indicatorsCount = mySlidesItems.length;
        if(indicatorsCount>1) { // 이미지 개수가 1개보다 많을 때만 가운데 버튼과 양쪽 prev, next button 보이게 하기.
            // next, prev 버튼
            const slideBtnsDiv = document.querySelector('.slideBtnsDiv');
            let nextPrevBtnStr = '';
            nextPrevBtnStr += '<div class="w3-center w3-container w3-section w3-large w3-text-white w3-display-bottommiddle" style="width:100%">'
                + '<div class="w3-left w3-hover-text-khaki" onclick="plusDivs(-1)">&#10094;</div>'
                + '<div class="w3-right w3-hover-text-khaki" onclick="plusDivs(1)">&#10095;</div>'
                + '<!-- 아래쪽 버튼 개수 동적으로 변경 -->'
                + '<div class="indicatorsBtnDiv">'
                + '</div>'
                + '</div>';
                slideBtnsDiv.innerHTML += nextPrevBtnStr;
            // indicators 버튼
            const indicatorsBtnDiv = document.querySelector('.indicatorsBtnDiv');
            for(let i=0; i < indicatorsCount; i++){
                   const slideBtnStr = `<span class="w3-badge demo w3-border w3-transparent w3-hover-white" onclick="currentDiv(${i+1})"></span>`
                       indicatorsBtnDiv.innerHTML += slideBtnStr;
            }
            
            // slides script
            showDivs(slideIndex);
            
        } else if(indicatorsCount === 1){ // 이미지 개수가 딱 1개일 때
            var y = document.getElementsByClassName("mySlides");
            y[slideIndex-1].style.display = "block";
        }
    }
    
    
    
    function plusDivs(n) {
      showDivs(slideIndex += n);
    }

    function currentDiv(n) {
      showDivs(slideIndex = n);
    }

    function showDivs(n) {
      var i;
      var x = document.getElementsByClassName("mySlides");
      var dots = document.getElementsByClassName("demo");
      if (n > x.length) {
          slideIndex = 1
      }
      if (n < 1) {
          slideIndex = x.length
      }
      for (i = 0; i < x.length; i++) {
        x[i].style.display = "none";  
      }
      for (i = 0; i < dots.length; i++) {
        dots[i].className = dots[i].className.replace(" w3-white", "");
      }
      x[slideIndex-1].style.display = "block";  
      dots[slideIndex-1].className += " w3-white";
    }
    function anonymousLogin(){
        alert("로그인 후 이용 부탁드립니다.");
        return;
    }
    
    // 글 작성자와 로그인 유저가 동일하다면 버튼 클릭으로 status 수정 가능하도록 해주기
    // 클릭했을 경우 변경 가능한 드롭다운 보여주기
    function statusModifing(){
      var optionBox = document.getElementById("statusChangeOption");
      if (optionBox.className.indexOf("w3-show") == -1) { 
        optionBox.className += " w3-show";
      } else {
        optionBox.className = optionBox.className.replace(" w3-show", "");
      }
    }
    
    // 판매중, 예약중, 판매완료 눌렀을 때 axios로 즉시 바꿔줌.
    function statusChanging(event){
        var postId = event.target.getAttribute('data-pid');
        const toChangeSts = event.target.text;
        const data = {
            id: postId,
            status: toChangeSts
        };
        axios.post('/sell/modify/status', data)
            .then(response => {
                const spanStatus = document.getElementById('spanStatus');
                if(toChangeSts === "예약중"){
                    spanStatus.style.color = 'green';
                } else if(toChangeSts === "판매완료"){
                    spanStatus.style.color = 'red';
                } else{
                    spanStatus.style.color = 'blue';
                }
                spanStatus.innerHTML = toChangeSts;
                statusModifing();
                alert(toChangeSts + ' 상태로 변경하였습니다.');
                
            })
            .catch(err => {console.log(err)});
    }    
    
    // 판매중, 예약중, 판매완료 각각의 조건에 따라 폰트색깔 동적으로 변경
    function statusFontColor(){
        const statusFontColor = document.getElementById('spanStatus');
        const spanStatus = statusFontColor.innerHTML;
        if(spanStatus === "예약중"){
            statusFontColor.style.color = 'green';
        } else if(spanStatus === "판매완료"){
            statusFontColor.style.color = 'red';
        }
    }