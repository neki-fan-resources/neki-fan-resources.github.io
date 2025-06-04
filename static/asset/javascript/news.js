var numberNewsItems = 1
var currentNewsItem = 0
var newsItemManuallySelected = false

function startLoopNewsItems() {
  numberNewsItems = document.querySelectorAll(".news-item").length
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

startLoopNewsItems()