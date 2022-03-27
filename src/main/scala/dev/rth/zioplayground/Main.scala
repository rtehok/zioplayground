package dev.rth
package zioplayground

import dev.rth.zioplayground.ourzio.*
//import zio.*

//@main def Main(args: String*): Unit =
//  val trace = s"[${Console.BLUE}TRACE${Console.RESET}]"
//
//  lazy val program =
//    for
//      _ <- console.putStrLn("─" * 100)
//      _ <- console.putStrLn("What is your name?")
//      //      name <- console.getStrLn
//      name <- ZIO.succeed("Ray")
//      _ <- console.putStrLn(s"Hello $name")
//      //      _ <- ZIO.fail(throw RuntimeException("boom")) // don't throw, use effect instead
//      //      _ <- ZIO.fail(RuntimeException("boom")) // produces a ZIO[RuntimeException, Unit]
//      //      _ <- ZIO.effect(throw RuntimeException("boom")) // produces a ZIO[Throwable, Unit]
////      _ <- ZIO
////        .effect(throw RuntimeException("boom"))
////        .catchAll(_ => console.putStrLn("error catch"))
////      _ <- ZIO
////        .effect(throw RuntimeException("boom"))
////        .mapError(_.getMessage)
//      _ <- console.putStrLn("─" * 100)
//    yield ()
//
//  println(Runtime.default.unsafeRunSync(program))

object businessLogic:
  trait BusinessLogic:
    def doesGoogleHaveEvenAmountOfPicturesOf(topic: String): ZIO[Any, Nothing, Boolean]

  object BusinessLogic:
    lazy val live: ZIO[Google, Nothing, BusinessLogic] =
      ZIO.fromFunction(make)

    def make(google: Google): BusinessLogic =
      new:
        override def doesGoogleHaveEvenAmountOfPicturesOf(topic: String): ZIO[Any, Nothing, Boolean] =
          google.countPicturesOf(topic).map(_ % 2 == 0)

  def doesGoogleHaveEvenAmountOfPicturesOf(topic: String): ZIO[BusinessLogic, Nothing, Boolean] =
    ZIO.accessM(_.doesGoogleHaveEvenAmountOfPicturesOf(topic))

trait Google:
  def countPicturesOf(topic: String): ZIO[Any, Nothing, Int]

object GoogleImpl:
  lazy val live: ZIO[Any, Nothing, Google] =
    ZIO.succeed(make)

  lazy val make: Google =
    (topic: String) => ZIO.succeed(if topic == "cats" then 1337 else 1338)

object DependencyGraph:
  lazy val live: ZIO[Any, Nothing, businessLogic.BusinessLogic] =
    for
      g <- GoogleImpl.live
      bl <- businessLogic.BusinessLogic.live.provide(g)
    yield bl

  lazy val make: businessLogic.BusinessLogic =
    val g = GoogleImpl.make
    val bl = businessLogic.BusinessLogic.make(g)
    bl

trait HasConsole:
  def console: zioplayground.ourzio.console.Console
  
trait HasBusinessLogic:
  def businessLogic: zioplayground.businessLogic.BusinessLogic

object Main extends scala.App :
  Runtime.default.unsafeRunSync(program)

  lazy val program =
    for
      bl <- DependencyGraph.live
      p <- makeProgram.provide{
        new HasConsole with HasBusinessLogic:
          override lazy val console = zioplayground.ourzio.console.Console.make
          override lazy val businessLogic = bl
      }
    yield p

//  def makeProgram(bl: businessLogic.BusinessLogic) =
  lazy val makeProgram =
    for
      env <- ZIO.environment[HasConsole & HasBusinessLogic]
      _ <- env.console.putStrLn("─" * 100)
      cats <- env.businessLogic.doesGoogleHaveEvenAmountOfPicturesOf("cats")
      _ <- env.console.putStrLn(cats.toString)
      dogs <- env.businessLogic.doesGoogleHaveEvenAmountOfPicturesOf("dogs")
      _ <- env.console.putStrLn(dogs.toString)
      _ <- env.console.putStrLn("─" * 100)
    yield ()

