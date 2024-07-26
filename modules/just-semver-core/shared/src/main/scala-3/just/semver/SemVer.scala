package just.semver

import just.Common.*
import just.decver.DecVer
import just.semver.AdditionalInfo.{BuildMetaInfo, PreRelease}
import just.semver.SemVer.{Major, Minor, Patch}
import just.semver.matcher.SemVerMatchers

import scala.util.matching.Regex

/** @author Kevin Lee
  * @since 2018-10-21
  */
final case class SemVer(
  major: Major,
  minor: Minor,
  patch: Patch,
  pre: Option[PreRelease],
  buildMetadata: Option[BuildMetaInfo]
) extends Ordered[SemVer]
    derives CanEqual {

  override def compare(that: SemVer): Int = {
    (
      this.major.value.compareTo(that.major.value),
      this.minor.value.compareTo(that.minor.value),
      this.patch.value.compareTo(that.patch.value)
    ) match {
      case (0, 0, 0) =>
        (this.pre, that.pre) match {
          case (Some(thisPre), Some(thatPre)) =>
            thisPre.identifier.compareElems(thatPre.identifier)

          case (Some(_), None) =>
            -1
          case (None, Some(_)) =>
            1
          case (None, None) =>
            0
        }
      case (0, 0, pt) =>
        pt
      case (0, mn, _) =>
        mn
      case (mj, _, _) =>
        mj
    }
  }

}

object SemVer {

  type Major = Major.Major
  object Major {
    opaque type Major = Int
    def apply(major: Int): Major         = major
    def unapply(major: Major): Some[Int] = Some(major)

    given majorCanEqual: CanEqual[Major, Major] = CanEqual.derived

    extension (major0: Major) {
      def value: Int = major0
      def major: Int = major0
    }
  }

  type Minor = Minor.Minor
  object Minor {
    opaque type Minor = Int
    def apply(minor: Int): Minor         = minor
    def unapply(minor: Minor): Some[Int] = Some(minor)

    given minorCanEqual: CanEqual[Minor, Minor] = CanEqual.derived

    extension (minor0: Minor) {
      def value: Int = minor0
      def minor: Int = minor0
    }
  }

  type Patch = Patch.Patch
  object Patch {
    opaque type Patch = Int
    def apply(patch: Int): Patch         = patch
    def unapply(patch: Patch): Some[Int] = Some(patch)

    given patchCanEqual: CanEqual[Patch, Patch] = CanEqual.derived

    extension (patch0: Patch) {
      def value: Int = patch0
      def patch: Int = patch0
    }
  }

  val major0: Major = Major(0)
  val minor0: Minor = Minor(0)
  val patch0: Patch = Patch(0)

  val semVerRegex: Regex = """(\d+)\.(\d+)\.(\d+)(?:-([a-zA-Z\d-\.]+)?)?(?:\+([a-zA-Z\d-\.]+)?)?""".r

  extension (semVer: SemVer) {
    inline def majorMinorPatch: (SemVer.Major, SemVer.Minor, SemVer.Patch) =
      (semVer.major, semVer.minor, semVer.patch)

    def renderMajorMinorPatch: String =
      s"${semVer.major.value.toString}.${semVer.minor.value.toString}.${semVer.patch.value.toString}"

    def render: String = semVer match {
      case SemVer(major, minor, patch, pre, buildMetadata) =>
        val versionString        = s"${major.value.toString}.${minor.value.toString}.${patch.value.toString}"
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

    def matches(semVerMatchers: SemVerMatchers): Boolean = semVerMatchers.matches(semVer)

    def unsafeMatches(semVerMatchers: String): Boolean =
      SemVerMatchers
        .unsafeParse(semVerMatchers)
        .matches(semVer)

    def toDecVer: DecVer = DecVer.fromSemVer(semVer)

  }

  def unsafeParse(version: String): SemVer =
    parse(version) match {
      case Right(semVer) =>
        semVer
      case Left(error) =>
        sys.error(ParseError.render(error))
    }

  def parseUnsafe(version: String): SemVer = unsafeParse(version)

  def parse(version: String): Either[ParseError, SemVer] = version match {
    case semVerRegex(major, minor, patch, pre, meta) =>
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
            SemVer(
              Major(major.toInt),
              Minor(minor.toInt),
              Patch(patch.toInt),
              preR,
              metaI
            )
          )
      }

    case _ =>
      Left(ParseError.invalidVersionStringError(version))
  }

  def semVer(major: Major, minor: Minor, patch: Patch): SemVer =
    SemVer(major, minor, patch, None, None)

  def withMajor(major: Major): SemVer =
    SemVer(major, minor0, patch0, None, None)

  def withMinor(minor: Minor): SemVer =
    SemVer(major0, minor, patch0, None, None)

  def withPatch(patch: Patch): SemVer =
    SemVer(major0, minor0, patch, None, None)

  def increaseMajor(semVer: SemVer): SemVer =
    semVer.copy(major = Major(semVer.major.value + 1))

  def increaseMinor(semVer: SemVer): SemVer =
    semVer.copy(minor = Minor(semVer.minor.value + 1))

  def increasePatch(semVer: SemVer): SemVer =
    semVer.copy(patch = Patch(semVer.patch.value + 1))

  def fromDecVer(decVer: DecVer): SemVer =
    SemVer.semVer(SemVer.Major(decVer.major.value), SemVer.Minor(decVer.minor.value), patch0)

}
