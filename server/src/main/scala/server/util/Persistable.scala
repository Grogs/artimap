package server.util

import java.io._
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.StandardCopyOption._

import boopickle.Default.{Pickle, Unpickle}
import boopickle.Pickler
import com.typesafe.scalalogging.LazyLogging

class Persistable[T:Pickler](name: String, default: => T) extends LazyLogging {

  val cacheFolder = new File("caches")
  if ( ! cacheFolder.exists())
    cacheFolder.mkdir()

  private val backingFile = new File(s"caches/$name.db")
  private val tmpFile = new File(s"caches/$name.db.flushing")

  logger.debug(s"${backingFile.getCanonicalPath} exists: ${backingFile.exists()}")

  val underlying: T = if (backingFile.exists) {
    val bytes = Files.readAllBytes(backingFile.toPath)
    Unpickle[T].fromBytes(ByteBuffer.wrap(bytes))
  } else default

  def flush() = {
    val bytes = Pickle.intoBytes(underlying)
    Files.write(tmpFile.toPath, bytes.array())
    Files.move(tmpFile.toPath, backingFile.toPath, REPLACE_EXISTING, ATOMIC_MOVE)
  }

}

object Persistable {
  def apply[T:Pickler](name: String, default: => T) = new Persistable[T](name, default)
}
