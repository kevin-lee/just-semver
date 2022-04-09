package just.semver.matcher

import just.semver.expr.ComparisonOperator
import just.semver.parser.Parser
import just.semver.{Compat, SemVer}

/** @author Kevin Lee
  * @since 2022-04-05
  */
final case class SemVerComparison(comparisonOperator: ComparisonOperator, semVer: SemVer) derives CanEqual
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

  extension (semVerComparison: SemVerComparison) {
    def render: String = semVerComparison match {
      case SemVerComparison(op, v) => s"${op.render}${v.render}"
    }
  }

  final case class ParseError(message: String, error: String, success: Option[String]) derives CanEqual
  object ParseError {

    extension (semVerComparisonParseError: SemVerComparison.ParseError) {
      def render: String = semVerComparisonParseError match {
        case ParseError(message, err, success) =>
          s"SemVerComparison.ParseError($message: ParserError($err)" +
            s"${success.fold(", Success:")(succ => s", Success: $succ")})"
      }
    }
  }

}
