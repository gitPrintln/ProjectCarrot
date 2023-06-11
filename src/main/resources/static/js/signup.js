/**
 * 
 */
 
 
window.addEventListener('DOMContentLoaded', () => {
    
    // username 중복체크
    const userInput = document.getElementById('username');
    userInput.addEventListener('change', e => {
        const username = userInput.value;
        axios.get('/user/idChk', {
                params: {
                    username: username
                }
            })
            .then(response => {
            const ava = document.querySelector('.idAvailabe');
            const unava = document.querySelector('.idUnavailabe');
            if(response.data){
                ava.style.display = "";
                unava.style.display = "none";
            } else {
                unava.style.display = "";
                ava.style.display = "none";
            }
            })
            .catch(err=>{console.log(err);});
    })
    
    // password 일치 여부 확인
    const password = document.getElementById('password');
    const passwordChk = document.getElementById('passwordChk');
    password.addEventListener('input', passwordCoincide);
    passwordChk.addEventListener('input', passwordCoincide);
    
    function passwordCoincide(){
    const pwAva = document.querySelector('.pwAvailabe');
    const pwUnava = document.querySelector('.pwUnavailabe');
    if(password.value === passwordChk.value){
        pwAva.style.display = "";
        pwUnava.style.display = "none";
    } else {
        pwAva.style.display = "none";
        pwUnava.style.display = "";
    }
    passwordCoincide
    }
    
    // nickname 중복 체크
    const nicknameInput = document.getElementById('nickName');
    nicknameInput.addEventListener('change', e => {
        const nickName = nicknameInput.value;
        axios.get('/user/nicknameChk', {
                params: {
                    nickName: nickName
                }
            })
            .then(response => {
            const ava = document.querySelector('.nickAvailabe');
            const unava = document.querySelector('.nickUnavailable');
            if(response.data){
                ava.style.display = "";
                unava.style.display = "none";
            } else {
                unava.style.display = "";
                ava.style.display = "none";
            }
            })
            .catch(err=>{console.log(err);});
    })
    
    // phone number 숫자로 입력
    const phoneInput = document.getElementById('phone');
    phoneInput.addEventListener('input', inNumber);
    function inNumber(){
        // 숫자와 하이픈(-) 체크용도
        const check = /^[0-9-]*$/;
        const phoneValue = phoneInput.value;
        if(!check.test(phoneValue)){
            alert("숫자로만 입력하세요!!");
            phoneInput.value = "";
            return;
        }
        
        // 숫자 외에 전부 제거
        var phone = phoneValue.replace(/\D/g, '');
        // 하이픈 추가
        if (phone.length > 3 && phone.length <= 7) { // 첫번째 하이픈
            phone = phone.replace(/(\d{3})(\d{1,4})/, '$1-$2');
        } else if (phone.length > 7) { // 두번째 하이픈
            phone = phone.replace(/(\d{3})(\d{4})(\d{1,4})/, '$1-$2-$3');
        }
        
        // 포맷팅된 번호 설정
        phoneInput.value = phone;
    }
    
    // TODO: 여기서부터 수정해야함
    btnActive();
    // 모두 잘 입력했을 경우 가입하기 버튼 활성화
    function btnActive(){
        const idAcc = document.querySelector('.idAvailable');
        const pwAcc = document.querySelector('.pwAvailable');
        const nickAcc = document.querySelector('.nickAvailable');
        if((idAcc && idAcc.style.display !== "none") &&
           (pwAcc && pwAcc.style.display !== "none") &&
           (nickAcc && nickAcc.style.display !== "none")){
            alert("check");
        }
    }
});