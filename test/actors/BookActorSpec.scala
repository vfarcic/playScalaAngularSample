package actors

import org.specs2.mutable.{After, Specification}
import akka.testkit._
import akka.actor._
import models.{BookReduced, Book}
import scala.concurrent.duration._

abstract class AkkaTestkitSpecs2Support extends TestKit(ActorSystem()) with After with ImplicitSender {
  def after = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }
}

class BookActorSpec extends Specification {

  "BookActor#ListBooks" should {

    "respond with a list of books" in new AkkaTestkitSpecs2Support {
      val bookActor = system.actorOf(Props[BookActor], "bookActor")
      bookActor ! ListBooks
      val books = expectMsgType[Books].books
      books must beAnInstanceOf[List[Book]]
      books must not have size(0)
      books.head must beAnInstanceOf[Book]
    }

  }

  "BookActor#ListBooksReduced" should {

    "respond with a reduced list of books" in new AkkaTestkitSpecs2Support {
      val bookActor = system.actorOf(Props[BookActor], "bookActor")
      bookActor ! ListBooksReduced
      val books = expectMsgType[BooksReduced].books
      books must beAnInstanceOf[List[BookReduced]]
      books must not have size(0)
      books.head must beAnInstanceOf[BookReduced]
    }

  }

  "BookActor#SaveBook" should {

    val title = "myTestTitle"
    val book = Book(12345, "myTestImage", title, "myTestAuthor", 123.45, "myTestLink")

    "save a book" in new AkkaTestkitSpecs2Support {
      val bookActor = system.actorOf(Props[BookActor], "bookActor")
      bookActor ! SaveBook(book)
      val status = expectMsgType[Status]
      Book.list must contain(book)
    }

    "respond with Status" in new AkkaTestkitSpecs2Support {
      val bookActor = system.actorOf(Props[BookActor], "bookActor")
      bookActor ! SaveBook(book)
      val status = expectMsgType[Status]
      status.status must be equalTo "OK"
      status.message must be equalTo s"Book $title has been saved."
    }

  }

}

