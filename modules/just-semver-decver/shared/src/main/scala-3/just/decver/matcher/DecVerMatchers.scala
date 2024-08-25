package just.decver.matcher

import just.Common._
import just.decver.DecVer
import just.decver.DecVer.{ParseError => DecVerParseError}
import just.semver.Compat

/** @author Kevin Lee
  * @since 2022-04-07
  */
final case class DecVerMatchers(matchers: DecVerMatchers.Or)
object DecVerMatchers extends Compat {

  @SuppressWarnings(Array("org.wartremover.warts.Recursion", "org.wartremover.warts.ListAppend"))
  def parse(selector: String): Either[DecVerMatchers.ParseErrors, DecVerMatchers] = {
    @SuppressWarnings(Array("org.wartremover.warts.JavaSerializable", "org.wartremover.warts.Serializable"))
    def each(
      s: String
    ): Either[(DecVerMatcher.ParseError, List[DecVerMatcher.ParseError]), List[DecVerMatcher]] = {
      val spaced      = s.split("\\s+")
      val hyphenIndex = spaced.indexWhere(_ === "-")
      if (hyphenIndex >= 0) {
        val (rangeBefore, rangeAfter) = spaced.splitAt(hyphenIndex)
        (
          rangeBefore
            .lastOption
            .toRight("Range start is missing")
            .flatMap(v => DecVer.parse(v.trim).left.map(_.render)),
          rangeAfter
            .drop(1)
            .headOption
            .toRight("Range end is missing")
            .flatMap(v => DecVer.parse(v.trim).left.map(_.render))
        ) match {
          case (Right(from), Right(to)) =>
            val range             = DecVerMatcher.range(from, to)
            val before            = rangeBefore
              .dropRight(1)
              .map(DecVerComparison.parse)
              .map(
                _.map(DecVerMatcher.comparison)
                  .left
                  .map(DecVerMatcher.ParseError.decVerComparisonParseFailure)
              )
            val after             = rangeAfter.drop(2)
            val (failed, success) =
              (before :+ Right(range))
                .foldLeft((List.empty[DecVerMatcher.ParseError], List.empty[DecVerMatcher])) {
                  case ((failed, success), Left(err)) =>
                    (failed :+ err, success)
                  case ((failed, success), Right(parsed)) =>
                    (failed, success :+ parsed)
                }

            failed match {
              case Nil =>
                if (after.isEmpty) {
                  Right(success)
                } else {
                  each(after.mkString(" "))
                    .map(rest => success ++ rest)
                }

              case failed :: failedMore =>
                if (after.isEmpty) {
                  Left((failed, failedMore))
                } else {
                  each(after.mkString(" ")) match {
                    case Right(_) =>
                      Left((failed, failedMore))
                    case Left((err, errs)) =>
                      Left((failed, failedMore ++ (err :: errs)))
                  }
                }

            }

          case (Right(before), Left(err)) =>
            Left(
              (
                DecVerMatcher
                  .ParseError
                  .rangeParseFailure("Parsing 'to' in range failed: ", List(err), Some(before)),
                Nil
              )
            )
          case (Left(err), Right(end)) =>
            Left(
              (
                DecVerMatcher.ParseError.rangeParseFailure("Parsing 'from' in range failed: ", List(err), Some(end)),
                Nil
              )
            )
          case (Left(err1), Left(err2)) =>
            Left(
              (
                DecVerMatcher
                  .ParseError
                  .rangeParseFailure("Parsing both 'from' and 'to' in range failed: ", List(err1, err2), None),
                Nil
              )
            )
        }
      } else {
        // no range
        val (failed, success) =
          spaced
            .map(
              DecVerComparison
                .parse(_)
                .map(DecVerMatcher.comparison)
                .left
                .map(DecVerMatcher.ParseError.decVerComparisonParseFailure)
            )
            .foldLeft((List.empty[DecVerMatcher.ParseError], List.empty[DecVerMatcher])) {
              case ((failed, success), Left(err)) =>
                (failed :+ err, success)
              case ((failed, success), Right(parsed)) =>
                (failed, success :+ parsed)
            }

        failed match {
          case Nil =>
            Right(success)
          case err :: errs =>
            Left((err, errs))
        }
      }

    }
    val or  = """[\s]+\|\|[\s]+"""
    val ors = selector.split(or).map(_.trim)

    val (failed, success) = ors
      .map(each)
      .foldLeft((List.empty[DecVerMatcher.ParseError], List.empty[And])) {
        case ((failed, success), Right(ands)) =>
          (failed, success :+ And(ands))
        case ((failed, success), Left((err, errs))) =>
          (failed ++ (err :: errs), success)
      }
    failed match {
      case Nil =>
        Right(DecVerMatchers(Or(success)))
      case err :: errs =>
        Left(ParseErrors(err, errs))
    }
  }

  def unsafeParse(matchers: String): DecVerMatchers =
    parse(matchers).fold(errs => sys.error(errs.render), identity)

  final case class Or(value: List[And]) extends AnyVal
  final case class And(value: List[DecVerMatcher]) extends AnyVal

  extension (decVerMatchers: DecVerMatchers) {

    def matches(decVer: DecVer): Boolean = decVerMatchers match {
      case DecVerMatchers(DecVerMatchers.Or(ors)) =>
        ors
          .find {
            case DecVerMatchers.And(ands) =>
              ands.forall(_.matches(decVer))
          }
          .fold(false)(_ => true)
    }

    def render: String = decVerMatchers match {
      case DecVerMatchers(Or(ands)) =>
        ands
          .map {
            case And(matcher) =>
              matcher.map(_.render).mkString(" ")
          }
          .mkString(" || ")
    }

  }

  final case class ParseErrors(
    private val error: DecVerMatcher.ParseError,
    private val errors: List[DecVerMatcher.ParseError]
  )
  object ParseErrors {

    extension (parseErrors: ParseErrors) {

      /** Returns List[DecVerMatcher.ParseError]. The List returned is guaranteed non-empty.
        */
      def allErrors: List[DecVerMatcher.ParseError] = parseErrors.error :: parseErrors.errors

      def render: String = allErrors.map(_.render).mkString("[", ", ", "]")
    }

    extension (eth: Either[ParseErrors, DecVerMatchers])  {
      def toDecVerParseError: Either[DecVerParseError, DecVerMatchers] =
        eth.left.map(DecVerParseError.decVerMatchersParseErrors)
    }
  }
}
