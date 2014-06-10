package controllers

import play.api.mvc.{AnyContent, BodyParsers, Action, Controller}
import models.{Book, BookSerializer}
import play.api.libs.json._

object BooksController extends Controller with BookSerializer {

  def index: Action[AnyContent] = Action {
    Ok(scala.io.Source.fromFile("public/html/index.html").mkString).as("text/html")
  }

  def listAll = Action {
    Ok(Json.toJson(Book.listReduced))
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
        Book.save(book)
        val title = book.title
        Ok(Json.obj("status" -> "OK", "message" -> s"Book $title has been saved."))
      }
    )
  }

}
