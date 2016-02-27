package shared.model

import scalatags.Text.all._

case class Entry(name: String, relPath: String, location: String, description: String) {
  def link = s"http://www.timeout.com$relPath"

  def address = s"$name, $location, London, England"

  def markerHtml(pos: LatLong) =
    div(id:="content",
      h1(id:="firstHeading", cls:="firstHeading")(name),
      div(id:="bodyContent",
        p(raw(description)),
        p(location),
        p(a(href:=link, target:="_blank", "Go to timeout.com for details")),
        p(a(href:=pos.gmapDirectionsLink,target:="_blank", "Get Directions with Google Maps"))
      )
    )
}
