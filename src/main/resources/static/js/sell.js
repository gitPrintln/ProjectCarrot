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
        // (1) 최종적으로 등록된 이미지들을 DB에 저장.
        const imageData = new FormData(); //  HTML <form> 요소의 데이터를 캡슐화하고, Ajax를 통해 서버로 전송하기 위해 사용
        const imageFileInput =  document.querySelector('input[name="images"]');
        
        // Array.from()은 유사 배열 객체나 이터러블(iterable) 객체를 배열로 변환하는 메서드
        // Array.from(iterable, mapFn, thisArg) 이런 형태
        // iterable: 배열로 변환할 유사 배열 객체 또는 이터러블 객체
        // mapFn (선택적): 배열의 각 요소에 대해 호출될 맵핑 함수
        // thisArg (선택적): mapFn에서 사용할 this 값을 지정        
        Array.from(imageFileInput).forEach(f => {
            imageData.append('files', f);
        });
        
        if(imageData != null) {
        uploadImages(imageData);
        }
        
        // (2) 전달해줄 완성된 전체 주소 input창 만들어주기
        const region = regionMain + ', ' + detailRegion; // Main주소 + 상세 주소
        const location = document.getElementById('location');
        const jusoStr = `<div><input type="hidden" class="w3-input w3-border w3-hover-shadow w3-sand" id="region" name="region" value="${region}" readonly/></div>`;
        location.innerHTML += jusoStr;
        
        
        // (3) 그 외 나머지 정보 DB에 저장.
            document.querySelector('#formSell').submit();
            formSell.action = '/sell/create';
            formSell.method = 'post';
            formSell.submit();
    }
    
    
});


// region-input 창 클릭해도 열리게
const regionMainInput = document.getElementById('regionMain');
regionMainInput.addEventListener('click', serchRegion);


function uploadImages(imageData){
    axios.post('/img/upload', imageData)
    .then(alert('이미지 작업끝났는데요> 확인해보세요!!'))
    .catch(err => {alert(err+'인데요, 확인해보세요!!')});
}




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