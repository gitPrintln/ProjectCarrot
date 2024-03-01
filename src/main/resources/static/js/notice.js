/**
 * 
 */
 
 
window.addEventListener('DOMContentLoaded', function () {
    const inquiryTitle = document.querySelector('#inquiryTitle');
    const inquiryType = document.querySelector('#inquiryType');
    const inquiryContent = document.querySelector('#inquiryContent');
    // 문의 남기기 모달 event
    const leaveBtn = document.querySelector('#leaveBtn');
    leaveBtn.addEventListener('click', function(){
        
        if (inquiryTitle.value == '' || inquiryType.value == '' || inquiryContent.value == '') {
            alert('빠진 부분을 채워넣어주세요!');
            return;
        }
        const result = confirm('정말 문의남기시겠습니까?');
        
        if(result){
                    const inquiryForm = document.querySelector('#inquiryForm');
                    inquiryForm.action = '/board/post';
                    inquiryForm.method = 'post';
                    inquiryForm.submit();
                    
                    alert('남긴 문의는 문의 내역에서 확인가능합니다.');
        }
    });

    const postTitle = document.querySelector('#postTitle');
    const postCategory = document.querySelector('#postCategory');
    const postContent = document.querySelector('#postContent');
    // 게시글 남기기 모달 event
    const postCreateBtn = document.querySelector('#postCreateBtn');
    postCreateBtn.addEventListener('click', function(){
        
        if (postTitle.value == '' || postCategory.value == '' || postContent.value == '') {
            alert('빠진 부분을 채워넣어주세요!');
            return;
        }
        const result = confirm('정말 글을 작성하시겠습니까?');
        
        if(result){
                    const postCreateForm = document.querySelector('#postCreateForm');
                    postCreateForm.action = '/board/post';
                    postCreateForm.method = 'post';
                    postCreateForm.submit();
                    
                    alert(postCategory.value + '글 작성 완료');
        }
    });
    
    // 문의 남기기 모달창 그냥 닫을 때 데이터 초기화 해주기
    const inquiryModalCloseBtn = document.querySelector('#inquiryModalCloseBtn');
    inquiryModalCloseBtn.addEventListener('click',() => {
         inquiryTitle.value = '';
         inquiryContent.value = '';
         inquiryType.value = '비매너/불량 유저';
    });
    // 글 남기기 모달창 그냥 닫을 때 데이터 초기화 해주기
    const postModalCloseBtn = document.querySelector('#postModalCloseBtn');
    postModalCloseBtn.addEventListener('click',() => {
         const noteEditable = document.querySelector('.note-editable'); // summernote 안의 내용 비우기위해
         postTitle.value = '';
         noteEditable.innerHTML = '';
         postCategory.value = '전체공지';
    });
    
    // 내가 작성한 글이면 x 버튼 눌렀을 때 글을 삭제할 수 있도록
    const postContentDeleteBtns = document.querySelectorAll('#postContentDeleteBtn');
    postContentDeleteBtns.forEach(deleteBtn => {
        deleteBtn.addEventListener('click', (event) => {
        const postContentId = event.target.getAttribute('data-pid');
               const result = confirm('글을 삭제하면 복구할 수 없습니다. 정말 삭제하시겠습니까?');
               if(result){
                   deleteNoticePost(postContentId); 
               }
        });
    });
});

// 전체공지, 자유게시판, FAQ
function community(event, category){
    // 링크의 기본 동작 막기
    event.preventDefault();
    if(category === '내 문의 내역'){ // 로그인 이용 서비스일 경우
        isUserLoggedIn()
            .then(loggedIn =>{
                if(!loggedIn){ // 로그인 되어 있지 않다면
                    alert('로그인 후 이용 부탁드립니다.');
                    // 로그인 페이지로 리디렉션
                    window.location.href = '/user/signin';
                } else{ // 로그인 되어 있다면
                    category = '문의';
                    updateMain(category);
                }
            })
            .catch(err => {
                console.error('로그인 상태 확인 중 오류:', err);
            });
    }else{ // 로그인 이용 상관 없는 서비스일 경우
        updateMain(category);
    }
}

// 게시판 내용 바꿔주기(axios)
function updateMain(category, page){
    const boardTitle = document.querySelector('#boardTitle');
    const boardContent = document.querySelector('#boardContent');
    axios.get('/board/notice/community/' + category, { params: { page : page}})
        .then(response => {
            // 해당 게시판의 불러온 데이터
            const data = response.data;
            // 게시판 타이틀 바꿔주기
            boardTitle.innerHTML = data.category;
            // 기존의 게시판 내용 비우기, 페이징 비우기
            boardContent.innerHTML = '';
            const pagination = document.querySelector('#pagination');
            pagination.innerHTML = '';
            
            if(data.entity.content.length != 0){ // 해당 게시판의 데이터가 하나라도 있으면
                // 로그인 유저 확인 후 작성자와 같은 id이면 삭제 버튼 생성(input은 string으로 변환되어 저장되므로 형 변환해줌)
                const loginusersidValue = document.getElementById('loginUsersid').value;
                const loginUsersid = parseInt(loginusersidValue, 10);

                // 게시판 내용 바꿔주기
                for(var i = 0; i < data.entity.content.length; i++){
                        // 새로운 게시글 틀 생성
                        var div = document.createElement('div');
                        boardContent.appendChild(div);
                        // 게시글 틀 설정
                        div.className = 'listContainer w3-twothird w3-container';
                        
                        // 삭제 버튼(조건에 따라 버튼 생성), 문의 내역은 삭제 버튼이 없음
                        if(data.entity.content[i].userId === loginUsersid && category !== '문의'){
                            var button = document.createElement('button');
                            button.type = 'button';
                            button.id = 'postContentDeleteBtn';
                            button.className = 'btn-close';
                            button.setAttribute('data-pid', data.entity.content[i].id);
                            // 생성한 button을 div에 추가
                            div.appendChild(button);
                        }
                        
                        // h3 요소(제목)를 생성하고 제목 데이터를 추가
                        var h3 = document.createElement('h3');
                        h3.className = 'w3-text-teal';
                        h3.innerText = data.entity.content[i].title;
                        
                        // p 요소(내용)를 생성하고 내용 데이터를 추가
                        var span = document.createElement('span');
                        span.innerText = data.entity.content[i].content;
                        
                        // 생성한 h3와 span를 div에 추가
                        div.appendChild(h3);
                        div.appendChild(span);
                }
                
                // 페이징 새 게시판에 맞게 적용시키기
                let str = '';
                if(data.currentPage != 0){
                    str += '<a class="w3-button w3-hover-black" href="#" onclick="goToPage(event, ' + (data.currentPage - 1)+ ')">«</a>';
                } else{
                    str += '<a class="w3-button" href="#" style="pointer-events: none; color: gray; text-decoration: none;">«</a>';
                }
                for(var i = data.startPage; i <= data.endPage; i++){
                    str += '<a class="w3-button ';
                        if(i == data.currentPage){
                    str += 'w3-black currentPageData';
                        } else{
                    str += 'w3-hover-black';
                        }
                    str += '" href="#" onclick="goToPage(event, ' + i +')">' + ( i + 1 ) + '</a>';
                }
                if(data.currentPage != data.totalPages - 1){
                    str += '<a class="w3-button w3-hover-black" href="#" onclick="goToPage(event, ' + (data.currentPage + 1) + ')">»</a>';
                } else{
                    str += '<a class="w3-button" href="#" style="pointer-events: none; color: gray; text-decoration: none;">»</a>';
                }
                pagination.innerHTML = str;
                
            } else { // 해당 게시판의 데이터가 하나도 없으면
                // 내용 없음을 알릴 게시글 틀 생성
                var div = document.createElement('div');
                boardContent.appendChild(div);
                div.className = 'w3-row w3-container';

                // 해당 카테고리의 게시판의 내용이 하나도 없음을 알림
                var p = document.createElement('p');
                p.innerText = category + ' 글이 하나도 없습니다.';
                div.appendChild(p);
            }
            
            // 내가 작성한 글이면 x 버튼 눌렀을 때 글을 삭제할 수 있도록(새로 생긴 postDeleteBtn에 이벤트 다시 걸어주기)
            const postContentDeleteBtns = document.querySelectorAll('#postContentDeleteBtn');
            postContentDeleteBtns.forEach(deleteBtn => {
                deleteBtn.addEventListener('click', (event) => {
                const postContentId = event.target.getAttribute('data-pid');
                        const result = confirm('글을 삭제하면 복구할 수 없습니다. 정말 삭제하시겠습니까?');
                           if(result){
                                deleteNoticePost(postContentId); 
                           }
                });
            });
        })
        .catch(err => {
            console.log(err + "!!");
        });
}
// 문의남기기
function leaveAnInquiry(event){
    event.preventDefault();
    // 로그인 상태 체크
    isUserLoggedIn()
        .then(loggedIn =>{
        if(!loggedIn){ // 로그인 되어 있지 않다면
                alert('로그인 후 이용 부탁드립니다.');
                window.location.href = '/user/signin';
        } else{ // 로그인 되어 있다면
            const inquiryModal = document.querySelector('#inquiryModal');
            const inquiry = new bootstrap.Modal(inquiryModal);
            inquiry.show();
        }
        })
        .catch(err => {
            console.error('로그인 상태 확인 중 오류:', err);
        });
}
// 게시글 남기기
function writePost(event){
    event.preventDefault();
    // 로그인 상태 체크
    isUserLoggedIn()
        .then(loggedIn =>{
        if(!loggedIn){ // 로그인 되어 있지 않다면
                alert('로그인 후 이용 부탁드립니다.');
                /*ar prevPage = window.location.href;
                //TODO: 잘안됨;;
                window.location.href = '/user/signin?prevPage=' + encodeURIComponent(prevPage);*/
                window.location.href = '/user/signin';
        } else{ // 로그인 되어 있다면
            const postModal = document.querySelector('#postModal');
            const postCreate = new bootstrap.Modal(postModal);
            postCreate.show();
        }
        })
        .catch(err => {
            console.error('로그인 상태 확인 중 오류:', err);
        });
}

// 게시글 삭제
function deleteNoticePost(postId){
    const cpd = document.querySelector('.currentPageData'); // 현재 페이지 정보   
    const page = cpd.text-1;
    const category = boardTitle.innerText; // 현재 카테고리 정보 
    axios.delete('/board/post/' + postId)
        .then(response =>{
            alert('글을 삭제했습니다.');
            updateMain(category, page);
        })
        .catch(err => { console.log('게시글 삭제 에러 : ' + err); });
}

 // 페이지 이동(goToPage(i)), 이전/다음 페이지(현재페이지-1, 현재페이지+1)
function goToPage(event, page){
    event.preventDefault();
    const category = document.querySelector('#boardTitle').innerHTML;
    updateMain(category, page);
}

// 로그인 상태 체크
// Promise는 비동기 작업이 성공하거나 실패할 때의 상태와 결과를 나타내는 객체
// isUserLoggedIn() 함수가 반환하는 것은 Promise 객체이기 때문에
// then() 함수는 Promise 객체의 성공(resolve) 상태에 대한 콜백 함수를 등록하는 데 사용
// catch() 함수는 실패(reject) 상태에 대한 콜백 함수를 등록하는 데 사용
function isUserLoggedIn(){
    return axios.post('/user/loggedInChk')
        .then(response => {
            if(response.data === true){ // 로그인 정보가 있음
                return true;
            } else { // 로그인 정보가 없음
                return false;
            }
        })
        .catch(err => {
            console.log(err + ">> 로그인 상태 체크 에러!!");
            return false; // 오류 발생 시에도 일단 로그인되어 있지 않은 것으로 간주
        });
}

// summernote lite
$('.summernote').summernote({
        tabsize: 2,
        width: 445,
        height: 200,
        lang: "ko-KR",
        toolbar: [
          ['style', ['style']],
          ['fontname', ['fontname']],
          ['fontsize', ['fontsize']],
          ['color', ['color']],
          ['font', ['bold', 'italic', 'underline', 'strikethrough', 'clear']],
          ['para', ['ul', 'ol', 'paragraph']],
          ['height', ['height']],
          ['view', ['fullscreen']]
        ],
        fontNames: ['Roboto', 'Arial', 'Arial Black', 'Comic Sans MS', 'Courier New','맑은 고딕','궁서','굴림체','굴림','돋움체','바탕체']            
});