package just.semver.matcher

import just.semver.SemVer

/** @author Kevin Lee
  * @since 2022-04-03
  */
enum SemVerMatcher derives CanEqual {
  case Range(from: SemVer, to: SemVer)
  case Comparison(semVerComparison: SemVerComparison)
}

object SemVerMatcher {

  def range(from: SemVer, to: SemVer): SemVerMatcher = SemVerMatcher.Range(from, to)

  def comparison(semVerComparison: SemVerComparison): SemVerMatcher = SemVerMatcher.Comparison(semVerComparison)

  extension (semVerMatcher: SemVerMatcher) {

    def matches(semVer: SemVer): Boolean = semVerMatcher match {
      case SemVerMatcher.Range(from, to) =>
        semVer >= from && semVer <= to
      case SemVerMatcher.Comparison(SemVerComparison(op, v)) =>
        op.eval(semVer, v)
    }

    def render: String = semVerMatcher match {
      case SemVerMatcher.Range(v1, v2) =>
        s"${v1.render} - ${v2.render}"
      case SemVerMatcher.Comparison(svc) =>
        svc.render
    }
  }

  enum ParseError derives CanEqual {
    case RangeParseFailure(message: String, parseErrors: List[String], success: Option[SemVer])
    case SemVerComparisonParseFailure(semVerComparisonParseError: SemVerComparison.ParseError)
  }
  object ParseError {

    def rangeParseFailure(
      message: String,
      parseErrors: List[String],
      success: Option[SemVer]
    ): ParseError =
      ParseError.RangeParseFailure(message, parseErrors, success)

    def semVerComparisonParseFailure(semVerComparisonParseError: SemVerComparison.ParseError): ParseError =
      ParseError.SemVerComparisonParseFailure(semVerComparisonParseError)

    extension (parseError: ParseError) {
      def render: String = parseError match {
        case ParseError.RangeParseFailure(message, errors, success) =>
          s"SemVerMatcher.ParseError($message: Errors: [${errors.mkString(", ")}]" +
            s"${success.fold(", Success:")(succ => s", Success: ${succ.render}")})"

        case ParseError.SemVerComparisonParseFailure(err) =>
          err.render
      }
    }
  }
}
