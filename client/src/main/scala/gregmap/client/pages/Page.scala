package gregmap.client.pages

import scalatags.Text.TypedTag

/**
  * Created by Greg Dorrell on 16/12/2015.
  */
trait Page {

  def render: TypedTag[String]

}
