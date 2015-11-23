/**
  * Created by grogs on 23/11/2015.
  */
trait TimeoutDaoInter {

  def getPage(articleId: String): String

  def getEntries(articleId: String)(page: String = getPage(articleId)): List[Entry]
}
