/**
 * 
 */
 
var selectedPostId;
// 드롭다운 열어줌.
function myitemsDropdown(event, id) {
      const dropdown = document.getElementById("itemsDropdown");

      // 클릭한 위치에서의 좌표
      const x = event.clientX;
      const y = event.clientY;
      // 클릭한 위치의 postId 등록
      selectedPostId = id;
      // 드롭다운을 클릭한 위치에 위치시키기
      dropdown.style.position = 'fixed';
      dropdown.style.top = y + 'px';
      dropdown.style.left = x + 'px';

      // 드롭다운이 열려있으면 닫고, 닫혀있으면 열기
      dropdown.classList.toggle("show");

      // 이벤트 전파(stopPropagation)를 막아서 document의 클릭 이벤트가 실행되지 않도록 함
      event.stopPropagation();
    }

// 글 보러가기 이동
function goDetail(event){
    // 드롭다운 내부에 클릭시 document 클릭이벤트 실행되지 않도록해줌.
    event.stopPropagation();
    const postId = selectedPostId;
    // detail 보기로 바로 이동시켜줌.
    location.href = '/sell/detail?id='+ postId;
};
// 판매글 수정/삭제하기(type u:수정, d:삭제)
function updatePost(event, type){
    // 드롭다운 내부에 클릭시 document 클릭이벤트 실행되지 않도록해줌.
    event.stopPropagation();
    const postId = selectedPostId;
    // 로그인 유저와 글 작성자가 동일한지 확인(2차 보호)
    axios.get('/myPage/writerChk', {
                params: {
                    postId: postId
                }
            })
            .then(chkResult =>{
                if(chkResult.data){
                    if(type === 'u'){
                        location.href = '/sell/modify?postId=' + postId;
                    } else if(type === 'd'){
                        const result = confirm('글을 삭제하게 되면 현재 판매와 관련된 채팅방은 삭제됩니다. 정말 삭제하시겠습니까?');
                        if(result){
                            axios.delete('/sell/delete?id=' + postId)
                                .then(response => {
                                    alert('판매글을 삭제했습니다.');
                                    // axios 작업 기다려 준 후 페이지 이탈
                                    setTimeout(function(){
                                        location.href = '/myPage/myItemsList';
                                    }, 300);
                                })
                                .catch(err => console.log(err + "내 판매글 삭제 오류!"));
                        }
                    }
                }else{
                    alert('글쓴이와 로그인 유저 다름 : 로그인 상태를 확인해주세요!!');
                }
            })
            .catch(err => console.log(err+' 글쓴이 체크 확인!!'));
};

// 문서 전체를 클릭했을 때 드롭다운을 닫음
document.addEventListener("click", function(event) {
  const dropdown = document.getElementById("itemsDropdown");
  if (dropdown.classList.contains("show")) {
    dropdown.classList.remove("show");
  }
});