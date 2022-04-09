package just.semver.matcher

import just.Common._
import just.semver.{Compat, SemVer}

/** @author Kevin Lee
  * @since 2022-04-07
  */
final case class SemVerMatchers(matchers: SemVerMatchers.Or) derives CanEqual
object SemVerMatchers extends Compat {

  @SuppressWarnings(Array("org.wartremover.warts.Recursion", "org.wartremover.warts.ListAppend"))
  def parse(selector: String): Either[List[SemVerMatcher.ParseError], SemVerMatchers] = {
    @SuppressWarnings(Array("org.wartremover.warts.JavaSerializable", "org.wartremover.warts.Serializable"))
    def each(s: String): Either[List[SemVerMatcher.ParseError], List[SemVerMatcher]] = {
      val spaced      = s.split("\\s+")
      val hyphenIndex = spaced.indexWhere(_ === "-")
      if (hyphenIndex >= 0) {
        val (rangeBefore, rangeAfter) = spaced.splitAt(hyphenIndex)
        (
          rangeBefore
            .lastOption
            .toRight("Range start is missing")
            .flatMap(v => SemVer.parse(v.trim).left.map(_.render)),
          rangeAfter
            .drop(1)
            .headOption
            .toRight("Range end is missing")
            .flatMap(v => SemVer.parse(v.trim).left.map(_.render))
        ) match {
          case (Right(from), Right(to)) =>
            val range             = SemVerMatcher.range(from, to)
            val before            = rangeBefore
              .dropRight(1)
              .map(SemVerComparison.parse)
              .map(
                _.map(SemVerMatcher.comparison)
                  .left
                  .map(SemVerMatcher.ParseError.semVerComparisonParseFailure)
              )
            val after             = rangeAfter.drop(2)
            val (failed, success) =
              (before :+ Right(range))
                .foldLeft((List.empty[SemVerMatcher.ParseError], List.empty[SemVerMatcher])) {
                  case ((failed, success), Left(err)) =>
                    (failed :+ err, success)
                  case ((failed, success), Right(parsed)) =>
                    (failed, success :+ parsed)
                }

            if (failed.isEmpty) {
              if (after.isEmpty) {
                Right(success)
              } else {
                each(after.mkString(" ")) match {
                  case Right(rest) =>
                    Right(success ++ rest)
                  case Left(err) =>
                    Left(failed ++ err)
                }
              }
            } else {
              if (after.isEmpty) {
                Left(failed)
              } else {
                each(after.mkString(" ")) match {
                  case Right(_) =>
                    Left(failed)
                  case Left(err) =>
                    Left(failed ++ err)
                }
              }

            }
          case (Right(before), Left(err)) =>
            Left(
              List(
                SemVerMatcher.ParseError.rangeParseFailure("Parsing 'to' in range failed: ", List(err), Some(before))
              )
            )
          case (Left(err), Right(end)) =>
            Left(
              List(SemVerMatcher.ParseError.rangeParseFailure("Parsing 'from' in range failed: ", List(err), Some(end)))
            )
          case (Left(err1), Left(err2)) =>
            Left(
              List(
                SemVerMatcher
                  .ParseError
                  .rangeParseFailure("Parsing both 'from' and 'to' in range failed: ", List(err1, err2), None)
              )
            )
        }
      } else {
        // no range
        val (failed, success) =
          spaced
            .map(
              SemVerComparison
                .parse(_)
                .map(SemVerMatcher.comparison)
                .left
                .map(SemVerMatcher.ParseError.semVerComparisonParseFailure)
            )
            .foldLeft((List.empty[SemVerMatcher.ParseError], List.empty[SemVerMatcher])) {
              case ((failed, success), Left(err)) =>
                (failed :+ err, success)
              case ((failed, success), Right(parsed)) =>
                (failed, success :+ parsed)
            }
        if (failed.isEmpty) {
          Right(success)
        } else {
          Left(failed)
        }

      }

    }
    val or                = """[\s]+\|\|[\s]+"""
    val ors               = selector.split(or).map(_.trim)
    val (failed, success) = ors
      .map(each)
      .foldLeft((List.empty[SemVerMatcher.ParseError], List.empty[And])) {
        case ((failed, success), Right(ands)) =>
          (failed, success :+ And(ands))
        case ((failed, success), Left(errs)) =>
          (failed ++ errs, success)
      }
    if (failed.isEmpty) {
      Right(SemVerMatchers(Or(success)))
    } else {
      Left(failed)
    }
  }

  def unsafeParse(matchers: String): SemVerMatchers =
    parse(matchers).fold(errs => sys.error(errs.map(_.render).mkString(", ")), identity)

  type Or = Or.Or
  object Or {
    opaque type Or = List[And]
    def apply(or: List[And]): Or = or

    def unapply(or: Or): Some[List[And]] = Some(or.value)

    given orCanEqual: CanEqual[Or, Or] = CanEqual.derived

    extension (or: Or) {
      def value: List[And] = or
    }
  }

  type And = And.And
  object And {
    opaque type And = List[SemVerMatcher]
    def apply(and: List[SemVerMatcher]): And = and

    def unapply(and: And): Some[List[SemVerMatcher]] = Some(and.value)

    given andCanEqual: CanEqual[And, And] = CanEqual.derived

    extension (and: And) {
      def value: List[SemVerMatcher] = and
    }
  }

  extension (semVerMatchers: SemVerMatchers) {

    def matches(semVer: SemVer): Boolean = semVerMatchers match {
      case SemVerMatchers(SemVerMatchers.Or(ors)) =>
        ors
          .find {
            case SemVerMatchers.And(ands) =>
              ands.forall(_.matches(semVer))
          }
          .fold(false)(_ => true)
    }

    def render: String = semVerMatchers match {
      case SemVerMatchers(Or(ands)) =>
        ands
          .map {
            case And(matcher) =>
              matcher.map(_.render).mkString(" ")
          }
          .mkString(" || ")
    }

  }

}
