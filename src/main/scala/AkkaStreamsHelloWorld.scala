import java.nio.file.Paths

import akka.NotUsed
import akka.stream.scaladsl.{FileIO, Source}
import akka.stream._
import akka.actor.ActorSystem
import akka.util.ByteString

import scala.concurrent.{ExecutionContextExecutor, Future}


object AkkaStreamsHelloWorld extends App {

  val source: Source[Int, NotUsed] = Source(1 to 100)

  implicit val system: ActorSystem = ActorSystem("QuickStart")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val done = source.runForeach(i ⇒ println(i))(materializer)

  done.onComplete(_ => system.terminate)

  val factorials = source.scan(BigInt(1))((acc, next) ⇒ acc * next)

  val result: Future[IOResult] =
    factorials
      .map(num ⇒ ByteString(s"$num\n"))
      .runWith(FileIO.toPath(Paths.get("factorials.txt")))
  
}
