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
    
    
    
    imageInput.addEventListener('change', (event) => {
        const files = event.target.files; // 파일 리스트들에 대한 정보를 files에 담음
        uploadImages(files);
    });
    
    // 이미지 업로드
    function uploadImages(files) {
        axios.post('/img/upload', files)
            .then(drawInput)
            .catch(err => { alert(err + '인데요, 확인해보세요!!') });
    }
    
    function drawInput(response) {
        if(response.status == 200){ // 성공적으로 이미지들을 가져왔을 때
        
        // 올릴 예정인 파일 미리보기 추가
        response.data.forEach(responseImgData => {
                let image = '';    
                image = `<img src="/img/view/${responseImgData.imageFileName}" data-src="${responseImgData.imageFileName}" width="150" height="150"/>`;
                const htmlStr = `<div class="card" style="display:inline-block;">
                                    <div class="card-header">
                                        <button type="button" data-imgSrc="${responseImgData.imageFileName}" class="btnDelete btn-close" aria-label="Close"></button>
                                        <div style="display:inline-block;">${responseImgData.originFileName}</div>
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
            });
        }
        
    }
    
    // 선택한 이미지 제거
    function deleteImg(event) {
        event.preventDefault();
        
        const fileName = event.target.getAttribute('data-imgSrc');
        console.log(fileName);
        // * 최종적으로 등록된 이미지들을 DB에 제출할 dataForm에 delete
        axios.delete('/img/upload/' + fileName)
            .then(response => {
                console.log('삭제성공'+ response.data);
                event.target.closest('.card').remove(); // 가까운 카드 이미지 선택해서 제거
                /*function(){
                // 올릴 예정인 파일 미리보기 삭제
                const selectedParent = event.target.parentNode; // 선택한 버튼의 부모요소
                const deleteSelectedCard = selectedParent.parentNode.parentNode; // 선택한 버튼의 부모의 부모의 요소
                deleteSelectedCard.removeChild(selectedParent.parentNode);}*/
            })
            .catch(err => { alert(err + '인데요, 확인해보세요!!') });
    }
    
    
});
    