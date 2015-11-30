package main
import scalatags.Text.tags2.{title=>titleTag}
import scalatags.Text.all._

object Index {
  def apply(googleApiKey: String) =
    html(
      head(
        titleTag("Map")
      ),
      body(
        select(id := "selected-article",
          option(value:="?", selected:="selected", disabled)("Please select an article"),
          option(value:="/london/food-drink/top-ten-restaurants-in-london")("The top ten restaurants in London"),
          option(value:="/london/bars-pubs/the-100-best-bars-and-pubs-in-london-full-list")("The 100 best bars and pubs in London"),
          option(value:="/london/shopping/christmas-markets-and-fairs-in-london")("Christmas markets and fairs in London"),
//          option(value:="/london/things-to-do/christmas-lights-in-london")("Christmas lights in London"),
          option(value:="/london/things-to-do/ice-skating-in-london")("Ice skating in London")
        ),
        div(id := "map-container", style := "height: 100%;"),
        script(src := s"https://maps.googleapis.com/maps/api/js"),
//        script(src := s"https://maps.googleapis.com/maps/api/js?key=$googleApiKey"),
        script(src := "/assets/client-fastopt.js"),
        script(`type`:="text/javascript")("gregmap.frontend.Main().main()")
      )
    )
}
