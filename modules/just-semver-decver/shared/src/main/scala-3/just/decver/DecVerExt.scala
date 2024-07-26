package just.decver

import just.semver.AdditionalInfo.{BuildMetaInfo, PreRelease}
import just.semver.{AdditionalInfo, Compat, SemVer}

import scala.util.matching.Regex

/** @author Kevin Lee
  * @since 2024-04-16
  */
final case class DecVerExt(
  major: DecVerExt.Major,
  minor: DecVerExt.Minor,
  pre: Option[PreRelease],
  buildMetadata: Option[BuildMetaInfo]
) extends Ordered[DecVerExt]
    derives CanEqual {
  override def compare(that: DecVerExt): Int = DecVerExt.decVerExtOrdering.compare(this, that)
}
object DecVerExt extends Compat {
  given decVerExtOrdering: Ordering[DecVerExt] with {
    override def compare(v1: DecVerExt, v2: DecVerExt): Int =
      (
        v1.major.value.compareTo(v2.major.value),
        v1.minor.value.compareTo(v2.minor.value)
      ) match {
        case (0, 0) => 0
        case (0, minor) => minor
        case (major, _) => major
      }
  }

  def fromDecVer(decVer: DecVer): DecVerExt = DecVerExt(
    DecVerExt.Major(decVer.major.value),
    DecVerExt.Minor(decVer.minor.value),
    None,
    None
  )

  extension (decVerExt: DecVerExt) {

    def increaseMajor: DecVerExt =
      decVerExt.copy(major = DecVerExt.Major(decVerExt.major.value + 1))
    def increaseMinor: DecVerExt =
      decVerExt.copy(minor = DecVerExt.Minor(decVerExt.minor.value + 1))

    def render: String = decVerExt match {
      case DecVerExt(major, minor, pre, buildMetadata) =>
        val versionString = s"${major.value.toString}.${minor.value.toString}"

        val additionalInfoString =
          (pre, buildMetadata) match {
            case (Some(p), Some(m)) =>
              s"-${PreRelease.render(p)}+${BuildMetaInfo.render(m)}"
            case (Some(p), None) =>
              s"-${PreRelease.render(p)}"
            case (None, Some(m)) =>
              s"+${BuildMetaInfo.render(m)}"
            case (None, None) =>
              ""
          }
        versionString + additionalInfoString
    }

    def toSemVer: SemVer = SemVer(
      SemVer.Major(decVerExt.major.value),
      SemVer.Minor(decVerExt.minor.value),
      SemVer.patch0,
      decVerExt.pre,
      decVerExt.buildMetadata
    )

    def toDecVer: DecVer = DecVer(
      DecVer.Major(decVerExt.major.value),
      DecVer.Minor(decVerExt.minor.value),
    )

  }

  val major0: Major = Major(0)
  val minor0: Minor = Minor(0)

  val DecimalVersionPattern: Regex = """(\d+)\.(\d+)(?:-([a-zA-Z\d-\.]+)?)?(?:\+([a-zA-Z\d-\.]+)?)?""".r

  def parse(version: String): Either[ParseError, DecVerExt] = Option(version)
    .toRight(ParseError.nullValue)
    .filterOrElse(_.nonEmpty, ParseError.empty)
    .flatMap {
      case DecimalVersionPattern(major, minor, pre, meta) =>
        val preRelease = AdditionalInfo.parsePreRelease(pre)
        val metaInfo   = AdditionalInfo.parseBuildMetaInfo(meta)
        (preRelease, metaInfo) match {
          case (Left(preError), Left(metaError)) =>
            Left(
              ParseError.combine(
                ParseError.fromAdditionalInfoParserError(preError),
                ParseError.fromAdditionalInfoParserError(metaError)
              )
            )
          case (Left(preError), _) =>
            Left(ParseError.preReleaseParseError(ParseError.fromAdditionalInfoParserError(preError)))
          case (_, Left(metaError)) =>
            Left(ParseError.buildMetadataParseError(ParseError.fromAdditionalInfoParserError(metaError)))
          case (Right(preR), Right(metaI)) =>
            Right(
              DecVerExt(
                Major(major.toInt),
                Minor(minor.toInt),
                preR,
                metaI
              )
            )
        }

      case _ =>
        Left(ParseError.invalid(version))
    }

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def unsafeParse(version: String): DecVerExt =
    parse(version) match {
      case Right(ver) => ver
      case Left(err) => throw err // scalafix:ok DisableSyntax.throw
    }

  def fromSemVer(semVer: SemVer): DecVerExt =
    DecVerExt(
      DecVerExt.Major(semVer.major.value),
      DecVerExt.Minor(semVer.minor.value),
      semVer.pre,
      semVer.buildMetadata
    )

  def withMajorMinor(major: Major, minor: Minor): DecVerExt =
    DecVerExt(major, minor, None, None)

  def withMajor(major: Major): DecVerExt =
    DecVerExt(major, minor0, None, None)

  def withMinor(minor: Minor): DecVerExt =
    DecVerExt(major0, minor, None, None)

  final case class Major(value: Int) extends AnyVal

  final case class Minor(value: Int) extends AnyVal

  sealed abstract class ParseError extends RuntimeException {
    override def getMessage: String = this.render
  }
  object ParseError {
    case object NullValue extends ParseError
    case object Empty extends ParseError
    final case class Invalid(version: String) extends ParseError

    final case class LeadingZeroNumError(n: String) extends ParseError

    final case class InvalidAlphaNumHyphenError(c: Char, rest: List[Char]) extends ParseError
    case object EmptyAlphaNumHyphenError extends ParseError

    final case class PreReleaseParseError(parseError: ParseError) extends ParseError
    final case class BuildMetadataParseError(parseError: ParseError) extends ParseError

    final case class CombinedParseError(preReleaseError: ParseError, buildMetadataError: ParseError) extends ParseError

    def nullValue: ParseError                = NullValue
    def empty: ParseError                    = Empty
    def invalid(version: String): ParseError = Invalid(version)

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

    def fromAdditionalInfoParserError(additionalInfoParseError: AdditionalInfo.AdditionalInfoParseError): ParseError =
      additionalInfoParseError match {
        case AdditionalInfo.AdditionalInfoParseError.LeadingZeroNumError(n) =>
          ParseError.leadingZeroNumError(n)
        case AdditionalInfo.AdditionalInfoParseError.InvalidAlphaNumHyphenError(c, rest) =>
          ParseError.invalidAlphaNumHyphenError(c, rest)
        case AdditionalInfo.AdditionalInfoParseError.EmptyAlphaNumHyphenError =>
          ParseError.emptyAlphaNumHyphenError
      }
    extension (decVerError: ParseError) {

      @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
      def render: String = decVerError match {
        case ParseError.NullValue => "Version String is null"
        case ParseError.Empty => "Version String is an empty String"
        case ParseError.Invalid(version) => s"Invalue version: $version"

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

      }

    }

  }
}
