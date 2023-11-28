/**
 * 
 */
 
 
window.addEventListener('DOMContentLoaded', function () {
    
    
});
function toggleTitle(id){
    const selectTitle = document.getElementById('detail' + id);
    if(selectTitle.style.display === 'block'){
        selectTitle.style.display = 'none';
    } else{
        selectTitle.style.display = 'block';        
    }
}
let vParagraph = ''; // 다음에 바뀔 문단
function toggleDetails(id) {
    // 선택된 부분을 보이게 해줌. 기존에 있던 부분은 안보이게 바꿔줌.
    if(vParagraph != ''){
        const deleteDetails = document.getElementById('detail' + vParagraph);
        deleteDetails.style.display = 'none';
    }
    const selectedDetails = document.getElementById('detail' + id);
    selectedDetails.style.display = 'block';
    vParagraph = id; // 다음에 바뀔 구문 저장해두기
    
}