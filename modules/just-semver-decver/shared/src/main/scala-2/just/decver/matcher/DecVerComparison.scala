package just.decver.matcher

import just.semver.expr.ComparisonOperator
import just.semver.parser.Parser
import just.semver.Compat
import just.decver.DecVer

/** @author Kevin Lee
  * @since 2022-04-05
  */
final case class DecVerComparison(comparisonOperator: ComparisonOperator, decVer: DecVer)
object DecVerComparison extends Compat {

  def parse(s: String): Either[ParseError, DecVerComparison] =
    Parser
      .charsIn(">!=<")
      .parse(s)
      .left
      .map(err => DecVerComparison.ParseError(s"Failed to parse operator from $s", err.render, None))
      .flatMap {
        case (rest, op) =>
          ComparisonOperator
            .parse(op)
            .left
            .map(err => DecVerComparison.ParseError(s"Failed to parse operator from $s", err, None))
            .flatMap { operator =>
              DecVer
                .parse(rest)
                .map(version => DecVerComparison(operator, version))
                .left
                .map(err =>
                  DecVerComparison.ParseError(
                    s"Parsing operator succeeded but failed to parse DecVer from $s",
                    err.render,
                    Some(op)
                  )
                )
            }
      }

  def unsafeParse(s: String): DecVerComparison =
    parse(s).fold(err => sys.error(err.render), identity)

  def render(decVerComparison: DecVerComparison): String = decVerComparison match {
    case DecVerComparison(op, v) => s"${op.render}${v.render}"
  }

  implicit class DecVerComparisonOps(private val decVerComparison: DecVerComparison) extends AnyVal {
    def render: String = DecVerComparison.render(decVerComparison)
  }

  final case class ParseError(message: String, error: String, success: Option[String])
  object ParseError {
    def render(decVerComparisonParseError: ParseError): String = decVerComparisonParseError match {
      case ParseError(message, err, success) =>
        s"DecVerComparison.ParseError($message: ParserError($err)${success.fold(", Success:")(succ => s", Success: $succ")})"
    }

    implicit class ParseErrorOps(private val decVerComparisonParseError: DecVerComparison.ParseError) extends AnyVal {
      def render: String = ParseError.render(decVerComparisonParseError)
    }
  }

}