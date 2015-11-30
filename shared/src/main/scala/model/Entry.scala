package model

import scalatags.Text.all._

case class Entry(name: String, link: String, location: String, description: String) {
  def address = s"$name, $location, London, England"

  def markerHtml =
    div(id:="content",
      h1(id:="firstHeading", cls:="firstHeading")(name),
      div(id:="bodyContent",
        p(description),
        p(location),
        p(a(href:=link,"Click here for details"))
      )
    )
}
