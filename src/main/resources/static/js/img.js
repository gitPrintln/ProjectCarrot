/** 이미지 테스트 중..
 * 
 */
 
window.addEventListener('DOMContentLoaded', () => {
    
    const imageInput = document.getElementById('images');
    const selectedImage = document.getElementById('selectedImage');

    imageInput.addEventListener('change', (event) => {
        const files = event.target.files; // 파일 리스트들에 대한 정보를 files에 담음
        const fileReader = new FileReader(); // 파일reader로 파일 읽기 위해서
        
        for (let i =0; i< files.length; i++){
            const file = files[i];
            
            fileReader.onload = (e) => {
                const imageDataUrl = e.target.result;
                displayImage(imageDataUrl);
            };

        fileReader.readAsDataURL(file); // URL로 파일 띄우기
        }
    });
    
    // 선택한 이미지 파일 보여주는 함수
    function displayImage(imageDataUrl) {
        const imageTag = document.createElement('img');
        imageTag.src = imageDataUrl;
        imageTag.alt = '선택한 이미지';
        imageTag.width = '150';
        selectedImage.appendChild(imageTag);
    }
    
    
    
    
    
    
    
    
    
    
});