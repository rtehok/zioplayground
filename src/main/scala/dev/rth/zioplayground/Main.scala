package dev.rth
package zioplayground

import dev.rth.zioplayground.ourzio.*

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

trait BusinessLogic:
  def doesGoogleHaveEvenAmountOfPicturesOf(topic: String): Boolean

object BusinessLogic:
  lazy val live: ZIO[Google, Nothing, BusinessLogic] =
    ZIO.fromFunction {
      google => make(google)
    }

  def make(google: Google): BusinessLogic =
    new :
      override def doesGoogleHaveEvenAmountOfPicturesOf(topic: String): Boolean =
        google.countPicturesOf(topic) % 2 == 0

trait Google:
  def countPicturesOf(topic: String): Int

object GoogleImpl:
  lazy val live: ZIO[Any, Nothing, Google] =
    ZIO.succeed(make)

  lazy val make: Google =
    new :
      override def countPicturesOf(topic: String): Int =
        if topic == "cats" then 1337 else 1338

object DependencyGraph:
  lazy val live: ZIO[Any, Nothing, BusinessLogic] =
    for
      google <- GoogleImpl.live
      businessLogicMaker <- BusinessLogic.live.provide(google)
    yield businessLogicMaker

  lazy val make: BusinessLogic =
    val google: Google = GoogleImpl.make
    val businessLogic = BusinessLogic.make(google)
    businessLogic

object Main extends scala.App :
  Runtime.default.unsafeRunSync(program)

  lazy val program =
    for
      businessLogic <- DependencyGraph.live
      _ <- console.putStrLn("─" * 100)
      _ <- console.putStrLn(businessLogic.doesGoogleHaveEvenAmountOfPicturesOf("cats").toString)
      _ <- console.putStrLn(businessLogic.doesGoogleHaveEvenAmountOfPicturesOf("dogs").toString)
      _ <- console.putStrLn("─" * 100)
    yield ()

