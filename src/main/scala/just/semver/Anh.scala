package just.semver

/**
 * @author Kevin Lee
 * @since 2018-10-21
 */
sealed trait Anh extends Ordered[Anh] {

  import Anh._

  override def compare(that: Anh): Int =
    (this, that) match {
      case (Num(thisValue), Num(thatValue)) =>
        Ordering[Int].compare(thisValue.toInt, thatValue.toInt)
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

object Anh {

  final case class Alphabet(value: String) extends Anh
  final case class Num(value: String) extends Anh
  case object Hyphen extends Anh

  def alphabet(value: String): Anh =
    Alphabet(value)

  def num(value: Int): Anh =
    Num(value.toString)

  def numFromStringUnsafe(value: String): Anh =
    if (value.forall(_.isDigit))
      Num(value)
    else
      sys.error(s"The Num value cannot contain any non-digit. value: $value")

  def hyphen: Anh =
    Hyphen

  def render(alphaNumHyphen: Anh): String = alphaNumHyphen match {
    case Num(value) => value
    case Alphabet(value) => value
    case Hyphen => "-"
  }

}