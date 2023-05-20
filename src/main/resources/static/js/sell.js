/**
 * 
 */
 
 
window.addEventListener('DOMContentLoaded', () => {
    
const btnSubmit = document.querySelector('#btnSubmit');

// 등록하기 버튼 눌렀을 때
btnSubmit.addEventListener('click', function () {
    const title = document.querySelector('#title').value;
    const category = document.querySelector('#category').value;
    const prices = document.querySelector('#prices').value;
    const content = document.querySelector('#content').value;
    const regionMain = document.querySelector('#regionMain').value;
    const detailRegion = document.querySelector('#detailRegion').value;
    
    
    if (title == '' || category == '' || prices == '' || content == '') {
        alert('빠진 부분을 채워넣어주세요!');
        return;
    }
    
    const result = confirm('정말 등록하시겠습니까?');
    
    if (result) {
        const region = regionMain + ', ' + detailRegion; // Main주소 + 상세 주소
        
        // 전달해줄 완성된 전체 주소 input창 만들어주기
        const location = document.getElementById('location');
        const jusoStr = `<div><input type="hidden" class="w3-input w3-border w3-hover-shadow w3-sand" id="region" name="region" value="${region}" readonly/></div>`;
        location.innerHTML += jusoStr;
        
        
            document.querySelector('#formSell').submit();
            formSell.action = '/sell/create';
            formSell.method = 'post';
            formSell.submit();
    }
    
    
});


// region-input 창 클릭해도 열리게
const regionMainInput = document.getElementById('regionMain');
regionMainInput.addEventListener('click', serchRegion);







});

// 주소 api 팝업창
function serchRegion(){
new daum.Postcode({
    oncomplete: function(data) {
         document.getElementById('regionMain').value = data.address; // 사용자가 찾은 main주소 input창에 넣기
         const region = document.querySelector('#detailRegion');
         region.style.display = 'block'; // 상세 입력창 등장
         region.value = '';
         region.focus(); // 상세 입력 포커싱
    }
}).open();
}

// 가격: 숫자만 입력, 세 자리마다 콤마
function inNumber(event){
    const check = /^[0-9,]*$/; // 숫자와 콤마만 입력가능한 변수 설정
    var inputValue = event.target.value;
    if (!check.test(inputValue)) { // 입력값이 숫자와 콤마로만 이루어져 있는지 검사
        // 사용자에게 알림
        alert("숫자로만 입력하세요!!");
        event.target.value = "";
        return;
      }
    
    // 숫자와 콤마로 이루어져 있다면.. 세 자리마다 콤마 설정하기
    inputValue = event.target.value.replace(/[^\d]/g, ""); // 값을 콤마를 제거하고 숫자로만 이루어지도록 설정
    return event.target.value = inputValue.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ","); // 세 자리 수마다 콤마를 넣어서 내보내줌.
}