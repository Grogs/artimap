package server.views

import scalatags.Text.all._
import scalatags.Text.tags2.{title => titleTag}



/**
  * Created by Greg Dorrell on 02/12/2015.
  */
object Index {


  def apply(googleApiKey: String) = {
    val containerId = "page-containers"
    html(
      head(
        titleTag("Map")
      ),
      body(
        div(id := containerId),
        Seq(script(src := s"https://maps.googleapis.com/maps/api/js"),
          script(src := "/assets/client-fastopt.js"),
          script(`type` := "text/javascript")(s"gregmap.frontend.Main().init('$containerId')"))
      )
    )
  }

}
