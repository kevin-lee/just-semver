package just.decver.matcher

import just.decver.DecVer

/** @author Kevin Lee
  * @since 2022-04-03
  */
sealed trait DecVerMatcher
object DecVerMatcher {

  final case class Range(from: DecVer, to: DecVer) extends DecVerMatcher
  final case class Comparison(
    decVerComparison: DecVerComparison
  ) extends DecVerMatcher

  def range(from: DecVer, to: DecVer): DecVerMatcher = Range(from, to)

  def comparison(decVerComparison: DecVerComparison): DecVerMatcher = Comparison(decVerComparison)

  implicit class DecVerMatcherOps(private val decVerMatcher: DecVerMatcher) extends AnyVal {

    def matches(decVer: DecVer): Boolean = decVerMatcher match {
      case DecVerMatcher.Range(from, to) =>
        decVer >= from && decVer <= to

      case DecVerMatcher.Comparison(DecVerComparison(op, v)) =>
        op.eval(decVer, v)
    }

    def render: String = decVerMatcher match {
      case DecVerMatcher.Range(v1, v2) =>
        s"${v1.render} - ${v2.render}"

      case DecVerMatcher.Comparison(svc) =>
        svc.render
    }
  }

  sealed trait ParseError
  object ParseError {

    final case class RangeParseFailure(message: String, parseErrors: List[String], success: Option[DecVer])
        extends ParseError
    final case class DecVerComparisonParseFailure(decVerComparisonParseError: DecVerComparison.ParseError)
        extends ParseError

    def rangeParseFailure(
      message: String,
      parseErrors: List[String],
      success: Option[DecVer]
    ): ParseError =
      RangeParseFailure(message, parseErrors, success)

    def decVerComparisonParseFailure(decVerComparisonParseError: DecVerComparison.ParseError): ParseError =
      DecVerComparisonParseFailure(decVerComparisonParseError)

    implicit class ParseErrorOps(private val parseError: ParseError) extends AnyVal {
      def render: String = parseError match {
        case ParseError.RangeParseFailure(message, errors, success) =>
          s"DecVerMatcher.ParseError($message: Errors: [${errors.mkString(", ")}]" +
            s"${success.fold(", Success:")(succ => s", Success: ${succ.render}")})"

        case ParseError.DecVerComparisonParseFailure(err) =>
          err.render
      }
    }
  }
}
