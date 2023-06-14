/** 이미지
 * 
 */
 
window.addEventListener('DOMContentLoaded', () => {
    
    const imageInput = document.getElementById('images'); // 이미지 인풋창
    const selectedImage = document.getElementById('selectedImage'); // 선택한 이미들이 보여질 공간
    
    // * 최종적으로 등록된 이미지들을 DB에 제출할 dataForm
    let imageData = new FormData(); //  HTML <form> 요소의 데이터를 캡슐화하고, Ajax를 통해 서버로 전송하기 위해 사용
    let count = 0; // imageData에 저장할 file 고유 번호 설정(번호로 삭제)  
            
    // 이미지 등록 여부에 따라서 부연 설명 on/off 해줌
    var targetDiv = document.getElementById('selectedImage'); // 관찰할 타겟 div
    var observer = new MutationObserver(function(mutations) { // 자식요소가 추가되거나 생기는 것(변화)을 관찰함
        mutations.forEach(function(mutation) {
            if (mutation.type === "childList") {
                const explainDiv = document.getElementById('explain');
                if (targetDiv.childElementCount === 0) { // 이미지 개수가 하나라도 등록되어 있지 않으면
                    explainDiv.style.display = "block"; // 부연 설명 on
                } else { // 이미지 개수가 하나라도 등록되어 있으면
                    explainDiv.style.display = "none"; // 부연 설명 off
                }
            }
        });
    });
    var explainConfig = { childList : true }; // 부연설명에 대한 환경 설정, childList는 자식 요소 추가 or 제거 관찰할지 여부
    observer.observe(targetDiv, explainConfig);
    
    
    imageInput.addEventListener('change', (event) => {
        const files = event.target.files; // 파일 리스트들에 대한 정보를 files에 담음
        
        // * 최종적으로 등록된 이미지들을 DB에 제출할 dataForm에 save
        const imageFileInput = document.querySelector('input[name="images"]');
        console.log(imageFileInput.files);
        // Array.from()은 유사 배열 객체나 이터러블(iterable) 객체를 배열로 변환하는 메서드
        // Array.from(iterable, mapFn, thisArg) 이런 형태
        // iterable: 배열로 변환할 유사 배열 객체 또는 이터러블 객체
        // mapFn (선택적): 배열의 각 요소에 대해 호출될 맵핑 함수
        // thisArg (선택적): mapFn에서 사용할 this 값을 지정        
        Array.from(imageFileInput.files).forEach(f => {
            imageData.append('files['+count+']', f);
            count++;
        });
        for (var pair of imageData.entries()) {
                console.log(pair[0]+ ', ' + pair[1]); 
            }
        count = count - imageFileInput.files.length; 
            
        // 올릴 예정인 파일 미리보기 추가
        for (let i =0; i< files.length; i++){
            const file = files[i];
            const fileReader = new FileReader(); // 복수 선택 경우 각 파일들을 각각의 reader로 파일 읽기 위해서
            
            // 파일 로드 처리할 작업 수행
            fileReader.onload = (e) => { 
                const imageUrl = e.target.result;
                const fileNameWithExtension = file.name; // 파일 이름 가져오기
                const fileName = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.')); // 파일이름 확장자 제거
                let image = '';
                    
                image = `<img src="${imageUrl}" width="150" height="150"/>`;
                const htmlStr = `<div class="card" style="display:inline-block;">
                                    <div class="card-header">
                                        <button type="button" data-num="files[${count}]" class="btnDelete btn-close" aria-label="Close"></button>
                                        <div style="display:inline-block;">${fileName}</div>
                                    </div>
                                    <div class="card-body">
                                        ${image}
                                    </div>
                                </div>`;
                selectedImage.innerHTML += htmlStr;
                count++;
                    // x버튼을 눌렀을 때 제거
                    document.querySelectorAll('.btnDelete').forEach(btnDel => {
                        btnDel.addEventListener('click', deleteImg);
                    });
                     
            };
            
        fileReader.readAsDataURL(file); //  파일을 읽기 시작
        }
    });
    
    // 이미지 등록 버튼 눌렀을 때
    const btnImg = document.getElementById('btnImg');
    btnImg.addEventListener('click', uploadImages);
    
    // 이미지 업로드
    function uploadImages() {
        for (const [key, value] of imageData) {
        output.textContent += `${key}: ${value}\n`;
        }
        axios.post('/img/upload', imageData)
            .then(drawInput)
            .catch(err => { alert(err + '인데요, 확인해보세요!!') });
    }
    function drawInput(response) {
        const imgIds = response.data.join(', ');
        const imgs = document.getElementById('imgs');
        const imgIdsForDbSave = `<div><input class="w3-input w3-border w3-hover-shadow w3-sand" id="imgIds" name="imgIds" value="${imgIds}" readonly/></div>`;
        imgs.innerHTML += imgIdsForDbSave;
    }
    
    // 선택한 이미지 제거
    function deleteImg(event) {
        event.preventDefault();
        
        const dataNum = event.target.getAttribute('data-num');
        console.log(dataNum)
        // * 최종적으로 등록된 이미지들을 DB에 제출할 dataForm에 delete
        imageData.delete(dataNum);
        for (var pair of imageData.entries()) {
                console.log(pair[0]+ ', ' + pair[1]); 
            }
        // 올릴 예정인 파일 미리보기 삭제
        const selectedParent = event.target.parentNode; // 선택한 버튼의 부모요소
        const deleteSelectedCard = selectedParent.parentNode.parentNode; // 선택한 버튼의 부모의 부모의 요소
        deleteSelectedCard.removeChild(selectedParent.parentNode);
        
        
    }
    
    
});
    