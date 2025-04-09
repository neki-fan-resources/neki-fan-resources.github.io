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

var numberNewsItems = 1
var currentNewsItem = 0
var newsItemManuallySelected = false

function startLoopNewsItems(nb) {
  numberNewsItems = nb
  currentNewsItem = 0
  setTimeout(() => {
    loopNewsItems()
  }, 7500)
}

function loopNewsItems() {
  if (newsItemManuallySelected) {
    setTimeout(() => {
      loopNewsItems()
    }, 30000) // keep the selection for longer, if manually changed
    newsItemManuallySelected = false
  } else {
    var next = (currentNewsItem + 1) % numberNewsItems
    newsItemsSwitch(next)
    setTimeout(() => {
      loopNewsItems()
    }, 7500)
  }
}

function manualNewsItemSwitch(next) {
  newsItemManuallySelected = true
  newsItemsSwitch(next)
}

function newsItemsSwitch(next) {
  if (currentNewsItem != next) {
    var currentItem = document.querySelectorAll("#news_item_" + currentNewsItem)[0]
    var currentDot = document.querySelectorAll("#news_dot_" + currentNewsItem)[0]
    var nextItem = document.querySelectorAll("#news_item_" + next)[0]
    var nextDot = document.querySelectorAll("#news_dot_" + next)[0]

    currentItem.classList.toggle("news-item-hidden")
    nextDot.classList.toggle("news-dot-selected")
    nextItem.classList.toggle("news-item-hidden")
    currentDot.classList.toggle("news-dot-selected")

    currentNewsItem = next
  }
}