package server.views

import scalatags.Text.all._
import scalatags.Text.tags2.{title=>titleTag}

/**
  * Created by Greg Dorrell on 02/12/2015.
  */
object Index {
  def apply(googleApiKey: String) =
    html(
      head(
        titleTag("Map")
      ),
      body(
        select(id := "selected-article",
          option(value:="?", selected:="selected", disabled)("Please select an article"),
          optgroup("label".attr:="London",
            option(value:="/london/food-drink/top-ten-restaurants-in-london")("The top ten restaurants in London"),
            option(value:="/london/bars-pubs/the-100-best-bars-and-pubs-in-london-full-list")("The 100 best bars and pubs in London"),
            //          option(value:="/london/shopping/the-100-best-shops-in-london")("The 100 best shops in London"),
            option(value:="/london/food-drink/londons-best-cafes-and-coffee-shops")("London's best cafés and coffee shops"),
            //          option(value:="/london/restaurants/londons-best-cycle-cafes")("London's best cycle cafés"),
            option(value:="/london/bars-and-pubs/the-best-places-to-drink-mulled-wine-in-london")("The best places to drink mulled wine in London"),
            //          option(value:="/london/bars-pubs/the-best-bars-for-drinking-gin-in-london")("The best bars for drinking gin in London"),
            option(value:="/london/bars/best-bars-in-london-cocktail-bars")("The 50 best cocktail bars in London"),
            option(value:="/london/bars-pubs/londons-best-craft-beer-bars-and-pubs")("London's best craft beer bars and pubs"),
            option(value:="/london/shopping/christmas-markets-and-fairs-in-london")("Christmas markets and fairs in London"),
            //          option(value:="/london/things-to-do/christmas-lights-in-london")("Christmas lights in London"),
            option(value:="/london/food-and-drink/londons-best-burger-restaurants-1")("London's best burgers"),
            option(value:="/london/things-to-do/ice-skating-in-london")("Ice skating in London")
          ),
          optgroup("label".attr:="Bristol",
//            option(value:="/bristol/restaurants/the-best-coffee-shops-and-cafes-in-bristol")("The best coffee shops and cafés in Bristol"),
            option(value:="/bristol/bars-pubs/the-best-pubs-in-bristol")("The best pubs in Bristol")
//            option(value:="/bristol/bars-and-pubs/the-best-craft-beer-bars-in-bristol")("The best craft beer bars in Bristol")
          )
        ),
        a(href:="#/map//london/shopping/christmas-markets-and-fairs-in-london", "Go straight to chrismtas markets map"),
        div(id := "map-container", style := "height: 100%;"),
        script(src := s"https://maps.googleapis.com/maps/api/js"),
//        script(src := s"https://maps.googleapis.com/maps/api/js?key=$googleApiKey"),
        script(src := "/assets/client-fastopt.js"),
        script(`type`:="text/javascript")("gregmap.frontend.Main().main()")
      )
    )
}
