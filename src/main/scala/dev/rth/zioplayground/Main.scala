package dev.rth
package zioplayground

import zio.*

object ourConsole:
  def putStrLn(line: String) =
    ZIO.succeed(println(line))

  val getStrLn =
    ZIO.succeed(scala.io.StdIn.readLine())

@main def Main(args: String*): Unit =
  val trace = s"[${Console.BLUE}TRACE${Console.RESET}]"

  lazy val program =
    for
      _ <- ourConsole.putStrLn("─" * 100)
      _ <- ourConsole.putStrLn("What is your name?").debug(trace)
      name <- ourConsole.getStrLn
      _ <- ourConsole.putStrLn(s"Hello $name").debug(trace)
//      _ <- ZIO.fail(sys.error("boom")) // throws error improperly
//      _ <- ZIO.effect(throw RuntimeException("boom")) // caught somewhere
      _ <- ourConsole.putStrLn("─" * 100)
    yield ()

  Runtime.default.unsafeRunSync(program)

