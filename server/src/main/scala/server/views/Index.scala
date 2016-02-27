package server.views

import server.Environment._

import scalatags.Text.all._
import scalatags.Text.tags2.{title => titleTag}



/**
  * Created by Greg Dorrell on 02/12/2015.
  */
object Index {


  def apply(googleApiKey: String, env: Environment) = {
    val containerId = "page-containers"
    html(
      head(
        titleTag("artimap"),
        link(rel :="stylesheet", href := "https://cdnjs.cloudflare.com/ajax/libs/intro.js/1.1.1/introjs.min.css")
      ),
      body(
        div(id := containerId),
        Seq(
          script(src := s"https://maps.googleapis.com/maps/api/js"),
          script(src := "assets/"+(env match {
            case DEV =>  "client-fastopt.js"
            case PROD => "client-opt.js"
          })),
          script(src := "https://cdnjs.cloudflare.com/ajax/libs/intro.js/1.1.1/"+(env match {
            case DEV =>  "intro.js"
            case PROD => "intro.min.js"
          })),
          script(`type` := "text/javascript")(s"gregmap.client.Main().init('$containerId')"))
      )
    )
  }

}
