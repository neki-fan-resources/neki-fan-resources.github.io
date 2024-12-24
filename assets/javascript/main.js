function toggle_language_visibility(languageId) {
  var classname = ".lyrics_" + languageId
  console.log(classname)
  var es = document.querySelectorAll(classname)
  console.log(es.length)
  es.forEach(element => {
    element.classList.toggle("lyrics_hidden")
  });
}