/**
 * 
 */
 
window.addEventListener('DOMContentLoaded', () => {
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