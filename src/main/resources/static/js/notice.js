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
                    const inquiryForm = document.querySelector('#postCreateForm');
                    inquiryForm.action = '/board/post';
                    inquiryForm.method = 'post';
                    inquiryForm.submit();
                    
                    alert('남긴 문의는 문의 내역에서 확인가능합니다.');
        }
    });
});

// 전체공지, 자유게시판, FAQ
function community(category){
    const boardTitle = document.querySelector('#boardTitle');
    axios.get('/board/notice/community/' + category)
        .then(response => {
            if(category === "전체공지"){
                boardTitle.innerHTML = "전체공지";
            } else if(category === "자유게시판"){
                boardTitle.innerHTML = "자유게시판";
            } else if(category === "FAQ"){
                boardTitle.innerHTML = "FAQ";
            } 
        })
        .catch(err => {
            console.log(err + "!!");
        });
    /*
    const boardContent =    '<h1 class="w3-text-teal" id="boardTitle">전체공지</h1>'
                       +    '<div class="w3-row w3-padding-64">'
                       +      '<div class="w3-twothird w3-container">'
                       +        '<h3 class="w3-text-teal">Heading</h3>'
                       +        '<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Lorem ipsum'
                       +          'dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>'
                       +      '</div>'
                       +      '<div class="w3-third w3-container">'
                       +        '<p class="w3-border w3-padding-large w3-padding-32 w3-center">AD</p>'
                       +        '<p class="w3-border w3-padding-large w3-padding-64 w3-center">AD</p>'
                       +      '</div>'
                       +    '</div>'

                       +    '<!-- Pagination -->'
                       +    '<div class="w3-center w3-padding-32">'
                       +      '<div class="w3-bar">'
                       +        '<a class="w3-button w3-black" href="#">1</a>'
                       +        '<a class="w3-button w3-black" href="#">2</a>'
                       +        '<a class="w3-button w3-black" href="#">3</a>'
                       +        '<a class="w3-button w3-black" href="#">4</a>'
                       +        '<a class="w3-button w3-black" href="#">5</a>'
                       +        '<a class="w3-button w3-black" href="#">»</a>'
                       +      '</div>'
                       +    '</div>';*/
}

// 문의남기기
function leaveAnInquiry(){
    const inquiryModal = document.querySelector('#inquiryModal');
    const inquiry = new bootstrap.Modal(inquiryModal);
    inquiry.show();
}
// 게시글 남기기
function writePost(){
    const postModal = document.querySelector('#postModal');
    const postCreate = new bootstrap.Modal(postModal);
    postCreate.show();
}