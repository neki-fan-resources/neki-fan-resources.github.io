var selected_band_member = "-"
var selected_timeline_block = "-"

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

function toggle_timeline_block(uid) {
  document.querySelector("#" + uid).classList.toggle("timeline-item-block-hidden")

  if (selected_timeline_block == "-") {
    selected_timeline_block = uid
  } else if (selected_timeline_block == uid) {
    selected_timeline_block = "-"
  } else {
    document.querySelector("#" + selected_timeline_block).classList.toggle("timeline-item-block-hidden")
    selected_timeline_block = uid
  }
}