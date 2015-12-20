package gregmap.client.pages

import scalatags.JsDom.TypedTag
import org.scalajs.dom.html.Div

/**
  * Created by Greg Dorrell on 16/12/2015.
  */
trait Page {

  def render: TypedTag[Div]

}
