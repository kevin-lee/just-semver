package just.semver

import just.fp.compat.EitherCompat
import just.fp.syntax._

/**
 * @author Kevin Lee
 * @since 2018-10-21
 */
object AdditionalInfo {

  import AlphaNumHyphen._

  final case class PreRelease(identifier: List[AlphaNumHyphenGroup])
  object PreRelease {
    def render(preRelease: PreRelease): String =
      preRelease.identifier.map(AlphaNumHyphenGroup.render).mkString(".")
  }

  final case class BuildMetaInfo(identifier: List[AlphaNumHyphenGroup])
  object BuildMetaInfo {
    def render(buildMetaInfo: BuildMetaInfo): String =
      buildMetaInfo.identifier.map(AlphaNumHyphenGroup.render).mkString(".")
  }

  def parsePreRelease(value: String): Either[ParseError, Option[PreRelease]] =
    EitherCompat.map(parse(value, {
      case a @ AlphaNumHyphenGroup(Num(n) :: Nil) =>
        if ((n === "0") || n.takeWhile(_ === '0').length === 0)
          Right(a)
        else
          Left(ParseError.leadingZeroNumError(n))
      case a @ AlphaNumHyphenGroup(_) =>
        Right(a)
    }))(_.map(PreRelease.apply))

  def parseBuildMetaInfo(value: String): Either[ParseError, Option[BuildMetaInfo]] =
    EitherCompat.map(parse(value, Right.apply))(_.map(BuildMetaInfo.apply))

  def parse(
      value: String
    , validator: AlphaNumHyphenGroup => Either[ParseError, AlphaNumHyphenGroup]
    ): Either[ParseError, Option[List[AlphaNumHyphenGroup]]] = {
    val alphaNumHyphens: Either[ParseError, List[AlphaNumHyphenGroup]] =
      Option(value)
        .map(_.split("\\."))
        .map(_.map(AlphaNumHyphenGroup.parse)) match {
        case Some(preRelease) =>
          preRelease.foldRight[Either[ParseError, List[AlphaNumHyphenGroup]]](List.empty.right) {
            (x, acc) =>
              EitherCompat.flatMap(x)(validator) match {
                case Right(alp) =>
                  EitherCompat.map(acc)(alps => alp :: alps)
                case Left(error) =>
                  error.left
              }
          }
        case None =>
          List.empty.right
      }
    EitherCompat.map(alphaNumHyphens) {
      case Nil =>
        none
      case xs =>
        xs.some
    }
  }
}