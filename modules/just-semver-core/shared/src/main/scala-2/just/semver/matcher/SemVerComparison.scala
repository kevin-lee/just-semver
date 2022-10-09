package just.semver.matcher

import just.semver.expr.ComparisonOperator
import just.semver.parser.Parser
import just.semver.{Compat, SemVer}

/** @author Kevin Lee
  * @since 2022-04-05
  */
final case class SemVerComparison(comparisonOperator: ComparisonOperator, semVer: SemVer)
object SemVerComparison extends Compat {

  def parse(s: String): Either[ParseError, SemVerComparison] =
    Parser
      .charsIn(">!=<")
      .parse(s)
      .left
      .map(err => SemVerComparison.ParseError(s"Failed to parse operator from $s", err.render, None))
      .flatMap {
        case (rest, op) =>
          ComparisonOperator
            .parse(op)
            .left
            .map(err => SemVerComparison.ParseError(s"Failed to parse operator from $s", err, None))
            .flatMap { operator =>
              SemVer
                .parse(rest)
                .map(version => SemVerComparison(operator, version))
                .left
                .map(err =>
                  SemVerComparison.ParseError(
                    s"Parsing operator succeeded but failed to parse SemVer from $s",
                    err.render,
                    Some(op)
                  )
                )
            }
      }

  def unsafeParse(s: String): SemVerComparison =
    parse(s).fold(err => sys.error(err.render), identity)

  def render(semVerComparison: SemVerComparison): String = semVerComparison match {
    case SemVerComparison(op, v) => s"${op.render}${v.render}"
  }

  implicit class SemVerComparisonOps(private val semVerComparison: SemVerComparison) extends AnyVal {
    def render: String = SemVerComparison.render(semVerComparison)
  }

  final case class ParseError(message: String, error: String, success: Option[String])
  object ParseError {
    def render(semVerComparisonParseError: ParseError): String = semVerComparisonParseError match {
      case ParseError(message, err, success) =>
        s"SemVerComparison.ParseError($message: ParserError($err)${success.fold(", Success:")(succ => s", Success: $succ")})"
    }

    implicit class ParseErrorOps(private val semVerComparisonParseError: SemVerComparison.ParseError) extends AnyVal {
      def render: String = ParseError.render(semVerComparisonParseError)
    }
  }

}
