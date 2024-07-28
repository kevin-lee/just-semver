package just.decver.matcher

import just.decver.DecVerExt

/** @author Kevin Lee
  * @since 2022-04-03
  */
enum DecVerExtMatcher {
  case Range(from: DecVerExt, to: DecVerExt)
  case Comparison(decVerExtComparison: DecVerExtComparison)
}
object DecVerExtMatcher {

  def range(from: DecVerExt, to: DecVerExt): DecVerExtMatcher = DecVerExtMatcher.Range(from, to)

  def comparison(decVerExtComparison: DecVerExtComparison): DecVerExtMatcher =
    DecVerExtMatcher.Comparison(decVerExtComparison)

  extension (decVerExtMatcher: DecVerExtMatcher) {

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

  enum ParseError {
    case RangeParseFailure(message: String, parseErrors: List[String], success: Option[DecVerExt])
    case DecVerExtComparisonParseFailure(decVerExtComparisonParseError: DecVerExtComparison.ParseError)
  }
  object ParseError {

    def rangeParseFailure(
      message: String,
      parseErrors: List[String],
      success: Option[DecVerExt]
    ): ParseError =
      ParseError.RangeParseFailure(message, parseErrors, success)

    def decVerExtComparisonParseFailure(decVerExtComparisonParseError: DecVerExtComparison.ParseError): ParseError =
      ParseError.DecVerExtComparisonParseFailure(decVerExtComparisonParseError)

    extension (parseError: ParseError) {
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
