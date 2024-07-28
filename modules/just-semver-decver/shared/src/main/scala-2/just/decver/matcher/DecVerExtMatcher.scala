package just.decver.matcher

import just.decver.DecVerExt

/** @author Kevin Lee
  * @since 2022-04-03
  */
sealed trait DecVerExtMatcher
object DecVerExtMatcher {

  final case class Range(from: DecVerExt, to: DecVerExt) extends DecVerExtMatcher
  final case class Comparison(
    decVerExtComparison: DecVerExtComparison
  ) extends DecVerExtMatcher

  def range(from: DecVerExt, to: DecVerExt): DecVerExtMatcher = Range(from, to)

  def comparison(decVerExtComparison: DecVerExtComparison): DecVerExtMatcher = Comparison(decVerExtComparison)

  implicit class DecVerExtMatcherOps(private val decVerExtMatcher: DecVerExtMatcher) extends AnyVal {

    def matches(decVerExt: DecVerExt): Boolean = decVerExtMatcher match {
      case DecVerExtMatcher.Range(from, to) =>
        decVerExt >= from && decVerExt <= to

      case DecVerExtMatcher.Comparison(DecVerExtComparison(op, v)) =>
        op.eval(decVerExt, v)
    }

    def render: String = decVerExtMatcher match {
      case DecVerExtMatcher.Range(v1, v2) =>
        s"${v1.render} - ${v2.render}"

      case DecVerExtMatcher.Comparison(svc) =>
        svc.render
    }
  }

  sealed trait ParseError
  object ParseError {

    final case class RangeParseFailure(message: String, parseErrors: List[String], success: Option[DecVerExt])
        extends ParseError
    final case class DecVerExtComparisonParseFailure(decVerExtComparisonParseError: DecVerExtComparison.ParseError)
        extends ParseError

    def rangeParseFailure(
      message: String,
      parseErrors: List[String],
      success: Option[DecVerExt]
    ): ParseError =
      RangeParseFailure(message, parseErrors, success)

    def decVerExtComparisonParseFailure(decVerExtComparisonParseError: DecVerExtComparison.ParseError): ParseError =
      DecVerExtComparisonParseFailure(decVerExtComparisonParseError)

    implicit class ParseErrorOps(private val parseError: ParseError) extends AnyVal {
      def render: String = parseError match {
        case ParseError.RangeParseFailure(message, errors, success) =>
          s"DecVerExtMatcher.ParseError($message: Errors: [${errors.mkString(", ")}]" +
            s"${success.fold(", Success:")(succ => s", Success: ${succ.render}")})"

        case ParseError.DecVerExtComparisonParseFailure(err) =>
          err.render
      }
    }
  }
}
