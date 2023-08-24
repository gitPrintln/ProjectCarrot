/**
 * 
 */
 
window.addEventListener('DOMContentLoaded', () => {
    
    const imgChangeBtn = document.querySelector('#imgChangeBtn'); // 이미지 변경완료 버튼
    const imgCloseBtn = document.querySelector('.imgCloseBtn'); // 모달창 종료 버튼
    const imgInput = document.querySelector('#imgFile'); // 이미지 미리보기에 사용
    const previewProfileImg = document.querySelector('#previewProfileImg'); // 현재 이미지 태그
    const initialProfileImgSrc = previewProfileImg.src; // 원래 내 이미지 src를 저장(변경 취소했을 경우)
    const userImage = document.querySelector('#userImage').value; // 저장되어 있는 유저 이미지
    const initialNickName = document.querySelector('#nickName').value; // 초기 저장되어 있던 닉네임
    
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
        axios.delete('/img/upload/' + userImage)
            .then(response => {
                console.log("원래 이미지는 로컬에서 삭제됨.");
            })
            .catch(error => {
                console.log(error);
            });
        axios.post('/img/upload/profile', file)
            .then(response => { 
                alert('프로필 이미지 변경 완료했습니다.');
                imgCloseBtn.click();
                location.reload();
                })
            .catch(error => {
                alert('파일 확장자는 jpeg, jpg, png, jfif 타입만 가능하며 최대 10MB을 넘지 않는지 확인해 주세요.')
                console.log(error)
                });
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
    const pwAva = document.querySelector('#pwAvailable');
    const pwUnava = document.querySelector('#pwUnavailable');
    function isCorrect(){
        if(newPw.value != "" && newPwChk.value != "" && newPw.value === newPwChk.value){
            pwAva.style.display = "block";
            pwUnava.style.display = "none";
        } else {
            pwAva.style.display = "none";
            pwUnava.style.display = "block";
        }
        isCorrect // 비밀번호 일치여부를 상시 체크하기 위해 실행.
    }
    
    // 비밀번호 변경 버튼 활성화(둘 다 일치할 경우만 활성화)
    newPw.addEventListener('focus', activePwBtn);
    newPwChk.addEventListener('focus', activePwBtn);
    newPw.addEventListener('blur', activePwBtn);
    newPwChk.addEventListener('blur', activePwBtn);
    function activePwBtn(){
        if(pwAva.style.display === "block"){
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
                alert('비밀번호를 변경했습니다. 다시 로그인 해주세요.');
                pwModalCloseBtn.click();
                location.href="/logout" // 로그아웃하지 않으면 DB에 새로 변경된 값이 저장되더라도 이전 값이 계속 적용됨.
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
      pwAva.style.display = "none";
      pwUnava.style.display = "none";
      pwChangeBtn.disabled = true;
    });
    
    // 닉네임 중복 확인 이벤트
    const nickCheckBtn = document.querySelector('.nickCheckBtn');
    const nickAva = document.getElementById('nickAva');
    const nickUnava = document.getElementById('nickUnava');
    nickCheckBtn.addEventListener('click', function(){
        const nickName = document.getElementById('nickName').value;
        axios.get('/user/nicknameChk', {
            params: {
                nickName: nickName
            }
        })
            .then(response => {
                if(response.data){
                    nickAva.style.display = "block";
                    nickUnava.style.display = "none";
                    alert("멋진 닉네임이네요!");
                    return;
                } else{
                    nickAva.style.display = "none";
                    nickUnava.style.display = "block";
                    alert("불가능한 닉네임이네요!");
                    return;
                }
            })
            .catch(error => { alert('오류체크 부탁드립니다.' + error)});
    });
    
    // 수정완료 버튼 이벤트
    const submitBtn = document.querySelector('.submitBtn');
    submitBtn.addEventListener('click', () =>{
        const nick = document.getElementById('nickName').value;
        const phone = document.getElementById('phone').value;
        const formUpdate = document.getElementById('formUpdate');
        const result = confirm('정말 수정하시겠습니까?')
        
        if(result){
            alert("정보 수정 완료를 위해 다시 로그인 해주세요!")
            if(nick != "" && phone != "" && phone.length == 13 
            && (nickAva.style.display === "block" || initialNickName === nick)){ // 필수 입력란이 비어있지 않고, 폰번호는 13자리이며, 닉네임 중복체크 통과한 경우(닉네임이 변함 없으면 무관)에만 제출함.
                formUpdate.action = '/user/update';
                formUpdate.method = 'post';
                formUpdate.submit();
            } else if(nick == "" || phone == "") {
                alert("빨간색으로 표시된 부분은 필수 입력 사항입니다.");
                return;
            } else if(phone.length != 13) {
                alert("휴대폰 번호를 정확히 입력해 주세요.");
                return;
            } else if(nickAva.style.display === "none"){
                alert("닉네임 중복 확인을 해주세요.");
                return;
            } 
        }
    });
    
});
    // 이미지 모달, 비밀번호 모달 띄우기
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