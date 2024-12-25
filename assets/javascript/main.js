var selected_band_member = "-"

function toggle_language_visibility(languageId) {
  var classname = ".lyrics_" + languageId
  var es = document.querySelectorAll(classname)
  es.forEach(element => {
    element.classList.toggle("lyrics_hidden")
  });
}

function toggle_band_member_details(member) {
  document.querySelectorAll("#member-details-" + member)[0]
    .classList.toggle("band_member_details_hidden")
  document.querySelectorAll("#member-more-" + member)[0]
    .classList.toggle("band_member_more_arrow_rotate")


  if (selected_band_member == "-") {
    selected_band_member = member
  } else if (selected_band_member == member) {
    selected_band_member = "-"
  } else {
    document.querySelectorAll("#member-more-" + selected_band_member)[0]
      .classList.toggle("band_member_more_arrow_rotate")
    document.querySelectorAll("#member-details-" + selected_band_member)[0]
      .classList.toggle("band_member_details_hidden")

    selected_band_member = member
  }


}