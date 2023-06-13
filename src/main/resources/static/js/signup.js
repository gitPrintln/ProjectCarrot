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
            const ava = document.querySelector('#idAvailable');
            const unava = document.querySelector('#idUnavailable');
            if(response.data){
                ava.style.display = "block";
                unava.style.display = "none";
            } else {
                unava.style.display = "block";
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
    const pwAva = document.querySelector('#pwAvailable');
    const pwUnava = document.querySelector('#pwUnavailable');
    if(password.value != "" && passwordChk.value != "" && password.value === passwordChk.value){
        pwAva.style.display = "block";
        pwUnava.style.display = "none";
    } else {
        pwAva.style.display = "none";
        pwUnava.style.display = "block";
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
            const ava = document.querySelector('#nickAvailable');
            const unava = document.querySelector('#nickUnavailable');
            if(response.data){
                ava.style.display = "block";
                unava.style.display = "none";
            } else {
                unava.style.display = "block";
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
    

    // 아이디 입력창 포커스 되는 순간 할 일(회원가입 버튼 활성화를 할 지 말지)
    const requiredInputs = document.querySelectorAll('.requiredInput');
    Array.from(requiredInputs).forEach(function(input) {
        input.addEventListener('focus', btnActive);
        input.addEventListener('blur', btnActive);
    });
    
    // 모두 잘 입력했을 경우 가입하기 버튼 활성화
    function btnActive(){
        const idAcc = document.getElementById('idAvailable');
        const pwAcc = document.querySelector('#pwAvailable');
        const nickAcc = document.querySelector('#nickAvailable');
        const name = document.querySelector('#name').value;
        const phone = document.querySelector('#phone').value;
        const btnSubmit = document.querySelector('#btnSubmit');
        setTimeout(function(){
            if(idAcc.style.display === "block" && pwAcc.style.display === "block" &&
               nickAcc.style.display === "block" && name != '' && phone.length == 13){
                console.log('활성화');
                btnSubmit.disabled = false;
            } else{
                console.log('비활성화');
                btnSubmit.disabled = true;
            }
            }, 20);
    }
    
    
    /* 포커싱 이동으로 이전 입력창에서 enter 누른 효과
    function handleFocus(event) {
        console.log("handlefocus 들어옴.")
        const idAcc = document.getElementById('idAvailable');
        console.log("idAvailable 상태^");
        console.log(idAcc);
        if (prevInput !== undefined) {
            console.log("prevInput =>  ");
            console.log(prevInput);
            console.log("prevInput End");
            var simulatedEvent = new KeyboardEvent('keydown', { key: 'Enter' });
            prevInput.dispatchEvent(simulatedEvent);
        }
        prevInput = event.target;
        console.log("idAvailable 상태^")
        console.log(idAcc);
    }
    */
    
    const submitBtn = document.getElementById('btnSubmit');
    submitBtn.addEventListener('click', function(){
        const result = confirm('정말 가입하시겠습니까?')
        if(result){
        document.getElementById('formRegister').submit();
        }
    });
});