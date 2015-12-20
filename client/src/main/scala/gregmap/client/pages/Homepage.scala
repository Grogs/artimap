package gregmap.client.pages

import gregmap.client.{Client, Map}
import org.scalajs.dom.html.Select
import org.scalajs.dom.raw.{HTMLSelectElement, Event}
import org.scalajs.dom.{document, console, location}
import rx._
import example.Framework._

import scala.scalajs.js,js.{debugger,Dynamic}

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import scalatags.JsDom.tags2.{style=>styleElem}

/**
  * Created by Greg Dorrell on 16/12/2015.
  */
class Homepage extends Page {
  type City = String
  case class Article(name: String, url: String, tags: List[Tag], publicationDate: String)

  sealed trait Tag
  case object Food extends Tag
  case object Shopping extends Tag
  case object `Pubs/Bars` extends Tag
  case object `Coffee/Cafes` extends Tag
  case object `Things To Do` extends Tag
  val tags = Map(
    "food" -> Food,
    "shops" -> Shopping,
    "drinks" -> `Pubs/Bars`,
    "cafes" -> `Coffee/Cafes`,
    "things" -> `Things To Do`
  )

//  val rawtags = typeTag[Tag].tpe.typeSymbol.asClass.knownDirectSubclasses
//  val tags = typeTag[Tag].tpe.typeSymbol.asClass.knownDirectSubclasses.map( tag =>
//    tag.name.decodedName.toString
//  )

  val articles = Map[City, List[Article]](
    "London" -> List(
      Article("The top ten restaurants in London", "/london/food-drink/top-ten-restaurants-in-london", Food::Nil, "" ),
      Article("The 100 best bars and pubs in London", "/london/bars-pubs/the-100-best-bars-and-pubs-in-london-full-list", `Pubs/Bars`::Nil, "" ),
      Article("London's best cafés and coffee shops", "/london/food-drink/londons-best-cafes-and-coffee-shops", `Coffee/Cafes`::Nil, "" ),
      Article("The best places to drink mulled wine in London", "/london/bars-and-pubs/the-best-places-to-drink-mulled-wine-in-london", `Pubs/Bars`::`Things To Do`::Nil, "" ),
      Article("The 50 best cocktail bars in London", "/london/bars/best-bars-in-london-cocktail-bars", `Pubs/Bars`::Nil, "" ),
      Article("London's best craft beer bars and pubs", "/london/bars-pubs/londons-best-craft-beer-bars-and-pubs", `Pubs/Bars`::Nil, "" ),
      Article("Christmas markets and fairs in London", "/london/shopping/christmas-markets-and-fairs-in-london", `Things To Do`::Shopping::Nil, "" ),
      Article("London's best burgers", "/london/food-and-drink/londons-best-burger-restaurants-1", Food::Nil, ""),
      Article("Ice skating in London", "/london/things-to-do/ice-skating-in-london", `Things To Do`::Nil, "" )
    ),
    "Bristol" -> List(
      Article("The best pubs in Bristol", "/bristol/bars-pubs/the-best-pubs-in-bristol", `Pubs/Bars`::Nil, ""),
      Article("The best coffee shops and cafés in Bristol","/bristol/restaurants/the-best-coffee-shops-and-cafes-in-bristol", `Coffee/Cafes`::Nil, "")

    )
    //DISABLED
    /*
              option(value:="/bristol/bars-and-pubs/the-best-craft-beer-bars-in-bristol")("The best craft beer bars in Bristol")
              option(value:="/london/shopping/the-100-best-shops-in-london")("The 100 best shops in London"),
              option(value:="/london/restaurants/londons-best-cycle-cafes")("London's best cycle cafés"),
              option(value:="/london/bars-pubs/the-best-bars-for-drinking-gin-in-london")("The best bars for drinking gin in London"),
              option(value:="/london/things-to-do/christmas-lights-in-london")("Christmas lights in London"),
    */
  )

  val selectedCity = Var[Option[String]](None)
  val selectedTag = Var[Option[Tag]](None)
  val filteredArticles = Rx[List[Article]] {(
    for {
      (city, articleList) <- articles
      article <- articleList
      if selectedCity().isEmpty || city == selectedCity().get
      if selectedTag().isEmpty || article.tags.contains(selectedTag().get)
    } yield article
  ).toList}

  val mapContainer = div(id := "map-container", style := "height: 100%;").render
  var map: Map = null

  private val styleSheet = styleElem(`type`:="text/css",
    """
      |   #article-selection > div {
      |     margin-bottom: 8px;
      |   }
      |
      |   #article-selection {
      |     text-align: center;
      |   }
      |
      |		select {
      |		    border: 2px solid grey;
      |		    border-radius: 5px;
      |		    padding: 2px;
      |		    -webkit-appearance: none;
      |		    background:url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='50px' height='50px'><polyline points='46.139,15.518 25.166,46.49 4.193,15.519' fill='white' /></svg>") right no-repeat;
      |		    background-position: right 15px top 16px;
      |		    background-size: 18px 18px;
      |		}
      |		select:active {
      |		    border: 1px solid #fff;
      |		}
		""".stripMargin)

  def render = {
    def default(text: String, mods: Modifier*) = option(value := "?", selected := "selected", mods)(text)

    def articleSelect(articles: List[Article]) = {
      val res =
        select(id := "selected-article",
          default("Please select an article", disabled),
          Rx{ optgroup(for (a <- filteredArticles()) yield option(value := a.url)(a.name)) }
        ).render
      res.onchange = loadFrom _
      res
    }
    val citySelect = {
      val res = select(default("everywhere"),for (city <- articles.keys.toList) yield option(value := city)(city)).render
      res.onchange = (e: Event) => selectedCity() = if(articles.keys.toList contains getSelectValue(e)) Option(getSelectValue(e)) else None
      res
    }
    val tagSelect = {
      val res = select(default("everything"), for ((key, tag) <- tags.toList) yield option(value := key)(tag.toString)).render
      res.onchange = (e: Event) => selectedTag() = tags.get(getSelectValue(e))
      res
    }
    div(
      styleSheet,
      div( id:="article-selection",
        div("I am in ", citySelect, " and I would like to see articles about ", tagSelect),
        div( id:="articleSelect",
          articleSelect(articles("Bristol"))
        )
      ),
      mapContainer
    )
  }

  def loadMap(articleId: String) = {
    console.log(s"make map - start - $articleId")
    map = new Map(articleId, mapContainer, Client)
    Dynamic.global.window.map = map.asInstanceOf[js.Any]
    console.log(s"make map - finish - $articleId")
  }

  def clearMap = {
    map = null
    mapContainer.innerHTML = ""
  }

//  def loadFrom(e: Event): Unit = loadMap(e.target.asInstanceOf[HTMLSelectElement].value)
  def loadFrom(e: Event): Unit = location.hash = s"#/map${getSelectValue(e)}"

//  val selectedArticle = Option(document.getElementById("selected-article")).getOrElse(throw new IllegalStateException("Cannot find article selector")).asInstanceOf[HTMLSelectElement]
//  selectedArticle.onchange = loadFrom _


  def getSelectValue(e: Event): String = e.target.asInstanceOf[Select].value
}
