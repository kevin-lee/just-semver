package just.semver

/**
 * @author Kevin Lee
 * @since 2018-10-21
 */
sealed trait AlphaNumHyphen extends Ordered[AlphaNumHyphen] {

  import AlphaNumHyphen._

  override def compare(that: AlphaNumHyphen): Int =
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