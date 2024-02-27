/**
 * 
 */
 
var selectedPostId;
// 드롭다운 열어줌.
function wishDropdown(event, id) {
      const dropdown = document.getElementById("wishDropdown");

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
    // detail 보기로 바로 이동시켜줌.
    location.href = '/sell/detail?id='+ selectedPostId;
};
// 관심 목록에서 삭제하기
function deleteHeart(event){
    // 드롭다운 내부에 클릭시 document 클릭이벤트 실행되지 않도록해줌.
    event.stopPropagation();
    // 관심목록 삭제 후 페이지를 새로고침
    axios.get('/myPage/postLike', {
           params: {
               postId: selectedPostId
           }
        })
        .then(likeStatus => {
            alert('관심 목록에서 삭제했습니다.');
            location.href = '/myPage/myWishList';
        })
        .catch(err => console.log(err + ': 관심 목록 삭제 오류'));
};
// 문서 전체를 클릭했을 때 드롭다운을 닫음
document.addEventListener("click", function(event) {
  const dropdown = document.getElementById("wishDropdown");
  if (dropdown.classList.contains("show")) {
    dropdown.classList.remove("show");
  }
});