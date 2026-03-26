#let title = "Final Project - Steam Search"

#let phone(screen) = {
  set text(fill: rgb("#eee"), font: ("Roboto", "Trebuchet MS"))
  box(
    outset: 2pt,
    width: 216pt,
    height: 432pt,
    radius: 6pt,
    stroke: 5pt,
    fill: gradient.radial(center: (50%, 20%), rgb("#193d57"), rgb("#1b2838")),
    inset: 0pt, {

    box(
      fill: rgb("#171d25"),
      height: 30pt,
      width: 100%,
      inset: 8pt, {

      v(1fr)
      title
      h(1fr)
      emoji.magnify
      h(.5em)
      [ ⋮ ]
      v(1fr)

    })
    v(0pt, weak: true)
    pad(8pt,
      screen
    )
  })
}

#let button(width: 100%, height: 22pt, inset: 0pt, text) = block(
  fill: gradient.linear(dir: direction.ttb, rgb("#445a7b"), rgb("#374964")),
  inset: inset,
  width: width,
  height: height, {
  text
})

#let game(name, description) = button(
  inset: 12pt,
  height: auto, {
  [== #name]
  v(.5em)
  par(description)
})

// ========================================================================= //

#set page(paper: "us-letter")

= #title
#v(1em)

For my final project, I want to make an app that searches Steam's store for
games. I think it would be interesting to work with their API. My idea is to
filter their games by tag.

The landing page (left) will have a list of tags provided by Steam's API
available for browsing. The list will be searchable via the bar at the top.
When the user clicks on a tag, the app makes a second request to retrieve a few
games from that tag. They are displayed in the second activity (right).

#phone[
  #columns(3,
    for tag in lorem(39).split() {
    button(
      align(center, {
        v(1fr)
        tag
        v(1fr)
      })
    )
    v(8pt, weak: true)
  })
]
#h(1em)
#phone[
  #game[Shooter Game][#lorem(20)]
  #game[Boring Simulator][#lorem(10)]
  #game[Battle of Something IDK][#lorem(3)]
  #game[Fake Names are Hard][#lorem(15)]
]

As a stretch goal, I may include various stats about a game when the user
clicks on it, such as active players, user reviews, or purchase options.
