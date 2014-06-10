package controllers

import play.api.mvc.{AnyContent, BodyParsers, Action, Controller}
import models.{BookSerializer, Book}
import play.api.libs.json._

object BooksController extends Controller with BookSerializer {

  def index: Action[AnyContent] = Action {
    Ok(scala.io.Source.fromFile("public/html/index.html").mkString).as("text/html")
  }

  def listAll = Action {
    Ok(Json.toJson(Book.list))
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
