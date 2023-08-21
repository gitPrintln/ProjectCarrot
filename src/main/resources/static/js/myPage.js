/**
 * 
 */
 
window.addEventListener('DOMContentLoaded', () => {
    
    const imgChangeBtn = document.querySelector('#imgChangeBtn'); // 이미지 변경완료 버튼
    const imgCloseBtn = document.querySelector('.imgCloseBtn'); // 모달창 종료 버튼
    const imgInput = document.querySelector('#imgFile'); // 이미지 미리보기에 사용
    const previewProfileImg = document.querySelector('#previewProfileImg'); // 현재 이미지 태그
    const initialProfileImgSrc = previewProfileImg.src; // 원래 내 이미지 src를 저장(변경 취소했을 경우)
    
    // 변경할 프로필 이미지 미리보기
    imgInput.addEventListener('change', (event) => {
        const previewImg = event.target.files[0];
        
        if (previewImg) {
        // file 미리보기 위해 파일리더를 이용
        const reader = new FileReader();
        reader.onload = function(event) {
            previewProfileImg.src = event.target.result;
        };
        reader.readAsDataURL(previewImg);
        }
    })
    
    // 프로필 이미지 변경 완료 이벤트
    imgChangeBtn.addEventListener('click', function(){
        const imgFile = document.querySelector('#imgFile');
        /* FormData로 전송하면 서버측에서 multipartFile로 받아서 처리
        이미지 파일을 FormData에 넣는 이유는 HTTP 요청을 보낼 때 파일 업로드와 같은 멀티파트 요청을 생성하기 위해서
        일반적으로 웹 애플리케이션에서 파일을 업로드하거나 다른 형태의 바이너리 데이터를 전송해야 할 때, 이 데이터를 멀티파트 형식으로 인코딩하여 서버로 전송
        */
        const file = new FormData();
        file.append('file', imgFile.files[0]);
        
        axios.post('/img/upload/profile', file)
            .then(response => { 
                alert('프로필 이미지 변경 완료했습니다.');
                imgCloseBtn.click();
                location.reload();
                })
            .catch(error => {
                alert('파일 확장자는 jpeg, jpg, png, jfif 타입만 가능하며 최대 10MB을 넘지 않는지 확인해 주세요.')
                console.log(error)
                })
    });
    
    // 이미지 모달창 닫을 때 데이터 초기화 해주기
    imgCloseBtn.addEventListener('click', () =>{
        previewProfileImg.src = initialProfileImgSrc; // 초기 프로필 이미지로 원상태로 돌리기
        imgInput.value = '';
    })
    
    
    const nowPw = document.querySelector('#nowPassword');
    const newPw = document.querySelector('#password');
    const newPwChk = document.querySelector('#passwordChk');
    const pwChangeBtn = document.querySelector('#pwChangeBtn'); // 비밀번호 변경완료 버튼
    const pwModalCloseBtn = document.querySelector('.pwCloseBtn'); // 모달창 종료 버튼
    
    
    // 새로운 비밀번호 일치 여부
    newPw.addEventListener('input', isCorrect);
    newPwChk.addEventListener('input', isCorrect);
    const ava = document.querySelector('#pwAvailable');
    const unava = document.querySelector('#pwUnavailable');
    function isCorrect(){
        if(newPw.value != "" && newPwChk.value != "" && newPw.value === newPwChk.value){
            ava.style.display = "block";
            unava.style.display = "none";
        } else {
            ava.style.display = "none";
            unava.style.display = "block";
        }
        isCorrect // 비밀번호 일치여부를 상시 체크하기 위해 실행.
    }
    
    // 비밀번호 변경 버튼 활성화(둘 다 일치할 경우만 활성화)
    newPw.addEventListener('focus', activePwBtn);
    newPwChk.addEventListener('focus', activePwBtn);
    newPw.addEventListener('blur', activePwBtn);
    newPwChk.addEventListener('blur', activePwBtn);
    function activePwBtn(){
        if(ava.style.display === "block"){
            pwChangeBtn.disabled = false;
        } else{
            pwChangeBtn.disabled = true;
        }
    }
    
    // 비밀번호 변경 완료 이벤트
    pwChangeBtn.addEventListener('click', function(){
        const data = {
            'nowPw': nowPw.value,
            'newPw': newPw.value
        };
        axios.post('/user/pwUpdate', data)
        .then(response => {
            if(response.data){
                alert('비밀번호를 변경했습니다.');
                pwModalCloseBtn.click();
            } else{
                alert('현재 비밀번호가 일치하지 않거나 새로운 비밀번호가 현재 비밀번호와 동일합니다.')
            }
        })
        .catch(error => {console.log(error)});
    });
    
    // 비밀번호 모달창 닫을 때 데이터 초기화해주기
    pwModalCloseBtn.addEventListener('click', () => {
      nowPw.value = ''; 
      newPw.value = ''; 
      newPwChk.value = ''; 
      ava.style.display = "none";
      unava.style.display = "none";
      pwChangeBtn.disabled = true;
    });
});
    // 이미지 모달 비밀번호 모달 띄우기
    const imageModal = document.querySelector('#imageModal');
    const imgModal = new bootstrap.Modal(imageModal);
    function imgUpdate(){
        imgModal.show();
    }
    const passwordModal = document.querySelector('#pwModal');
    const pwModal = new bootstrap.Modal(passwordModal);
    function pwUpdate(){
        pwModal.show();
    }