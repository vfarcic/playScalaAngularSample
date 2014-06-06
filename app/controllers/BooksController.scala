package controllers

import play.api.mvc.{BodyParsers, Action, Controller}
import play.api.libs.json.{JsPath, Writes}
import models.Book
import play.api.libs.json._
import play.api.libs.functional.syntax._

object BooksController extends Controller {

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
