/**
 * 
 */
 
 
window.addEventListener('DOMContentLoaded', () => {
    
});
    function connectChat(event){
        var pid = event.target.getAttribute('data-pid');
        var sid = event.target.getAttribute('data-sid');
        var url = "chat";
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
    
    