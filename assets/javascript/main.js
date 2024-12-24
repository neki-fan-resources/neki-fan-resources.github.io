var selected_band_member = "-"

function toggle_language_visibility(languageId) {
  var classname = ".lyrics_" + languageId
  var es = document.querySelectorAll(classname)
  es.forEach(element => {
    element.classList.toggle("lyrics_hidden")
  });
}

function toggle_band_member_details(member) {
  var element = document.querySelectorAll("#member-details-" + member)[0]
  element.classList.toggle("band_member_details_hidden")

  if (selected_band_member == "-") {
    document.querySelectorAll("#member-more-" + member)[0].innerHTML = "⮙"
    selected_band_member = member
  } else if (selected_band_member == member) {
    document.querySelectorAll("#member-more-" + member)[0].innerHTML = "⮛"
    selected_band_member = "-"
  } else {
    document.querySelectorAll("#member-more-" + member)[0].innerHTML = "⮙"
    
    document.querySelectorAll("#member-more-" + selected_band_member)[0].innerHTML = "⮛"
    var es = document.querySelectorAll("#member-details-" + selected_band_member)
    es.forEach(element => {
      element.classList.toggle("band_member_details_hidden")
    })

    selected_band_member = member
  }


}