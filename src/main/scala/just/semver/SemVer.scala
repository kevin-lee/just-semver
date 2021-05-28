package just.semver

import just.Common

import just.semver.AdditionalInfo.{BuildMetaInfo, PreRelease}
import just.semver.SemVer.{Major, Minor, Patch}

import scala.util.matching.Regex

/**
 * @author Kevin Lee
 * @since 2018-10-21
 */
final case class SemVer(
    major: Major
  , minor: Minor
  , patch: Patch
  , pre: Option[PreRelease]
  , buildMetadata: Option[BuildMetaInfo]
  ) extends Ordered[SemVer] {

  override def compare(that: SemVer): Int = {
    ( this.major.major.compareTo(that.major.major)
    , this.minor.minor.compareTo(that.minor.minor)
    , this.patch.patch.compareTo(that.patch.patch) ) match {
      case (0, 0, 0) =>
        (this.pre, that.pre) match {
          case (Some(thisPre), Some(thatPre)) =>
            Common.compareElems(thisPre.identifier, thatPre.identifier)
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

  final case class Major(major: Int) extends AnyVal
  final case class Minor(minor: Int) extends AnyVal
  final case class Patch(patch: Int) extends AnyVal

  val major0: Major = Major(0)
  val minor0: Minor = Minor(0)
  val patch0: Patch = Patch(0)

  val semVerRegex: Regex =
    """(\d+)\.(\d+)\.(\d+)(?:-([a-zA-Z\d-\.]+)?)?(?:\+([a-zA-Z\d-\.]+)?)?""".r

  implicit final class SemVerOps(val semVer: SemVer) extends AnyVal {
    @inline def majorMinorPatch: (SemVer.Major, SemVer.Minor, SemVer.Patch) =
      SemVer.majorMinorPatch(semVer)

    @inline def render: String = SemVer.render(semVer)
  }

  def majorMinorPatch(semVer: SemVer): (SemVer.Major, SemVer.Minor, SemVer.Patch) =
    (semVer.major, semVer.minor, semVer.patch)

  def render(semVer: SemVer): String = semVer match {
    case SemVer(major, minor, patch, pre, buildMetadata) =>
      val versionString = s"${major.major.toString}.${minor.minor.toString}.${patch.patch.toString}"
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

  def parseUnsafe(version: String): SemVer =
    parse(version) match {
      case Right(semVer) =>
        semVer
      case Left(error) =>
        sys.error(ParseError.render(error))
    }

  def parse(version: String): Either[ParseError, SemVer] = version match {
    case semVerRegex(major, minor, patch, pre, meta) =>
      val preRelease = AdditionalInfo.parsePreRelease(pre)
      val metaInfo = AdditionalInfo.parseBuildMetaInfo(meta)
      (preRelease, metaInfo) match {
        case (Left(preError), Left(metaError)) =>
          Left(ParseError.combine(preError, metaError))
        case (Left(preError), _) =>
          Left(ParseError.preReleaseParseError(preError))
        case (_, Left(metaError)) =>
          Left(ParseError.buildMetadataParseError(metaError))
        case (Right(preR), Right(metaI)) =>
          Right(
            SemVer(
              Major(major.toInt), Minor(minor.toInt), Patch(patch.toInt),
              preR, metaI
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
    semVer.copy(major = Major(semVer.major.major + 1))

  def increaseMinor(semVer: SemVer): SemVer =
    semVer.copy(minor = Minor(semVer.minor.minor + 1))

  def increasePatch(semVer: SemVer): SemVer =
    semVer.copy(patch = Patch(semVer.patch.patch + 1))

}