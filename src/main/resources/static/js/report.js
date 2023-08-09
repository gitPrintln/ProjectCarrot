/**
 * 
 */
 
 
window.addEventListener('DOMContentLoaded', function () {
    
    
    
    
    
    
});
function toggleDetails(id) {
    var details = document.getElementById('details' + id);
    
    if (details.style.display === 'block') {
        details.style.display = 'none';
      } else {
        details.style.display = 'block';
      }
    }