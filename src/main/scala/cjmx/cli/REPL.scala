package cjmx.cli

import scala.annotation.tailrec

import java.io.PrintWriter

import sbt.LineReader
import sbt.complete.Parser

import scalaz._
import Scalaz._

import cjmx.util.JMX


object REPL {
  def run(reader: Parser[_] => LineReader, out: PrintWriter): Int = {
    @tailrec def runR(state: ActionContext): Int = {
      state.runState match {
        case Running =>
          val parser = state.connectionState match {
            case Disconnected => Parsers.disconnected(JMX.localVMs)
            case Connected(cnx) => Parsers.connected(cnx)
          }
          def readLine = reader(parser).readLine("> ").filter { _.nonEmpty }
          val result = for {
            line <- readLine.success[NonEmptyList[String]]
            parse = (line: String) => Validation.fromEither(Parser.parse(line, parser)).toValidationNel
            action <- line.fold(parse, NoopAction.success)
            result <- action(state)
          } yield result
          val newState = result match {
            case Success((newState, msgs)) =>
              msgs foreach out.println
              newState
            case Failure(errs) =>
              val lines = errs.list flatMap { _.split('\n') }
              val formatted = lines map { e => "[%serror%s] %s".format(Console.RED, Console.RESET, e) }
              formatted foreach out.println
              state
          }
          runR(newState)

        case Exit(statusCode) =>
          statusCode
      }
    }

    runR(ActionContext())
  }
}