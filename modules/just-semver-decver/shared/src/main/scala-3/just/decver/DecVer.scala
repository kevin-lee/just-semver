package just.decver

import just.decver.matcher.DecVerMatchers
import just.semver.AdditionalInfo.{BuildMetaInfo, PreRelease}
import just.semver.{AdditionalInfo, Compat, SemVer}

import scala.util.matching.Regex

/** @author Kevin Lee
  * @since 2024-04-16
  */
final case class DecVer(
  major: DecVer.Major,
  minor: DecVer.Minor,
  pre: Option[PreRelease],
  buildMetadata: Option[BuildMetaInfo]
) extends Ordered[DecVer]
    derives CanEqual {
  override def compare(that: DecVer): Int = DecVer.decVerOrdering.compare(this, that)
}
object DecVer extends Compat {
  given decVerOrdering: Ordering[DecVer] with {
    override def compare(v1: DecVer, v2: DecVer): Int =
      (
        v1.major.value.compareTo(v2.major.value),
        v1.minor.value.compareTo(v2.minor.value)
      ) match {
        case (0, 0) => 0
        case (0, minor) => minor
        case (major, _) => major
      }
  }

  def fromDecVer(decVer: DecVer): DecVer = DecVer(
    DecVer.Major(decVer.major.value),
    DecVer.Minor(decVer.minor.value),
    None,
    None
  )

  extension (decVer: DecVer) {

    def increaseMajor: DecVer =
      decVer.copy(major = DecVer.Major(decVer.major.value + 1))
    def increaseMinor: DecVer =
      decVer.copy(minor = DecVer.Minor(decVer.minor.value + 1))

    def render: String = decVer match {
      case DecVer(major, minor, pre, buildMetadata) =>
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

    def renderMajorMinor: String = decVer match {
      case DecVer(major, minor, _, _) =>
        s"${major.value.toString}.${minor.value.toString}"
    }

    def matches(decVerMatchers: DecVerMatchers): Boolean = decVerMatchers.matches(decVer)

    def unsafeMatches(decVerMatchers: String): Boolean =
      DecVerMatchers
        .unsafeParse(decVerMatchers)
        .matches(decVer)

    def toSemVer: SemVer = SemVer(
      SemVer.Major(decVer.major.value),
      SemVer.Minor(decVer.minor.value),
      SemVer.patch0,
      decVer.pre,
      decVer.buildMetadata
    )

//    def toDecVer: DecVer = DecVer(
//      DecVer.Major(decVer.major.value),
//      DecVer.Minor(decVer.minor.value),
//    )

  }

  val major0: Major = Major(0)
  val minor0: Minor = Minor(0)

  val DecimalVersionPattern: Regex = """(\d+)\.(\d+)(?:-([a-zA-Z\d-\.]+)?)?(?:\+([a-zA-Z\d-\.]+)?)?""".r

  def parse(version: String): Either[ParseError, DecVer] = Option(version)
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
              DecVer(
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
  def unsafeParse(version: String): DecVer =
    parse(version) match {
      case Right(ver) => ver
      case Left(err) => throw err // scalafix:ok DisableSyntax.throw
    }

  def fromSemVer(semVer: SemVer): DecVer =
    DecVer(
      DecVer.Major(semVer.major.value),
      DecVer.Minor(semVer.minor.value),
      semVer.pre,
      semVer.buildMetadata
    )

  def withMajorMinor(major: Major, minor: Minor): DecVer =
    DecVer(major, minor, None, None)

  def withMajor(major: Major): DecVer =
    DecVer(major, minor0, None, None)

  def withMinor(minor: Minor): DecVer =
    DecVer(major0, minor, None, None)

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

    final case class DecVerMatchersParseErrors(error: matcher.DecVerMatchers.ParseErrors) extends ParseError

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

    def decVerMatchersParseErrors(error: matcher.DecVerMatchers.ParseErrors): ParseError =
      DecVerMatchersParseErrors(error)

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

        case DecVerMatchersParseErrors(error) =>
          s"Error when parsing DecVerMatchers: ${error.render}"

      }

    }

  }
}
