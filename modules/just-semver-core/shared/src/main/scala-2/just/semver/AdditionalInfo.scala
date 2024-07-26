package just.semver

import just.Common._

/** @author Kevin Lee
  * @since 2018-10-21
  */
object AdditionalInfo extends Compat {

  import Anh._

  final case class PreRelease(identifier: List[Dsv])
  object PreRelease {
    implicit final class PreReleaseOps(private val preRelease: PreRelease) extends AnyVal {
      @inline def render: String = PreRelease.render(preRelease)
    }
    def render(preRelease: PreRelease): String =
      preRelease.identifier.map(Dsv.render).mkString(".")
  }

  final case class BuildMetaInfo(identifier: List[Dsv])
  object BuildMetaInfo {
    implicit final class BuildMetaInfoOps(private val buildMetaInfo: BuildMetaInfo) extends AnyVal {
      @inline def render: String = BuildMetaInfo.render(buildMetaInfo)
    }
    def render(buildMetaInfo: BuildMetaInfo): String =
      buildMetaInfo.identifier.map(Dsv.render).mkString(".")
  }

  def parsePreRelease(value: String): Either[AdditionalInfoParseError, Option[PreRelease]] =
    parse(
      value,
      {
        case a @ Dsv(Num(n) :: Nil) =>
          if ((n === "0") || n.takeWhile(_ === '0').length === 0)
            Right(a)
          else
            Left(AdditionalInfoParseError.leadingZeroNumError(n))
        case a @ Dsv(_) =>
          Right(a)
      }
    ).map(_.map(PreRelease.apply))

  def parseBuildMetaInfo(value: String): Either[AdditionalInfoParseError, Option[BuildMetaInfo]] =
    parse(value, Right.apply).map(_.map(BuildMetaInfo.apply))

  def parse(
    value: String,
    validator: Dsv => Either[AdditionalInfoParseError, Dsv]
  ): Either[AdditionalInfoParseError, Option[List[Dsv]]] = {
    val alphaNumHyphens: Either[AdditionalInfoParseError, List[Dsv]] =
      Option(value)
        .map(_.split("\\."))
        .map(_.map(Dsv.parse)) match {
        case Some(preRelease) =>
          preRelease.foldRight(List.empty[Dsv].asRight[AdditionalInfoParseError]) { (x, acc) =>
            x.left
              .map {
                case Dsv.DsvParseError.InvalidAlphaNumHyphenError(c, rest) =>
                  AdditionalInfoParseError.invalidAlphaNumHyphenError(c, rest)
                case Dsv.DsvParseError.EmptyAlphaNumHyphenError =>
                  AdditionalInfoParseError.emptyAlphaNumHyphenError
              }
              .flatMap(validator) match {
              case Right(alp) =>
                acc.map(alps => alp :: alps)
              case Left(error) =>
                error.asLeft[List[Dsv]]
            }
          }
        case None =>
          List.empty[Dsv].asRight[AdditionalInfoParseError]
      }
    alphaNumHyphens.map {
      case Nil =>
        none[List[Dsv]]
      case xs =>
        xs.some
    }
  }

  sealed trait AdditionalInfoParseError
  object AdditionalInfoParseError {
    final case class LeadingZeroNumError(n: String) extends AdditionalInfoParseError

    final case class InvalidAlphaNumHyphenError(c: Char, rest: List[Char]) extends AdditionalInfoParseError
    case object EmptyAlphaNumHyphenError extends AdditionalInfoParseError

    def leadingZeroNumError(n: String): AdditionalInfoParseError = LeadingZeroNumError(n)

    def invalidAlphaNumHyphenError(c: Char, rest: List[Char]): AdditionalInfoParseError =
      InvalidAlphaNumHyphenError(c, rest)

    def emptyAlphaNumHyphenError: AdditionalInfoParseError = EmptyAlphaNumHyphenError
  }

}
