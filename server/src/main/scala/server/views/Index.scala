package server.views

import scalatags.Text.all._
import scalatags.Text.tags2.{title=>titleTag}

/**
  * Created by Greg Dorrell on 02/12/2015.
  */
object Index {

  type City = String
  case class Article(name: String, url: String, tags: Seq[String], publicationDate: String)

  
  
  val articles: Map[City, Seq[Article]] = Map(
    "London" -> List(
      Article("The top ten restaurants in London", "/london/food-drink/top-ten-restaurants-in-london", Nil, "" ),
      Article("The 100 best bars and pubs in London", "/london/bars-pubs/the-100-best-bars-and-pubs-in-london-full-list", Nil, "" ),
      Article("London's best cafés and coffee shops", "/london/food-drink/londons-best-cafes-and-coffee-shops", Nil, "" ),
      Article("The best places to drink mulled wine in London", "/london/bars-and-pubs/the-best-places-to-drink-mulled-wine-in-london", Nil, "" ),
      Article("The 50 best cocktail bars in London", "/london/bars/best-bars-in-london-cocktail-bars", Nil, "" ),
      Article("London's best craft beer bars and pubs", "/london/bars-pubs/londons-best-craft-beer-bars-and-pubs", Nil, "" ),
      Article("Christmas markets and fairs in London", "/london/shopping/christmas-markets-and-fairs-in-london", Nil, "" ),
      Article("London's best burgers", "/london/food-and-drink/londons-best-burger-restaurants-1", Nil, ""),
      Article("Ice skating in London", "/london/things-to-do/ice-skating-in-london", Nil, "" )
    ),
    "Bristol" -> List(
      Article("The best pubs in Bristol", "/bristol/bars-pubs/the-best-pubs-in-bristol", Nil, "")
    )
)
  def apply(googleApiKey: String) =
    html(
      head(
        titleTag("Map")
      ),
      body(
        select(id := "selected-article",
          option(value:="?", selected:="selected", disabled)("Please select an article"),
          for ( (city, articlesForCity) <-  articles.toList )
            yield optgroup("label".attr:=city,
              for ( a <- articlesForCity)
                yield option(value:=a.url)(a.name)
            )
        ),
        a(href:="#/map/london/shopping/christmas-markets-and-fairs-in-london", "Go straight to chrismtas markets map"),
        div(id := "map-container", style := "height: 100%;"),
        script(src := s"https://maps.googleapis.com/maps/api/js"),
//        script(src := s"https://maps.googleapis.com/maps/api/js?key=$googleApiKey"),
        script(src := "/assets/client-fastopt.js"),
        script(`type`:="text/javascript")("gregmap.frontend.Main().main()")
      )
    )
}



//DISABLED
//            option(value:="/bristol/restaurants/the-best-coffee-shops-and-cafes-in-bristol")("The best coffee shops and cafés in Bristol"),
//            option(value:="/bristol/bars-and-pubs/the-best-craft-beer-bars-in-bristol")("The best craft beer bars in Bristol")
//          option(value:="/london/shopping/the-100-best-shops-in-london")("The 100 best shops in London"),
//          option(value:="/london/restaurants/londons-best-cycle-cafes")("London's best cycle cafés"),
//          option(value:="/london/bars-pubs/the-best-bars-for-drinking-gin-in-london")("The best bars for drinking gin in London"),
//          option(value:="/london/things-to-do/christmas-lights-in-london")("Christmas lights in London"),
