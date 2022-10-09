package just.semver.parser

/** @author Kevin Lee
  * @since 2022-04-03
  */
final case class ParserError(message: String, parsed: Option[String], rest: Option[String])
object ParserError {
  implicit class ParserErrorOps(private val parserError: ParserError) extends AnyVal {
    def render: String =
      s"ParserError: ${parserError.message} after ${parserError.parsed.fold("Nothing")("parsing " + _)}. " +
        s"The rest: ${parserError.rest.getOrElse("")}"
  }
}
