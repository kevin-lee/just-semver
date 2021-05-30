package just.semver

/** Alphabet / Number / Hyphen (Anh)
  *
  * @author
  *   Kevin Lee
  * @since
  *   2018-10-21
  */
enum Anh extends Ordered[Anh] derives CanEqual {

  case Alphabet(value: String)
  case Num(value: String)
  case Hyphen

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

  def alphabet(value: String): Anh =
    Anh.Alphabet(value)

  def num(value: Int): Anh =
    Anh.Num(value.toString)

  def numFromStringUnsafe(value: String): Anh =
    if (value.forall(_.isDigit))
      Anh.Num(value)
    else
      sys.error(s"The Num value cannot contain any non-digit. value: $value")

  def hyphen: Anh =
    Anh.Hyphen

  extension (anh: Anh) {
    def render: String = anh match {
      case Num(value)      =>
        value
      case Alphabet(value) =>
        value
      case Hyphen          =>
        "-"
    }
  }

}
