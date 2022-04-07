package just.semver

import just.Common._

/** @author Kevin Lee
  * @since 2018-10-21
  */
object AdditionalInfo extends Compat {

  import Anh._

  final case class PreRelease(identifier: List[Dsv]) derives CanEqual
  object PreRelease {
    extension (preRelease: PreRelease) {
      def render: String =
        preRelease.identifier.map(_.render).mkString(".")
    }
  }

  final case class BuildMetaInfo(identifier: List[Dsv]) derives CanEqual
  object BuildMetaInfo {
    extension (buildMetaInfo: BuildMetaInfo) {
      def render: String =
        buildMetaInfo.identifier.map(_.render).mkString(".")
    }
  }

  def parsePreRelease(value: String): Either[ParseError, Option[PreRelease]] =
    parse(
      value,
      {
        case a @ Dsv(Num(n) :: Nil) =>
          if ((n == "0") || n.takeWhile(_ == '0').length == 0)
            Right(a)
          else
            Left(ParseError.leadingZeroNumError(n))
        case a @ Dsv(_) =>
          Right(a)
      }
    ).map(_.map(PreRelease.apply))

  def parseBuildMetaInfo(value: String): Either[ParseError, Option[BuildMetaInfo]] =
    parse(value, Right.apply).map(_.map(BuildMetaInfo.apply))

  def parse(
    value: String,
    validator: Dsv => Either[ParseError, Dsv]
  ): Either[ParseError, Option[List[Dsv]]] = {
    val alphaNumHyphens: Either[ParseError, List[Dsv]] =
      Option(value)
        .flatMap { s =>
          Option(s.split("\\.")).map { array =>
            array.nn.toList.collect {
              case s: String =>
                s
            }
          }
        }
        .map(_.map(Dsv.parse)) match {
        case Some(preRelease) =>
          preRelease.foldRight(List.empty[Dsv].asRight[ParseError]) { (x, acc) =>
            x.flatMap(validator) match {
              case Right(alp) =>
                acc.map(alps => alp :: alps)
              case Left(error) =>
                error.asLeft[List[Dsv]]
            }
          }
        case None =>
          List.empty[Dsv].asRight[ParseError]
      }
    alphaNumHyphens.map {
      case Nil =>
        none[List[Dsv]]
      case xs =>
        xs.some
    }
  }
}
