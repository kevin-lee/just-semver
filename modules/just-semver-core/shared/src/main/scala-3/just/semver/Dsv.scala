package just.semver

import just.Common.*
import scala.annotation.tailrec

/** Dot separated value
  * @author Kevin Lee
  * @since 2018-10-21
  */
final case class Dsv(values: List[Anh]) extends Ordered[Dsv] derives CanEqual {
  override def compare(that: Dsv): Int =
    this.values.compareElems(that.values)
}

object Dsv extends Compat {

  import Anh._

  extension (dsv: Dsv) {
    def render: String = dsv.values.map(_.render).mkString
  }

  def parse(value: String): Either[DsvParseError, Dsv] = {

    @tailrec
    def accumulate(cs: List[Char], chars: Anh, acc: Vector[Anh]): Either[DsvParseError, Vector[Anh]] =
      cs match {
        case x :: xs =>
          if (x >= '0' && x <= '9') {
            chars match {
              case Num(ns) =>
                accumulate(xs, Num(ns :+ x), acc)

              case _ =>
                accumulate(xs, Num(x.toString), acc :+ chars)
            }
          } else if (x === '-') {
            accumulate(xs, Hyphen, acc :+ chars)
          } else if ((x >= 'A' && x <= 'Z') || (x >= 'a' && x <= 'z')) {
            chars match {
              case Alphabet(as) =>
                accumulate(xs, Alphabet(as :+ x), acc)

              case _ =>
                accumulate(xs, Alphabet(x.toString), acc :+ chars)
            }
          } else {
            Left(
              DsvParseError.invalidAlphaNumHyphenError(x, xs)
            )
          }

        case Nil =>
          Right(acc :+ chars)
      }

    value.toList match {
      case x :: xs =>
        val result =
          if (x >= '0' && x <= '9') {
            accumulate(xs, Num(x.toString), Vector.empty)
          } else if (x === '-')
            accumulate(xs, Hyphen, Vector.empty)
          else if ((x >= 'A' && x <= 'Z') || (x >= 'a' && x <= 'z'))
            accumulate(xs, Alphabet(x.toString), Vector.empty)
          else
            Left(
              DsvParseError.invalidAlphaNumHyphenError(x, xs)
            )

        result.map(groups => Dsv(groups.toList))

      case Nil =>
        Left(DsvParseError.emptyAlphaNumHyphenError)
    }

  }

  enum DsvParseError {
    case InvalidAlphaNumHyphenError(c: Char, rest: List[Char])
    case EmptyAlphaNumHyphenError
  }
  object DsvParseError {
    def invalidAlphaNumHyphenError(c: Char, rest: List[Char]): DsvParseError =
      DsvParseError.InvalidAlphaNumHyphenError(c, rest)

    def emptyAlphaNumHyphenError: DsvParseError = DsvParseError.EmptyAlphaNumHyphenError

  }
}
