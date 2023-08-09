/**
 * 
 */
 
 
window.addEventListener('DOMContentLoaded', () => {
    
    const btnSubmit = document.querySelector('#btnSubmit');
    // * 최종이미지 DB에 저장할 dataForm
    let imageData = []; //  HTML <form> 요소의 데이터를 캡슐화하고, Ajax를 통해 서버로 전송하기 위해 사용
        
    // 등록하기 버튼 눌렀을 때
    btnSubmit.addEventListener('click', function() {
        const title = document.querySelector('#title').value;
        const category = document.querySelector('#category').value;
        const prices = document.querySelector('#prices').value;
        let content = document.querySelector('#content').value;
        const regionMain = document.querySelector('#regionMain').value;
        const detailRegion = document.querySelector('#detailRegion').value;

        if (title == '' || category == '' || prices == '' || content == '') {
            alert('빠진 부분을 채워넣어주세요!');
            return;
        }

        const result = confirm('정말 등록하시겠습니까?');

        if (result) {
            // 제출했음을 변수에 저장
            submitted = true;
            content = content.replaceAll(/(\n|\r\n)/g, "<br>");
            alert("줄바꿈 체크중" +   content);
            // (1) 전달해줄 완성된 전체 주소 input창 만들어주기
            if(detailRegion != '') { // 상세 주소까지 있을 경우
            const region = regionMain + ', ' + detailRegion; // Main주소 + 상세 주소
            const location = document.getElementById('location');
            const jusoStr = `<div><input type="hidden" class="w3-input w3-border w3-hover-shadow w3-sand" id="region" name="region" value="${region}" readonly/></div>`;
            location.innerHTML += jusoStr;
            } else if(regionMain != ''){ // 상세 주소는 없고 Main 주소만 있을 경우
                const location = document.getElementById('location');
                const jusoStr = `<div><input type="hidden" class="w3-input w3-border w3-hover-shadow w3-sand" id="region" name="region" value="${regionMain}" readonly/></div>`;
                location.innerHTML += jusoStr;
            }
            
            
            
            // (2) 이미지 파일들 확인
            const selectedImage = document.querySelector('#selectedImage');
            if(selectedImage.children.length > 0){ // 이미지가 있는 경우
                const finalImgs = selectedImage.querySelectorAll('img')
                
                // Array.from()은 유사 배열 객체나 이터러블(iterable) 객체를 배열로 변환하는 메서드
                // Array.from(iterable, mapFn, thisArg) 이런 형태
                // iterable: 배열로 변환할 유사 배열 객체 또는 이터러블 객체
                // mapFn (선택적): 배열의 각 요소에 대해 호출될 맵핑 함수
                // thisArg (선택적): mapFn에서 사용할 this 값을 지정        
                for (let file of finalImgs) {
                    const imgSrc = file.getAttribute('data-src');
                    console.log(file);
                    imageData.push(imgSrc);
                }
                axios.post('/img/upload/db', imageData)
                    .then(response => {
                        const imgIds = response.data.join(', ');
                        const imgs = document.getElementById('imgs');
                        const imgIdsForDbSave = `<div><input class="w3-input w3-border w3-hover-shadow w3-sand" id="imgIds" name="imgIds" value="${imgIds}" readonly/></div>`;
                        imgs.innerHTML += imgIdsForDbSave;
                    })
                    .catch(err => {alert(err+"!!!!");
                    });
                
           }
                
                
                
            // axios 작업 기다려 준 후 최종 제출.
            setTimeout(function(){
                // (3) 그 외 나머지 정보 DB에 저장.
                    document.querySelector('#formSell').submit();
                    formSell.action = '/sell/create';
                    formSell.method = 'post';
                    formSell.submit();
            }, 75);
            
        }
    });


// region-input 창 클릭해도 열리게
const regionMainInput = document.getElementById('regionMain');
regionMainInput.addEventListener('click', serchRegion);

});

let submitted = false; // 제출 여부 확인

// 제출하지 않고 페이지를 벗어날 시 발생하는 이벤트 리스너(실제 등록되지 않은 로컬저장소 이미지 삭제)
window.addEventListener('beforeunload', function(event) {
    const selectedImage = document.querySelector('#selectedImage');
    if(!submitted && selectedImage.children.length > 0){ // 제출하지 않았을 때, 등록하려던 이미지가 있을 때만 실행
    deleteTemporaryFile();
    }
});

// 로컬저장소에 저장되어있지만 최종 등록 되지 않은 이미지 삭제
function deleteTemporaryFile(){
    let temporaryData = [];
    const selectedImage = document.querySelector('#selectedImage');
    const finalImgs = selectedImage.querySelectorAll('img');
        for (let file of finalImgs) {
                const imgSrc = file.getAttribute('data-src');
                temporaryData.push(imgSrc);
           }
        axios.delete('/img/delete/' + temporaryData)
            .then(response => {console.log('이미지 삭제 완료');
            })
            .catch(err=>{console.log(err)});
}

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