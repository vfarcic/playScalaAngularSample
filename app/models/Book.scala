package models

case class Book(id: Int, image: String, title: String, author: String, price: Double, link: String)

object Book {
  var list: List[Book] = {
    List(
      Book(1, "image1", "title1", "author1", 1.11, "link1"),
      Book(2, "image2", "title2", "author2", 2.22, "link2")
    )
  }

  def save(book: Book) = {
    list = list ::: List(book)
  }

}
