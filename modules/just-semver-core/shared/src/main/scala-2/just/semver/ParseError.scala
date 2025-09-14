package just.semver

import just.semver.matcher.SemVerMatchers

/** @author Kevin Lee
  * @since 2018-10-21
  */
sealed trait ParseError

object ParseError {

  final case class InvalidAlphaNumHyphenError(c: Char, rest: List[Char]) extends ParseError
  case object EmptyAlphaNumHyphenError extends ParseError

  final case class LeadingZeroNumError(n: String) extends ParseError

  final case class PreReleaseParseError(parseError: ParseError) extends ParseError
  final case class BuildMetadataParseError(parseError: ParseError) extends ParseError

  final case class CombinedParseError(preReleaseError: ParseError, buildMetadataError: ParseError) extends ParseError

  final case class InvalidVersionStringError(value: String) extends ParseError

  final case class SemVerMatchersParseErrors(error: matcher.SemVerMatchers.ParseErrors) extends ParseError

  def invalidAlphaNumHyphenError(c: Char, rest: List[Char]): ParseError =
    InvalidAlphaNumHyphenError(c, rest)

  def emptyAlphaNumHyphenError: ParseError =
    EmptyAlphaNumHyphenError

  def leadingZeroNumError(n: String): ParseError =
    LeadingZeroNumError(n)

  def preReleaseParseError(parseError: ParseError): ParseError =
    PreReleaseParseError(parseError)

  def buildMetadataParseError(parseError: ParseError): ParseError =
    BuildMetadataParseError(parseError)

  def combine(preReleaseError: ParseError, buildMetadataError: ParseError): ParseError =
    CombinedParseError(
      preReleaseParseError(preReleaseError),
      buildMetadataParseError(buildMetadataError)
    )

  def invalidVersionStringError(value: String): ParseError =
    InvalidVersionStringError(value)

  def semVerMatchersParseErrors(error: SemVerMatchers.ParseErrors): ParseError = SemVerMatchersParseErrors(error)

  def fromAdditionalInfoParserError(additionalInfoParseError: AdditionalInfo.AdditionalInfoParseError): ParseError =
    additionalInfoParseError match {
      case AdditionalInfo.AdditionalInfoParseError.LeadingZeroNumError(n) =>
        ParseError.leadingZeroNumError(n)
      case AdditionalInfo.AdditionalInfoParseError.InvalidAlphaNumHyphenError(c, rest) =>
        ParseError.invalidAlphaNumHyphenError(c, rest)
      case AdditionalInfo.AdditionalInfoParseError.EmptyAlphaNumHyphenError =>
        ParseError.emptyAlphaNumHyphenError
    }

  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  def render(parseError: ParseError): String = parseError match {
    case InvalidAlphaNumHyphenError(c, rest) =>
      s"Invalid char for AlphaNumHyphen found. value: ${c.toString} / rest: ${rest.toString}"

    case EmptyAlphaNumHyphenError =>
      "AlphaNumHyphen cannot be empty but the given value is an empty String."

    case LeadingZeroNumError(n) =>
      s"Invalid Num value. It should not have any leading zeros. value: $n"

    case PreReleaseParseError(error) =>
      s"Error in parsing pre-release: ${render(error)}"

    case BuildMetadataParseError(error) =>
      s"Error in parsing build meta data: ${render(error)}"

    case CombinedParseError(preReleaseError, buildMetadataError) =>
      s"""Errors:
         |[1] ${render(preReleaseError)}
         |[2] ${render(buildMetadataError)}
         |""".stripMargin

    case InvalidVersionStringError(value) =>
      s"Invalid SemVer String. value: $value"

    case SemVerMatchersParseErrors(error) =>
      s"Error when parsing SemVerMatchers: ${error.render}"
  }

  implicit final class ParseErrorOps(private val parseError: ParseError) extends AnyVal {
    def render: String = just.semver.ParseError.render(parseError)
  }

}
