package just.decver.matcher

import just.semver.expr.ComparisonOperator
import just.semver.parser.Parser
import just.semver.Compat
import just.decver.DecVerExt

/** @author Kevin Lee
  * @since 2022-04-05
  */
final case class DecVerExtComparison(comparisonOperator: ComparisonOperator, decVerExt: DecVerExt)
object DecVerExtComparison extends Compat {

  def parse(s: String): Either[ParseError, DecVerExtComparison] =
    Parser
      .charsIn(">!=<")
      .parse(s)
      .left
      .map(err => DecVerExtComparison.ParseError(s"Failed to parse operator from $s", err.render, None))
      .flatMap {
        case (rest, op) =>
          ComparisonOperator
            .parse(op)
            .left
            .map(err => DecVerExtComparison.ParseError(s"Failed to parse operator from $s", err, None))
            .flatMap { operator =>
              DecVerExt
                .parse(rest)
                .map(version => DecVerExtComparison(operator, version))
                .left
                .map(err =>
                  DecVerExtComparison.ParseError(
                    s"Parsing operator succeeded but failed to parse DecVerExt from $s",
                    err.render,
                    Some(op)
                  )
                )
            }
      }

  def unsafeParse(s: String): DecVerExtComparison =
    parse(s).fold(err => sys.error(err.render), identity)

  extension (decVerExtComparison: DecVerExtComparison) {
    def render: String = decVerExtComparison match {
      case DecVerExtComparison(op, v) => s"${op.render}${v.render}"
    }
  }

  final case class ParseError(message: String, error: String, success: Option[String])
  object ParseError {

    extension (decVerExtComparisonParseError: DecVerExtComparison.ParseError) {
      def render: String = decVerExtComparisonParseError match {
        case ParseError(message, err, success) =>
          s"DecVerExtComparison.ParseError($message: ParserError($err)${success
              .fold(", Success:")(succ => s", Success: $succ")})"
      }
    }
  }

}
