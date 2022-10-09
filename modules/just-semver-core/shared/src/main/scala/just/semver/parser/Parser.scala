package just.semver.parser

/** @author Kevin Lee
  * @since 2022-04-03
  */
abstract class Parser[+A] {
  self =>

  def parseTo(state: Parser.State): A

  def parse(s: String): Either[ParserError, (String, A)] = {
    val state  = Parser.State(s)
    val result = parseTo(state)
    val error  = state.error

    if (error.isEmpty)
      Right((s.substring(state.offset), result))
    else
      Left(
        ParserError(
          s"Error at ${state.offset.toString}",
          Some(s.substring(0, state.offset)).filter(_.nonEmpty),
          Some(s).filter(_.nonEmpty)
        )
      )
  }

  def map[B](f: A => B): Parser[B] = new Parser[B] {
    override def parseTo(state: Parser.State): B =
      f(self.parseTo(state))
  }

  def flatMap[B](f: A => Parser[B]): Parser[B] = new Parser[B] {
    override def parseTo(state: Parser.State): B = {
      val result = self.parseTo(state)
      f(result).parseTo(state)
    }
  }

  def ~[B](next: Parser[B]): Parser[(A, B)] = new Parser[(A, B)] {
    override def parseTo(state: Parser.State): (A, B) = {
      val a = self.parseTo(state)
      val b = next.parseTo(state)
      (a, b)
    }
  }

}

object Parser {

  class State(val value: String) {
    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var offset: Int = 0 // scalafix:ok DisableSyntax.var

    @SuppressWarnings(Array("org.wartremover.warts.Var"))
    var error: Option[ParserError] = None // scalafix:ok DisableSyntax.var
  }
  object State {
    def apply(value: String): State = new State(value)
  }

  def charsWhile(f: Char => Boolean): Parser[String] = new Parser[String] {
    override def parseTo(state: State): String = {
      val parsed    = state.value.zipWithIndex.drop(state.offset).takeWhile { case (c, index) => f(c) }
      val parsedStr = parsed.map(_._1).mkString
      val offset    = parsed.lastOption.map(_._2).getOrElse(0)
      if (parsedStr.isEmpty) {
        state.error = Some(ParserError("", None, Some(state.value)))
        ""
      } else
        state.offset = offset + 1
        parsedStr
    }
  }

  def charsIn(in: Seq[Char]): Parser[String] = charsWhile(c => in.contains(c))

}
