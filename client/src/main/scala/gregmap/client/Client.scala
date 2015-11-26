package gregmap.client

import org.scalajs.dom.ext.Ajax
import upickle.{default => upickle}

import scala.concurrent.ExecutionContext.Implicits.global

class Client(path: String) extends autowire.Client[String, upickle.Reader, upickle.Writer]{
  def doCall(req: Request) = {
    Ajax.post(
      url = s"/$path/".replaceAll("//","/") + req.path.mkString("/"),
      data = upickle.write(req.args)
    ).map(_.responseText)
  }
  def read[Result: upickle.Reader](p: String) = upickle.read[Result](p)
  def write[Result: upickle.Writer](r: Result) = upickle.write(r)
}

object Client extends Client("api")
