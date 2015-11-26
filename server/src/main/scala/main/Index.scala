package main
import scalatags.Text.tags2.{title=>titleTag}
import scalatags.Text.all._

object Index {
  def apply() =
    html(
      head(
        titleTag("Map")
      ),
      body(
        div(id := "map", style := "height: 100%;"),
        script(src := "/assets/client-fastopt.js"),
        script(`type`:="text/javascript")("gregmap.client.Main().main")
      )
    )
}
