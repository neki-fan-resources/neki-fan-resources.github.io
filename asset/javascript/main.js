var selected_band_member = "-"
var selected_timeline_card = "-"

function toggle_language_visibility(languageId) {
  var classname = ".lyrics-sub-" + languageId
  var es = document.querySelectorAll(classname)
  es.forEach(element => {
    element.classList.toggle("lyrics-sub-hidden")
  });
}

function toggle_band_member_info(member) {
  document.querySelectorAll("#band-" + member + "-info")[0]
    .classList.toggle("band-info-hidden")
  document.querySelectorAll("#band-" + member + "-summary")[0]
    .classList.toggle("band-" + member + "-summary-background")

  if (selected_band_member == "-") {
    selected_band_member = member
  } else if (selected_band_member == member) {
    selected_band_member = "-"
  } else {
    document.querySelectorAll("#band-" + selected_band_member + "-info")[0]
    .classList.toggle("band-info-hidden")
    document.querySelectorAll("#band-" + selected_band_member + "-summary")[0]
      .classList.toggle("band-" + selected_band_member + "-summary-background")

    selected_band_member = member
  }
}

function toggle_timeline_card(uid) {
  document.querySelector("#" + uid).classList.toggle("timeline-card-hidden")

  if (selected_timeline_card == "-") {
    selected_timeline_card = uid
  } else if (selected_timeline_card == uid) {
    selected_timeline_card = "-"
  } else {
    document.querySelector("#" + selected_timeline_card).classList.toggle("timeline-card-hidden")
    selected_timeline_card = uid
  }
}