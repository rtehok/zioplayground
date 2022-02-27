package dev.rth
package zioplayground

import dev.rth.zioplayground.ourzio.*

@main def Main(args: String*): Unit =
  val trace = s"[${Console.BLUE}TRACE${Console.RESET}]"

  lazy val program =
    for
      _ <- console.putStrLn("─" * 100)
      _ <- console.putStrLn("What is your name?")
      //      name <- console.getStrLn
      name <- ZIO.succeed("Ray")
      _ <- console.putStrLn(s"Hello $name")
      //      _ <- ZIO.fail(throw RuntimeException("boom")) // don't throw, use effect instead
      //      _ <- ZIO.fail(RuntimeException("boom")) // produces a ZIO[RuntimeException, Unit]
      //      _ <- ZIO.effect(throw RuntimeException("boom")) // produces a ZIO[Throwable, Unit]
//      _ <- ZIO
//        .effect(throw RuntimeException("boom"))
//        .catchAll(_ => console.putStrLn("error catch"))
      _ <- ZIO
        .effect(throw RuntimeException("boom"))
        .mapError(_.getMessage)
      _ <- console.putStrLn("─" * 100)
    yield ()

  println(Runtime.default.unsafeRunSync(program))

