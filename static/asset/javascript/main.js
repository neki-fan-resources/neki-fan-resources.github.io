
function toggleClass(classToSelect, classToToggle) {
  var classname = "." + classToSelect
  var es = document.querySelectorAll(classname)
  es.forEach(element => {
    element.classList.toggle(classToToggle)
  });
}