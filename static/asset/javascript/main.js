var selectedOverlay = "-"


function toggleClass(classToSelect, classToToggle) {
  var classname = "." + classToSelect
  var es = document.querySelectorAll(classname)
  es.forEach(element => {
    element.classList.toggle(classToToggle)
  });
}

function toggleOverlay(overlayId) {
  var overlayDiv = document.querySelectorAll("#main-overlay")[0]
  var overlayContentDiv = document.querySelectorAll("#main-overlay-content")[0]

  if (selectedOverlay == "-") {
    // display and open
    overlayContentDiv.setHTMLUnsafe(overlayContent[overlayId])
    overlayDiv.classList.toggle("main-overlay-hidden")
    selectedOverlay = overlayId
  } else if (selectedOverlay == overlayId) {
    // close
    overlayDiv.classList.toggle("main-overlay-hidden")
    selectedOverlay = "-"
  } else {
    // replace previous content
    overlayContentDiv.setHTMLUnsafe(overlayContent[overlayId])
    selectedOverlay = overlayId
  }
}

// only to be called from elements inside the overlay
function closeOverlay() {
  var overlayDiv = document.querySelectorAll("#main-overlay")[0]
  overlayDiv.classList.toggle("main-overlay-hidden")
  selectedOverlay = "-"
}