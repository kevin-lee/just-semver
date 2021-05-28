package just.semver

import just.Common._

import scala.annotation.tailrec

/** Dot separated value
  * @author
  *   Kevin Lee
  * @since
  *   2018-10-21
  */
final case class Dsv(values: List[Anh]) extends Ordered[Dsv] {
  override def compare(that: Dsv): Int =
    compareElems(this.values, that.values)
}

object Dsv extends Compat {

  import Anh._

  implicit final class DsvOps(val dsv: Dsv) extends AnyVal {
    @inline def render: String = Dsv.render(dsv)
  }

  def render(alphaNumHyphenGroup: Dsv): String =
    alphaNumHyphenGroup.values.map(Anh.render).mkString

  def parse(value: String): Either[ParseError, Dsv] = {

    @tailrec
    def accumulate(cs: List[Char], chars: Anh, acc: Vector[Anh]): Either[ParseError, Vector[Anh]] =
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

        result.map(groups => Dsv(groups.toList))

      case Nil =>
        Left(ParseError.emptyAlphaNumHyphenError)
    }

  }
}
