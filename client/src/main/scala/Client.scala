import dao.{GeocodingDaoInter, TimeoutDaoInter}
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import upickle.{default => upickle}
import scala.concurrent.ExecutionContext.Implicits.global

class Client(relativePath: String) extends autowire.Client[String, upickle.Reader, upickle.Writer]{
  def doCall(req: Request) = {
    Ajax.post(
      url = s"/api/$relativePath/" + req.path.mkString("/"),
      data = upickle.write(req.args)
    ).map(_.responseText)
  }
  def read[Result: upickle.Reader](p: String) = upickle.read[Result](p)
  def write[Result: upickle.Writer](r: Result) = upickle.write(r)
}

object Client {
  val timeoutDaoClient = new Client("entries")[TimeoutDaoInter]
  val geocodingDaoClient = new Client("geocode")[GeocodingDaoInter]
}
