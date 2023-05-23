/** 이미지
 * 
 */
 
window.addEventListener('DOMContentLoaded', () => {
    
    const imageInput = document.getElementById('images'); // 이미지 인풋창
    const selectedImage = document.getElementById('selectedImage'); // 선택한 이미들이 보여질 공간
    
    
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
    
    
    //var imgFiles = []; // 올릴 이미지 파일들 저장할 최종 배열
    imageInput.addEventListener('change', (event) => {
        const files = event.target.files; // 파일 리스트들에 대한 정보를 files에 담음
        
        
        for (let i =0; i< files.length; i++){
            const file = files[i];
            //imgFiles.push(file); // 선택된 파일 목록에서 추가
            
            const fileReader = new FileReader(); // 복수 선택 경우 각 파일들을 각각의 reader로 파일 읽기 위해서
            
            
            
            // 파일 로드 처리할 작업 수행
            fileReader.onload = (e) => { 
                const imageDataUrl = e.target.result;
                const fileNameWithExtension = file.name; // 파일 이름 가져오기
                const fileName = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.')); // 파일이름 확장자 제거
                let image = '';
                    
                image = `<img src="${imageDataUrl}" width="150" height="150"/>`;
                const htmlStr = `<div class="card" style="display:inline-block;">
                                    <div class="card-header">
                                        <button type="button" class="btnDelete btn-close" aria-label="Close"></button>
                                        <div style="display:inline-block;">${fileName}</div>
                                    </div>
                                    <div class="card-body">
                                        ${image}
                                    </div>
                                </div>`;
                selectedImage.innerHTML += htmlStr;

    
                    // x버튼을 눌렀을 때 제거
                    document.querySelectorAll('.btnDelete').forEach(btnDel => {
                        btnDel.addEventListener('click', deleteImg);
                    });
                     
            };
        fileReader.readAsDataURL(file); //  파일을 읽기 시작
        
        //filesHiddenInputTest() // 최종으로 올릴 파일들을 저장해 둠.
        }
        
    });
    
    
    /*
    // 선택한 이미지 파일 보여주는 함수
    function displayImage(imageDataUrl) {
        const imageTag = document.createElement('img');
        imageTag.src = imageDataUrl;
        imageTag.alt = '선택한 이미지';
        imageTag.width = '150';
        selectedImage.appendChild(imageTag);
    }
    */
    
    
    // 선택한 이미지 제거
    function deleteImg(event) {
        event.preventDefault();
        
        //imgFiles.splice(index, 1) // 선택된 파일 목록에서 해당 인덱스 제거
        
        const selectedParent = event.target.parentNode; // 선택한 버튼의 부모요소
        const deleteSelectedCard = selectedParent.parentNode.parentNode; // 선택한 버튼의 부모의 부모의 요소
        deleteSelectedCard.removeChild(selectedParent.parentNode);
        
        
    }
    
    /*function filesHiddenInputTest(){
        var filesHiddenInput = document.getElementById('images'); // img input 태그 찾기.
        filesHiddenInput.value = imgFiles; // 이미지 파일들을 hiddenInput창에 넣기
    }*/
    
    
    
});
/*
// 이미지 파일 인풋창 동적 생성
function img() {
        const imgPlace = document.querySelector('.imgContents');
        console.log(imgPlace)
        const imgInput = '<div style="display:none;"><input type="file" class="imgInputs w3-input w3-border w3-sand" id="images" name="images" multiple/></div>';
        imgPlace.innerHTML += imgInput;
    }
    */
    