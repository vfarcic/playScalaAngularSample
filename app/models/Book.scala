package models

import play.api.libs.json.{Reads, JsPath, Writes}
import play.api.libs.functional.syntax._

case class Book(id: Int, image: String, title: String, author: String, price: Double, link: String)

object Book {

  var list: List[Book] = {
    1.until(99).map { n =>
      Book(n, s"image$n", s"title$n", s"author$n", n, s"link$n")
    }.toList
  }

  def save(book: Book) = {
    list = list ::: List(book)
  }

}

trait BookSerializer {

  implicit val bookWrites: Writes[Book] = (
    (JsPath \ "id").write[Int] and
      (JsPath \ "image").write[String] and
      (JsPath \ "title").write[String] and
      (JsPath \ "author").write[String] and
      (JsPath \ "price").write[Double] and
      (JsPath \ "link").write[String]
    )(unlift(Book.unapply))

  implicit val bookReads: Reads[Book] = (
    (JsPath \ "id").read[Int] and
      (JsPath \ "image").read[String] and
      (JsPath \ "title").read[String] and
      (JsPath \ "author").read[String] and
      (JsPath \ "price").read[Double] and
      (JsPath \ "link").read[String]
    )(Book.apply _)

}
