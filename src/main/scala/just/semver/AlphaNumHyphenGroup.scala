package just.semver

import just.Common.compareElems

import just.fp.compat.EitherCompat
import just.fp.syntax._

import scala.annotation.tailrec

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

        EitherCompat.map(result)(groups => AlphaNumHyphenGroup(groups.toList))

      case Nil =>
        Left(ParseError.emptyAlphaNumHyphenError)
    }

  }
}