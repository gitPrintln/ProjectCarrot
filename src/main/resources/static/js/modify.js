/**
 * 
 */

let initialImageData = []; // 초기 이미지 정보를 저장해두고 최종 수정하지 않거나 이탈할 경우 이용(전역 변수로 사용)

window.addEventListener('DOMContentLoaded', () => {
    const explainDiv = document.getElementById('explain'); // 이미지 업로드 부연설명창
     // 원래 저장되어 있던 포스트 이미지들
    if (selectedImage.childElementCount === 0) { // 이미 이미지가 불러온 이미지가 없으면 부연설명 on, 아니면 off(default가 off)
        explainDiv.style.display = "block";
    } else { // 불러온 이미지가 있으면, 최종 수정까지 하게 될 경우를 대비해서 이미지 정보 저장
        const initialImgs = selectedImage.querySelectorAll('img');
        for (let file of initialImgs) {
              const iniImgSrc = file.getAttribute('data-src');
              initialImageData.push(iniImgSrc);
        }
    }

    
    // * 최종이미지 DB에 저장할 dataForm(initialImgs를 제외한 추가된 이미지)
    let imageData = []; //  HTML <form> 요소의 데이터를 캡슐화하고, Ajax를 통해 서버로 전송하기 위해 사용
    
    // 수정 버튼 이벤트
    const btnUpdate = document.getElementById('btnUpdate');
    btnUpdate.addEventListener('click', function(){
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
        
        const result = confirm('정말 수정하시겠습니까?');

        if (result) {
            // 수정했음을 변수에 저장
            updated = true;
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
                const finalImgs = selectedImage.querySelectorAll('img');
                
                // Array.from()은 유사 배열 객체나 이터러블(iterable) 객체를 배열로 변환하는 메서드
                // Array.from(iterable, mapFn, thisArg) 이런 형태
                // iterable: 배열로 변환할 유사 배열 객체 또는 이터러블 객체
                // mapFn (선택적): 배열의 각 요소에 대해 호출될 맵핑 함수
                // thisArg (선택적): mapFn에서 사용할 this 값을 지정 
                // 로직 : 최종 저장하려는 이미지를 초기 저장되어있는 이미지와 비교를 하여
                // 초기 이미지에 포함되지 않은 이미지면 최종 저장 이미지에 올려서 저장을 하고
                // 초기 이미지에 포함된 이미지라면 초기 이미지에서 해당 이미지를 빼주고 남은 이미지를 추려냄
                // 반복하면 결과는,
                // 최종 저장하려는 이미지는 추가 저장과 초기 이미지는 삭제해야할 이미지만 남게 된다.
                for (let file of finalImgs) {
                    const imgSrc = file.getAttribute('data-src');
                    console.log(file);
                    if(!initialImageData.includes(imgSrc)){ // ㄱ-초기 이미지 데이터에 저장되어 있지 않은 추가하려고 하는 추가 이미지만 DB에 저장
                        imageData.push(imgSrc);
                    } else{ // ㄴ-초기 이미지 중 삭제된 이미지 가려내서 로컬저장소와 DB에 삭제
                        const indexToRemove = initialImageData.indexOf(imgSrc); // 해당 요소의 인덱스 값을 찾은 후
                        initialImageData.splice(indexToRemove, 1); // 인덱스부터 시작해서 1개의 값을 제거
                    }
                }
                
                // Promise 체이닝은 비동기 작업을 순차적으로 연결하여 처리하는 방식
                // 비동기 작업을 처리하기 위해 Promise를 사용하며, 이러한 Promise 객체들을 연속적으로 연결하여 작업을 수행하는 것을 Promise 체이닝
                axios.post('/img/upload/db', imageData)
                    .then(response => {
                        const imgIds = response.data.join(', ');
                        const imgs = document.getElementById('imgs');
                        const imgIdsForDbSave = `<div><input class="w3-input w3-border w3-hover-shadow w3-sand" id="imgIds" name="imgIds" value="${imgIds}" readonly/></div>`;
                        imgs.innerHTML += imgIdsForDbSave;
                        
                        if(initialImageData.length > 0) { // 삭제되어야할 데이터가 있으면 실행
                            axios.delete('/img/delete/db/' + initialImageData)
                                .then(response => {console.log('이미지 삭제 완료');
                                })
                                .catch(err=>{console.log(err)});
                        }
                    })
                    .catch(err => {alert(err+"!!!!");
                    });
                
           } else if(selectedImage.children.length === 0 && initialImageData.length > 0){ // 이미지가 있었지만 없앤 경우(원래 이미지를 모두 없애줌.)
                axios.delete('/img/delete/db/' + initialImageData)
                                .then(response => {console.log('이미지 삭제 완료');
                                })
                                .catch(err=>{console.log(err)});
           }
                
                
                
            // axios 작업 기다려 준 후 최종 제출.
            setTimeout(function(){
                // (3) 그 외 나머지 정보 DB에 저장.
                    document.querySelector('#formModify').submit();
                    formModify.action = '/sell/modify';
                    formModify.method = 'post';
                    formModify.submit();
            }, 100);
            
        }
        
    });
    
    
    // 삭제 버튼 이벤트
    const btnDelete = document.getElementById('btnDelete');
    btnDelete.addEventListener('click', function(){
        const result = confirm('글을 삭제하게 되면 현재 판매와 관련된 채팅방은 삭제됩니다. 정말 삭제하시겠습니까?');
        if(result){
            alert('글을 삭제했습니다.')
            document.querySelector('#formModify').submit();
            formModify.action = '/sell/delete';
            formModify.method = 'post';
            formModify.submit();
        }
    });
    
    
    // region-input 창 클릭해도 열리게
    const regionMainInput = document.getElementById('regionMain');
    regionMainInput.addEventListener('click', serchRegion);
    
});

let updated = false; // 수정 버튼 안누르고 다른 행위를 할 경우 이미지 정보를 원상복구시키기 위해

// 수정하지 않고 페이지를 벗어날 시 발생하는 이벤트 리스너(실제 등록되지 않은 로컬저장소 이미지 삭제 - 원래이미지는 그냥 둠)
window.addEventListener('beforeunload', function(event) {
    const selectedImage = document.querySelector('#selectedImage');
    if(!updated && selectedImage.children.length > 0){ // 수정하지 않았을 때, 등록하려던 이미지가 있을 때만 실행
    deleteTemporaryFile();
    }
});

// 이미지를 추가하려고 시도했지만 최종 등록 되지 않은 이미지 삭제(이미 있는 이미지는 건드리지 않음)
function deleteTemporaryFile(){
    let temporaryData = [];
    const selectedImage = document.querySelector('#selectedImage');
    const addedImgs = selectedImage.querySelectorAll('img');
        addedImgs.forEach(file => {
            const imgSrc = file.getAttribute('data-src');
            if (!initialImageData.includes(imgSrc)) { // 초기 이미지 데이터에 저장되어 있지않은 추가하려고 시도했던 이미지만 삭제행
                temporaryData.push(imgSrc);
            }
        });
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

// 상태 바꿔주는 함수
function changeSts(event){
    const status = document.querySelector('#status');
    const selectOption = event.target.text;
    status.value = selectOption;
}