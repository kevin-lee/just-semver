package just.decver.matcher

import just.decver.DecVer

/** @author Kevin Lee
  * @since 2022-04-03
  */
enum DecVerMatcher {
  case Range(from: DecVer, to: DecVer)
  case Comparison(decVerComparison: DecVerComparison)
}
object DecVerMatcher {

  def range(from: DecVer, to: DecVer): DecVerMatcher = DecVerMatcher.Range(from, to)

  def comparison(decVerComparison: DecVerComparison): DecVerMatcher =
    DecVerMatcher.Comparison(decVerComparison)

  extension (decVerMatcher: DecVerMatcher) {

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

  enum ParseError {
    case RangeParseFailure(message: String, parseErrors: List[String], success: Option[DecVer])
    case DecVerComparisonParseFailure(decVerComparisonParseError: DecVerComparison.ParseError)
  }
  object ParseError {

    def rangeParseFailure(
      message: String,
      parseErrors: List[String],
      success: Option[DecVer]
    ): ParseError =
      ParseError.RangeParseFailure(message, parseErrors, success)

    def decVerComparisonParseFailure(decVerComparisonParseError: DecVerComparison.ParseError): ParseError =
      ParseError.DecVerComparisonParseFailure(decVerComparisonParseError)

    extension (parseError: ParseError) {
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
