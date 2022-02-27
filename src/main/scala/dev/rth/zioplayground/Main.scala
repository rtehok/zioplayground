package dev.rth
package zioplayground

import ourzio.*

@main def Main(args: String*): Unit =
  val trace = s"[${Console.BLUE}TRACE${Console.RESET}]"

  lazy val program =
    for
      _ <- console.putStrLn("─" * 100)
      _ <- console.putStrLn("What is your name?")
//      name <- console.getStrLn
      name <- ZIO.succeed("Ray")
      _ <- console.putStrLn(s"Hello $name")
      _ <- console.putStrLn("─" * 100)
    yield ()

  Runtime.default.unsafeRunSync(program)

