package server

import scala.collection.mutable
import scala.concurrent.ExecutionContext.{global=>defaultEC}
import scala.concurrent.Future

package object util {

  /**
    * getOrElseUpdate... but async
    * One limitation which would be straight forward to remove:
    *   - If you call this method with Key1, and then call it immediately again for the previous Future completes.. Then we will duplicate the work
    *   - We could store the in-flight futures in another hashmap, and got a [non-async] getOrElseUpdate on that.
    */
  implicit class MutableMapPimp[K,V](mmap: mutable.Map[K,V]) {
    def getOrElseUpdateAsync(key: K, fun: => Future[V]): Future[V] =
      if (mmap contains key)
        Future.successful(mmap(key))
      else {
        val res = fun
        res.map( mmap.put(key, _) )(defaultEC)
        res
      }
  }

}
