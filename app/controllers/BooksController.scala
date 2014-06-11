package controllers

import play.api.mvc.{AnyContent, BodyParsers, Action, Controller}
import models.{BookReduced, Book, BookSerializer}
import play.api.libs.json._
import akka.actor.{Inbox, ActorSystem, Props}
import actors.{BooksReduced, ListBooksReduced, SaveBook, BookActor, Status}
import scala.concurrent.duration._

object BooksController extends Controller with BookSerializer {

  val akkaSystem = ActorSystem("booksAkka")
  val bookActor = akkaSystem.actorOf(Props[BookActor], "bookActor")

  def index: Action[AnyContent] = Action {
    Ok(scala.io.Source.fromFile("public/html/index.html").mkString).as("text/html")
  }

  def get(id: Int) = Action {
    Ok(Json.toJson(Book.get(id)))
  }

  def delete(id: Int) = Action {
    Book.delete(id)
    Ok(Json.obj("status" -> "OK", "message" -> s"Book $id has been removed"))
  }

  def save = Action(BodyParsers.parse.json) { request =>
    val bookResult = request.body.validate[Book]
    bookResult.fold(
      errors => {
        BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toFlatJson(errors)))
      },
      book => {
//        Book.save(book)
//        val title = book.title
//        val status = "OK"
//        val message = s"Book $title has been saved."

//        bookActor ! SaveBook(book)

        val inbox = Inbox.create(akkaSystem)
        inbox.send(bookActor, SaveBook(book))
        val Status(status, message) = inbox.receive(10.seconds)

        Ok(Json.obj("status" -> status, "message" -> message))
      }
    )
  }

  def listAll = Action {
    //    Ok(Json.toJson(Book.listReduced))
    val inbox = Inbox.create(akkaSystem)
    inbox.send(bookActor, ListBooksReduced)
    val BooksReduced(books) = inbox.receive(10.seconds)
    Ok(Json.toJson(books))
  }

}
