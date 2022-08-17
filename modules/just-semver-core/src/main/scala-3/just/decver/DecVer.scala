package just.decver

import just.semver.SemVer

import scala.util.matching.Regex

/** @author Kevin Lee
  * @since 2022-06-10
  */
final case class DecVer(major: DecVer.Major, minor: DecVer.Minor) extends Ordered[DecVer] derives CanEqual {
  override def compare(that: DecVer): Int = DecVer.decVerOrdering.compare(this, that)
}

object DecVer {
  given decVerOrdering: Ordering[DecVer] = {
    case (v1: DecVer, v2: DecVer) =>
      (
        v1.major.value.compareTo(v2.major.value),
        v1.minor.value.compareTo(v2.minor.value),
      ) match {
        case (0, 0) => 0
        case (0, minor) => minor
        case (major, _) => major
      }

  }

  extension (decVer: DecVer) {
    def increaseMajor: DecVer = DecVer(DecVer.Major(decVer.major.value + 1), decVer.minor)
    def increaseMinor: DecVer = DecVer(decVer.major, DecVer.Minor(decVer.minor.value + 1))

    def render: String = s"${decVer.major.value.toString}.${decVer.minor.value.toString}"

    def toSemVer: SemVer = SemVer.fromDecVer(decVer)
  }

  val DecimalVersionPattern: Regex = """(\d+)\.(\d+)""".r

  def parse(version: String): Either[ParseError, DecVer] = Option(version)
    .toRight(ParseError.nullValue)
    .filterOrElse(_.nonEmpty, ParseError.empty)
    .flatMap {
      case DecimalVersionPattern(major, minor) =>
        Right(DecVer(DecVer.Major(major.toInt), DecVer.Minor(minor.toInt)))
      case _ =>
        Left(ParseError.Invalid(version))
    }

  @SuppressWarnings(Array("org.wartremover.warts.Throw"))
  def unsafeParse(version: String): DecVer =
    parse(version) match {
      case Right(ver) => ver
      case Left(err) => throw err // scalafix:ok DisableSyntax.throw
    }

  def fromSemVer(semVer: SemVer): DecVer = DecVer(DecVer.Major(semVer.major.value), DecVer.Minor(semVer.minor.value))

  type Major = Major.Major
  object Major {
    opaque type Major = Int
    def apply(major: Int): Major = major

    given majorCanEqual: CanEqual[Major, Major] = CanEqual.derived

    extension (major: Major) {
      def value: Int = major
    }
  }

  type Minor = Minor.Minor
  object Minor {
    opaque type Minor = Int
    def apply(minor: Int): Minor = minor

    given minorCanEqual: CanEqual[Minor, Minor] = CanEqual.derived

    extension (minor: Minor) {
      def value: Int = minor
    }
  }

  enum ParseError extends RuntimeException derives CanEqual {
    case NullValue
    case Empty
    case Invalid(version: String)

    override def getMessage: String = this.render
  }
  object ParseError {

    def nullValue: ParseError                = NullValue
    def empty: ParseError                    = Empty
    def invalid(version: String): ParseError = Invalid(version)

    extension (decVerError: ParseError) {
      def render: String = decVerError match {
        case ParseError.NullValue => "Version String is null"
        case ParseError.Empty => "Version String is an empty String"
        case ParseError.Invalid(version) => s"Invalue version: $version"
      }
    }

  }
}
