$(document).ready(function() {
  
  function trim(klass, size) {
    $.each($(klass), function() {
      if (this.innerHTML.length >= size) {
        this.innerHTML = this.innerHTML.substring(0, size) +  "...";
      }
    });
  }

  trim(".link-title", 20);
  trim(".description", 60);
});
