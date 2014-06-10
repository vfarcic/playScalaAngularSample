package models

import play.api.libs.json.{Reads, JsPath, Writes}
import play.api.libs.functional.syntax._

case class BookReduced(id: Int, title: String, link: String)

case class Book(var id: Int, var image: String, var title: String, var author: String, var price: Double, var link: String)

object Book {

  var booksMap: scala.collection.mutable.Map[Int, Book] = collection.mutable.Map() ++ 10.to(99).map { n =>
    n -> Book(n, s"image$n", s"title$n", s"author$n", n.toDouble, s"/api/v1/books/$n")
  }.toMap

  def list: List[Book] = {
    booksMap.values.toList.sortWith(_.title < _.title)
  }

  def listReduced: List[BookReduced] = {
    list.map { book =>
      BookReduced(book.id, book.title, book.link)
    }
  }

  def save(book: Book) = {
    booksMap += (book.id -> book)
  }

  def delete(id: Int) {
    booksMap -= id
  }

  def get(id: Int): Book = {
    booksMap(id)
  }

}

trait BookSerializer {

  implicit val bookReducedWrites: Writes[BookReduced] = (
    (JsPath \ "id").write[Int] and
    (JsPath \ "title").write[String] and
    (JsPath \ "link").write[String]
  )(unlift(BookReduced.unapply))

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
