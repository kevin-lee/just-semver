package io.kevinlee.semver

import io.kevinlee.semver.AdditionalInfo.{BuildMetaInfo, PreRelease}
import io.kevinlee.Common._
import io.kevinlee.CommonPredef._

import scala.annotation.tailrec
import scala.util.matching.Regex

/**
  * @author Kevin Lee
  * @since 2018-10-21
  */
final case class AlphaNumHyphenGroup(values: List[AlphaNumHyphen]) extends Ordered[AlphaNumHyphenGroup] {
  override def compare(that: AlphaNumHyphenGroup): Int =
    compareElems(this.values, that.values)
}

object AlphaNumHyphenGroup {

  import AlphaNumHyphen._

  def render(alphaNumHyphenGroup: AlphaNumHyphenGroup): String =
    alphaNumHyphenGroup.values.map(AlphaNumHyphen.render).mkString

  def parse(value: String): Either[ParseError, AlphaNumHyphenGroup] = {

    @tailrec
    def accumulate(cs: List[Char], chars: AlphaNumHyphen, acc: Vector[AlphaNumHyphen]): Either[ParseError, Vector[AlphaNumHyphen]] =
      cs match {
        case x :: xs =>

          if (x.isDigit) {
            chars match {
              case Num(ns) =>
                accumulate(xs, Num(ns :+ x), acc)

              case _ =>
                accumulate(xs, Num(x.toString), acc :+ chars)
            }
          } else if (x === '-') {
            accumulate(xs, Hyphen, acc :+ chars)
          } else if (x.isUpper || x.isLower) {
            chars match {
              case Alphabet(as) =>
                accumulate(xs, Alphabet(as :+ x), acc)

              case _ =>
                accumulate(xs, Alphabet(x.toString), acc :+ chars)
            }
          } else {
            Left(
              ParseError.invalidAlphaNumHyphenError(x, xs)
            )
          }

        case Nil =>
          Right(acc :+ chars)
      }

    value.toList match {
      case x :: xs =>
        val result =
          if (x.isDigit) {
            accumulate(xs, Num(x.toString), Vector.empty)
          } else if (x === '-')
            accumulate(xs, Hyphen, Vector.empty)
          else if (x.isLower || x.isUpper)
            accumulate(xs, Alphabet(x.toString), Vector.empty)
          else
            Left(
              ParseError.invalidAlphaNumHyphenError(x, xs)
            )

        result.right.map(groups => AlphaNumHyphenGroup(groups.toList))

      case Nil =>
        Left(ParseError.emptyAlphaNumHyphenError)
    }

  }
}

sealed trait AlphaNumHyphen extends Ordered[AlphaNumHyphen] {

  import AlphaNumHyphen._

  override def compare(that: AlphaNumHyphen): Int =
    (this, that) match {
      case (Num(thisValue), Num(thatValue)) =>
        thisValue.toInt.compareTo(thatValue.toInt)
      case (Num(_), Alphabet(_)) =>
        -1
      case (Num(_), Hyphen) =>
        -1
      case (Alphabet(_), Num(_)) =>
        1
      case (Alphabet(thisValue), Alphabet(thatValue)) =>
        thisValue.compareTo(thatValue)
      case (Alphabet(_), Hyphen) =>
        1
      case (Hyphen, Num(_)) =>
        1
      case (Hyphen, Alphabet(_)) =>
        -1
      case (Hyphen, Hyphen) =>
        0
    }
}

object AlphaNumHyphen {

  final case class Alphabet(value: String) extends AlphaNumHyphen
  final case class Num(value: String) extends AlphaNumHyphen
  case object Hyphen extends AlphaNumHyphen

  def alphabet(value: String): AlphaNumHyphen =
    Alphabet(value)

  def num(value: Int): AlphaNumHyphen =
    Num(value.toString)

  def numFromStringUnsafe(value: String): AlphaNumHyphen =
    if (value.forall(_.isDigit))
      Num(value)
    else
      sys.error(s"The Num value cannot contain any non-digit. value: $value")

  def hyphen: AlphaNumHyphen =
    Hyphen

  def render(alphaNumHyphen: AlphaNumHyphen): String = alphaNumHyphen match {
    case Num(value) => value
    case Alphabet(value) => value
    case Hyphen => "-"
  }

}

final case class Identifier(values: List[AlphaNumHyphenGroup]) extends AnyVal

object Identifier {

  def compare(a: Identifier, b: Identifier): Int =
    compareElems(a.values, b.values)

  def render(identifier: Identifier): String =
    identifier.values.map(AlphaNumHyphenGroup.render).mkString(".")

}

object AdditionalInfo {

  import AlphaNumHyphen._

  final case class PreRelease(identifier: Identifier)
  final case class BuildMetaInfo(identifier: Identifier)

  def parsePreRelease(value: String): Either[ParseError, Option[PreRelease]] =
    parse(value, {
      case a @ AlphaNumHyphenGroup(Num(n) :: Nil) =>
        if ((n === "0") || n.takeWhile(_ === '0').length === 0)
          Right(a)
        else
          Left(ParseError.leadingZeroNumError(n))
      case a @ AlphaNumHyphenGroup(_) =>
        Right(a)
    }).right.map(_.map(PreRelease))

  def parseBuildMetaInfo(value: String): Either[ParseError, Option[BuildMetaInfo]] =
    parse(value, Right.apply).right.map(_.map(BuildMetaInfo))

  def parse(
    value: String
  , validator: AlphaNumHyphenGroup => Either[ParseError, AlphaNumHyphenGroup]
  ): Either[ParseError, Option[Identifier]] = {
    val alphaNumHyphens: Either[ParseError, List[AlphaNumHyphenGroup]] =
      Option(value)
        .map(_.split("\\."))
        .map(_.map(AlphaNumHyphenGroup.parse)) match {
          case Some(preRelease) =>
            preRelease.foldRight[Either[ParseError, List[AlphaNumHyphenGroup]]](Right(List.empty)){
              (x, acc) =>
                x.right.flatMap(validator) match {
                  case Right(alp) =>
                    acc.right.map(alps => alp :: alps)
                  case Left(error) =>
                    Left(error)
                }
            }
          case None =>
            Right(List.empty)
        }
    alphaNumHyphens.right.map {
      case Nil =>
        None
      case xs =>
        Some(Identifier(xs))
    }
  }
}

final case class Major(major: Int) extends AnyVal
final case class Minor(minor: Int) extends AnyVal
final case class Patch(patch: Int) extends AnyVal

trait SequenceBasedVersion[T] extends Ordered[T] {
  def major: Major
  def minor: Minor

  def render: String
}

final case class SemanticVersion(
  major: Major
, minor: Minor
, patch: Patch
, pre: Option[PreRelease]
, buildMetadata: Option[BuildMetaInfo]
) extends SequenceBasedVersion[SemanticVersion] {

  override def compare(that: SemanticVersion): Int = {
    val mj = this.major.major.compareTo(that.major.major)
    if (mj === 0) {
      val mn = this.minor.minor.compareTo(that.minor.minor)
      if (mn === 0) {
        val pt = this.patch.patch.compareTo(that.patch.patch)
        if (pt === 0) {
          (this.pre, that.pre) match {
            case (Some(thisPre), Some(thatPre)) =>
              Identifier.compare(thisPre.identifier, thatPre.identifier)
            case (Some(_), None) =>
              -1
            case (None, Some(_)) =>
              1
            case (None, None) =>
              0
          }
        } else {
          pt
        }
      } else {
        mn
      }
    } else {
      mj
    }
  }

  def render: String =
    s"${major.major}.${minor.minor}.${patch.patch}" + ((pre, buildMetadata) match {
        case (Some(p), Some(m)) =>
          s"-${Identifier.render(p.identifier)}+${Identifier.render(m.identifier)}"
        case (Some(p), None) =>
          s"-${Identifier.render(p.identifier)}"
        case (None, Some(m)) =>
          s"+${Identifier.render(m.identifier)}"
        case (None, None) =>
          ""
      }).toString
}

object SemanticVersion {
  val major0: Major = Major(0)
  val minor0: Minor = Minor(0)
  val patch0: Patch = Patch(0)

  val semanticVersionRegex: Regex =
    """(\d+)\.(\d+)\.(\d+)(?:-([a-zA-Z\d-\.]+)?)?(?:\+([a-zA-Z\d-\.]+)?)?""".r

  def parse(version: String): Either[ParseError, SemanticVersion] = version match {
    case semanticVersionRegex(major, minor, patch, pre, meta) =>
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
            SemanticVersion(
              Major(major.toInt), Minor(minor.toInt), Patch(patch.toInt),
              preR, metaI
            )
          )
      }

    case _ =>
      Left(ParseError.invalidVersionStringError(version))
  }

  def noIdentifier(major: Major, minor: Minor, patch: Patch): SemanticVersion =
    SemanticVersion(major, minor, patch, None, None)

  def withMajor(major: Major): SemanticVersion =
    SemanticVersion(major, minor0, patch0, None, None)

  def withMinor(minor: Minor): SemanticVersion =
    SemanticVersion(major0, minor, patch0, None, None)

  def withPatch(patch: Patch): SemanticVersion =
    SemanticVersion(major0, minor0, patch, None, None)
}

sealed trait ParseError

object ParseError {

  final case class InvalidAlphaNumHyphenError(c: Char, rest: List[Char]) extends ParseError
  case object EmptyAlphaNumHyphenError extends ParseError

  final case class LeadingZeroNumError(n: String) extends ParseError

  final case class PreReleaseParseError(parseError: ParseError) extends ParseError
  final case class BuildMetadataParseError(parseError: ParseError) extends ParseError

  final case class CombinedParseError(preReleaseError: ParseError, buildMetadataError: ParseError) extends ParseError

  final case class InvalidVersionStringError(value: String) extends ParseError

  @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
  def render(parseError: ParseError): String = parseError match {
    case InvalidAlphaNumHyphenError(c, rest) =>
      s"Invalid char for AlphaNumHyphen found. value: $c / rest: $rest"

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
      s"Invalid SemanticVersion String. value: $value"
  }

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
      preReleaseParseError(preReleaseError)
    , buildMetadataParseError(buildMetadataError)
    )

  def invalidVersionStringError(value: String): ParseError =
    InvalidVersionStringError(value)

}
