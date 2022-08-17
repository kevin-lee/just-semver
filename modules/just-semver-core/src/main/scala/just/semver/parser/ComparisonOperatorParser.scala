package just.semver.parser

import just.semver.Compat
import just.semver.expr.ComparisonOperator

/** @author Kevin Lee
  * @since 2022-04-03
  */
object ComparisonOperatorParser extends Compat {
  private val operators = "<>=!"
  private val parser    = Parser.charsWhile(c => operators.contains(c))

  def parse(s: String): Either[ParserError, (ComparisonOperator, String)] = {
    parser.parse(s).flatMap {
      case (rest, parsed) =>
        ComparisonOperator
          .parse(parsed)
          .map(operator => (operator, rest))
          .left
          .map(err =>
            ParserError(
              s"Parsed successfully but failed to create ComparisonOperator: Error: $err",
              Some(parsed),
              Some(rest)
            )
          )
    }
  }
}
