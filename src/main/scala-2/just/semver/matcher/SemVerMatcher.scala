package just.semver.matcher

import just.semver.SemVer

/** @author Kevin Lee
  * @since 2022-04-03
  */
sealed trait SemVerMatcher
object SemVerMatcher {

  final case class Range(from: SemVer, to: SemVer) extends SemVerMatcher
  final case class Comparison(
    semVerComparison: SemVerComparison
  ) extends SemVerMatcher

  def range(from: SemVer, to: SemVer): SemVerMatcher = Range(from, to)

  def comparison(semVerComparison: SemVerComparison): SemVerMatcher = Comparison(semVerComparison)

  implicit class SemVerMatcherOps(private val semVerMatcher: SemVerMatcher) extends AnyVal {

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

  sealed trait ParseError
  object ParseError {

    final case class RangeParseFailure(message: String, parseErrors: List[String], success: Option[SemVer])
        extends ParseError
    final case class SemVerComparisonParseFailure(semVerComparisonParseError: SemVerComparison.ParseError)
        extends ParseError

    def rangeParseFailure(
      message: String,
      parseErrors: List[String],
      success: Option[SemVer]
    ): ParseError =
      RangeParseFailure(message, parseErrors, success)

    def semVerComparisonParseFailure(semVerComparisonParseError: SemVerComparison.ParseError): ParseError =
      SemVerComparisonParseFailure(semVerComparisonParseError)

    implicit class ParseErrorOps(private val parseError: ParseError) extends AnyVal {
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
