package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json.{JsPath, Writes}
import models.Book
import play.api.libs.json._
import play.api.libs.functional.syntax._

object BooksController extends Controller {

  implicit val bookWrites: Writes[Book] = (
    (JsPath \ "id").write[Int] and
    (JsPath \ "image").write[String] and
    (JsPath \ "title").write[String] and
    (JsPath \ "author").write[String] and
    (JsPath \ "price").write[Double] and
    (JsPath \ "link").write[String]
  )(unlift(Book.unapply))

  def listAll = Action {
    Ok(Json.toJson(Book.list))
  }

}
