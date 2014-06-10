package models

import org.specs2.mutable.Specification
import org.specs2.specification.BeforeExample

class BookSpec extends Specification with BeforeExample {

  val book = Book
  val book1 = Book(1, "image1", "title1", "author1", 1.11, "/api/v1/books/1")
  val book2 = Book(2, "image2", "title2", "author1", 2.22, "/api/v1/books/2")
  val unsavedBook = Book(123, "myImage", "myTitle", "me, myself and I", 123.45, "google.com")

  def before {
    book.booksMap = scala.collection.mutable.Map(
      2 -> book2,
      1 -> book1
    )
  }

  "Book#list" should {

    "return list of books" in {
      book.list must have size 2
      book.list must beAnInstanceOf[List[Book]]
      book.list.head must beAnInstanceOf[Book]
    }

    "return list of books in ascending order" in {
      book.list.head must beEqualTo(book1)
    }

  }

  "Book#listReduced" should {

    "return list of reduced books" in {
      book.listReduced must have size 2
      book.listReduced must beAnInstanceOf[List[BookReduced]]
      book.listReduced.head must beAnInstanceOf[BookReduced]
    }

  }

  "Book#save" should {

    "save the specified book" in {
      book.save(unsavedBook)
      book.list must contain(unsavedBook)
    }

  }

  "Book#delete" should {

    "remove the specified book" in {
      book.delete(book1.id)
      book.list must not contain book1
    }

  }

  "Book#get" should {

    "retrieve the specified book" in {
      book.get(book1.id) must beEqualTo(book1)
    }

  }

}
