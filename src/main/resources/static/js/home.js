/**
 * 
 */
 
 
window.addEventListener('DOMContentLoaded', function () {
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
});
// 자동 슬라이드 이미지 - 3초 후 이미지 변경
var myIndex = 0;
SlideImages();
function SlideImages() {
  var i;
  var x = document.getElementsByClassName("mySlides");
  for (i = 0; i < x.length; i++) {
    x[i].style.display = "none";  
  }
  myIndex++;
  if (myIndex > x.length) {
      myIndex = 1;
      }    
  x[myIndex-1].style.display = "block";  
  setTimeout(SlideImages, 3000);    
}