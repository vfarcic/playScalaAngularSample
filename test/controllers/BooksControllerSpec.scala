package controllers

import play.api.test.Helpers._
import org.specs2.mutable.Specification
import play.api.test.{FakeHeaders, FakeRequest, FakeApplication}
import play.api.http.MimeTypes
import play.api.libs.json.Json
import models.{BookSerializer, Book}
import org.specs2.specification.BeforeExample
import org.specs2.matcher.JsonMatchers

class BooksControllerSpec extends Specification with BeforeExample with JsonMatchers with MimeTypes with BookSerializer {

  val fakeJsonHeaders = FakeHeaders(Seq("Content-type" -> Seq("application/json")))
  val book1 = Book(1, "image1", "title1", "author1", 1.11, "/api/v1/books/1")
  val book1Id = book1.id
  val unsavedBook = Book(123, "image123", "title123", "author123", 123.45, "/api/v1/books/123")
  val unsavedBookId = unsavedBook.id
  val url = "/api/v1/books"

  def before {
    running(FakeApplication()) {
      route(FakeRequest(PUT, url, fakeJsonHeaders, Json.toJson(book1)))
    }
  }

  "GET /" should {

    "respond with HTML" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(GET, "/"))
        contentType(result).get must equalTo(HTML)
        status(result) must equalTo(OK)
      }
    }

  }

  "GET /api/v1/books" should {

    "respond with JSON" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(GET, url))
        contentType(result).get must equalTo(JSON)
        status(result) must equalTo(OK)
      }
    }

    "return all books reduced" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(GET, url))
        val json = contentAsJson(result)
        json must equalTo(Json.toJson(Book.listReduced))
      }
    }

  }

  "GET /api/v1/books/ID" should {

    val url = s"/api/v1/books/$book1Id"

    "respond with JSON" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(GET, url))
        contentType(result).get must equalTo(JSON)
        status(result) must equalTo(OK)
      }
    }

    "return the specified book" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(GET, url))
        val json = contentAsJson(result)
        json must equalTo(Json.toJson(book1))
      }
    }

  }

  "DELETE /api/v1/books/ID" should {

    val url = s"/api/v1/books/$book1Id"

    "respond with JSON" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(DELETE, url))
        contentType(result).get must equalTo(JSON)
        status(result) must equalTo(OK)
      }
    }

    "remove the specified book" in {
      running(FakeApplication()) {
        route(FakeRequest(DELETE, url))
        status(result) must equalTo(OK)
        Book.list must not contain book1
      }
    }

  }

  "PUT /api/v1/books" should {

    "respond with JSON" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(PUT, url, fakeJsonHeaders, Json.toJson(unsavedBook)))
        contentType(result).get must equalTo(JSON)
        status(result) must equalTo(OK)
        contentAsString(result) must /("status" -> "OK")
      }
    }

    "add the specified book" in {
      running(FakeApplication()) {
        route(FakeRequest(PUT, url, fakeJsonHeaders, Json.toJson(unsavedBook)))
        Book.list must contain(unsavedBook)
      }
    }

    "update the specified book" in {
      running(FakeApplication()) {
        book1.title = "This is updated title"
        val count = Book.list.size
        val Some(result) = route(FakeRequest(PUT, url, fakeJsonHeaders, Json.toJson(book1)))
        status(result) must equalTo(OK)
        Book.list must contain(book1)
        Book.list must have size count
      }
    }

    "respond with BAD_REQUEST when JSON is incorrect" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(PUT, url, fakeJsonHeaders, Json.parse("{}")))
        contentType(result).get must equalTo(JSON)
        status(result) must equalTo(BAD_REQUEST)
      }
    }

    "respond with status KO when JSON is incorrect" in {
      running(FakeApplication()) {
        val Some(result) = route(FakeRequest(PUT, url, fakeJsonHeaders, Json.parse("{}")))
        contentAsString(result) must /("status" -> "KO")
      }
    }

  }

}
