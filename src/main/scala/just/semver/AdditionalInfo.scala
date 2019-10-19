package just.semver

import just.Common.compareElems

import just.fp.compat.EitherCompat
import just.fp.syntax._

/**
 * @author Kevin Lee
 * @since 2018-10-21
 */
object AdditionalInfo {

  import AlphaNumHyphen._

  final case class Identifier(values: List[AlphaNumHyphenGroup]) extends AnyVal

  object Identifier {

    def compare(a: Identifier, b: Identifier): Int =
      compareElems(a.values, b.values)

    def render(identifier: Identifier): String =
      identifier.values.map(AlphaNumHyphenGroup.render).mkString(".")

  }

  final case class PreRelease(identifier: Identifier)
  final case class BuildMetaInfo(identifier: Identifier)

  def parsePreRelease(value: String): Either[ParseError, Option[PreRelease]] =
    EitherCompat.map(parse(value, {
      case a @ AlphaNumHyphenGroup(Num(n) :: Nil) =>
        if ((n === "0") || n.takeWhile(_ === '0').length === 0)
          Right(a)
        else
          Left(ParseError.leadingZeroNumError(n))
      case a @ AlphaNumHyphenGroup(_) =>
        Right(a)
    }))(_.map(PreRelease))

  def parseBuildMetaInfo(value: String): Either[ParseError, Option[BuildMetaInfo]] =
    EitherCompat.map(parse(value, Right.apply))(_.map(BuildMetaInfo))

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
              EitherCompat.flatMap(x)(validator) match {
                case Right(alp) =>
                  EitherCompat.map(acc)(alps => alp :: alps)
                case Left(error) =>
                  Left(error)
              }
          }
        case None =>
          Right(List.empty)
      }
    EitherCompat.map(alphaNumHyphens) {
      case Nil =>
        None
      case xs =>
        Some(Identifier(xs))
    }
  }
}