package dao

import model.Entry

trait TimeoutDaoInter {

  def getPage(articleId: String): String

  def getEntries(articleId: String): List[Entry]
}
