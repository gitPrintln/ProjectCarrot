/**
 * 
 */
 
 
window.addEventListener('DOMContentLoaded', function () {
    // 문의 남기기 모달 event
    const leaveBtn = document.querySelector('#leaveBtn');
    leaveBtn.addEventListener('click', function(){
        const inquiryTitle = document.querySelector('#inquiryTitle').value;
        const inquiryType = document.querySelector('#inquiryType').value;
        const inquiryContent = document.querySelector('#inquiryContent').value;
        
        if (inquiryTitle == '' || inquiryType == '' || inquiryContent == '') {
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
    
    // 게시글 남기기 모달 event
    const postCreateBtn = document.querySelector('#postCreateBtn');
    postCreateBtn.addEventListener('click', function(){
        const postTitle = document.querySelector('#postTitle').value;
        const postCategory = document.querySelector('#postCategory').value;
        const postContent = document.querySelector('#postContent').value;
        
        if (postTitle == '' || postCategory == '' || postContent == '') {
            alert('빠진 부분을 채워넣어주세요!');
            return;
        }
        const result = confirm('정말 글을 작성하시겠습니까?');
        
        if(result){
                    const postCreateForm = document.querySelector('#postCreateForm');
                    postCreateForm.action = '/board/post';
                    postCreateForm.method = 'post';
                    postCreateForm.submit();
                    
                    alert(postCategory + '글 작성 완료');
        }
    });
});

// 전체공지, 자유게시판, FAQ
function community(event, category){
    // 링크의 기본 동작 막기
    event.preventDefault();
    updateMain(category);
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
                // 게시판 내용 바꿔주기
                for(var i = 0; i < data.entity.content.length; i++){
                        // 새로운 게시글 틀 생성
                        var div = document.createElement('div');
                        boardContent.appendChild(div);
                        // 게시글 틀 설정
                        div.className = 'w3-twothird w3-container';
                        
                        // h3 요소(제목)를 생성하고 제목 데이터를 추가
                        var h3 = document.createElement('h3');
                        h3.className = 'w3-text-teal';
                        h3.innerText = data.entity.content[i].title;
                        
                        // p 요소(내용)를 생성하고 내용 데이터를 추가
                        var p = document.createElement('p');
                        p.innerText = data.entity.content[i].content;
                        
                        // 생성한 h3와 p를 div에 추가
                        div.appendChild(h3);
                        div.appendChild(p);
                }
                
                // 페이징 새 게시판에 맞게 적용시키기
                let str = '';
                if(data.currentPage != 0){
                    str += '<a class="w3-button w3-hover-black" href="#" onclick="prevPage(event)">«</a>';
                }
                for(var i = data.startPage; i <= data.endPage; i++){
                    str += '<a class="w3-button ';
                        if(i == data.currentPage){
                    str += 'w3-black';
                        } else{
                    str += 'w3-hover-black';
                        }
                    str += '" href="#" onclick="goToPage(event, ' + i +')">' + ( i + 1 ) + '</a>';
                }
                if(data.currentPage != data.totalPages - 1){
                    str += '<a class="w3-button w3-hover-black" href="#" onclick="nextPage(event)">»</a>';
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
        })
        .catch(err => {
            console.log(err + "!!");
        });
}
// 문의남기기
function leaveAnInquiry(event){
    event.preventDefault();
    const inquiryModal = document.querySelector('#inquiryModal');
    const inquiry = new bootstrap.Modal(inquiryModal);
    inquiry.show();
}
// 게시글 남기기
function writePost(event){
    event.preventDefault();
    const postModal = document.querySelector('#postModal');
    const postCreate = new bootstrap.Modal(postModal);
    postCreate.show();
}

 // 페이지 이동(goToPage(i))
function goToPage(event, page){
    event.preventDefault();
    const category = document.querySelector('#boardTitle').innerHTML;
    updateMain(category, page);
    
}
 // 이전 페이지 이동
function prevPage(event){
    event.preventDefault();
}
 // 다음 페이지 이동
function nextPage(event){
    event.preventDefault();
}