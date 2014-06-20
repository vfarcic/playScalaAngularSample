package controllers

import actors.{BookActor, BooksReduced, ListBooksReduced}
import akka.actor.{Props, ActorSystem, Inbox}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

class GenericController extends Controller {

  val akkaSystem = ActorSystem("genericAkka")

  def get(id: Int) = Action {
    val inbox = Inbox.create(akkaSystem)

    val genericActor = akkaSystem.actorOf(Props[BookActor], "bookActor")
    inbox.send(bookActor, ListBooksReduced)
    val BooksReduced(books) = inbox.receive(10.seconds)
    Ok(Json.toJson(books))
  }

}
