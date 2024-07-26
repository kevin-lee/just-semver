package just.semver

import just.semver.matcher.SemVerMatchers

/** @author Kevin Lee
  * @since 2018-10-21
  */
enum ParseError derives CanEqual {

  case InvalidAlphaNumHyphenError(c: Char, rest: List[Char])
  case EmptyAlphaNumHyphenError
  case LeadingZeroNumError(n: String)
  case PreReleaseParseError(parseError: ParseError)
  case BuildMetadataParseError(parseError: ParseError)
  case CombinedParseError(preReleaseError: ParseError, buildMetadataError: ParseError)
  case InvalidVersionStringError(value: String)
  case SemVerMatchersParseErrors(error: matcher.SemVerMatchers.ParseErrors)
}

object ParseError {
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

  def semVerMatchersParseErrors(error: SemVerMatchers.ParseErrors): ParseError =
    SemVerMatchersParseErrors(error)

  def fromAdditionalInfoParserError(additionalInfoParseError: AdditionalInfo.AdditionalInfoParseError): ParseError =
    additionalInfoParseError match {
      case AdditionalInfo.AdditionalInfoParseError.LeadingZeroNumError(n) =>
        ParseError.leadingZeroNumError(n)
      case AdditionalInfo.AdditionalInfoParseError.InvalidAlphaNumHyphenError(c, rest) =>
        ParseError.invalidAlphaNumHyphenError(c, rest)
      case AdditionalInfo.AdditionalInfoParseError.EmptyAlphaNumHyphenError =>
        ParseError.emptyAlphaNumHyphenError
    }

  extension (parseError: ParseError) {
    def render: String = parseError match {
      case InvalidAlphaNumHyphenError(c, rest) =>
        s"Invalid char for AlphaNumHyphen found. value: ${c.toString} / rest: ${rest.toString}"

      case EmptyAlphaNumHyphenError =>
        "AlphaNumHyphen cannot be empty but the given value is an empty String."

      case LeadingZeroNumError(n) =>
        s"Invalid Num value. It should not have any leading zeros. value: $n"

      case PreReleaseParseError(error) =>
        s"Error in parsing pre-release: ${error.render}"

      case BuildMetadataParseError(error) =>
        s"Error in parsing build meta data: ${error.render}"

      case CombinedParseError(preReleaseError, buildMetadataError) =>
        s"""Errors:
           |[1] ${preReleaseError.render}
           |[2] ${buildMetadataError.render}
           |""".stripMargin

      case InvalidVersionStringError(value) =>
        s"Invalid SemVer String. value: $value"

      case SemVerMatchersParseErrors(error) =>
        s"Error when parsing SemVerMatchers: ${error.render}"
    }
  }

}
