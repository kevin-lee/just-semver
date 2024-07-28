package just.decver.matcher

import just.Common._
import just.decver.DecVerExt
import just.decver.DecVerExt.{ParseError => DecVerExtParseError}
import just.semver.Compat

/** @author Kevin Lee
  * @since 2022-04-07
  */
final case class DecVerExtMatchers(matchers: DecVerExtMatchers.Or)
object DecVerExtMatchers extends Compat {

  @SuppressWarnings(Array("org.wartremover.warts.Recursion", "org.wartremover.warts.ListAppend"))
  def parse(selector: String): Either[DecVerExtMatchers.ParseErrors, DecVerExtMatchers] = {
    @SuppressWarnings(Array("org.wartremover.warts.JavaSerializable", "org.wartremover.warts.Serializable"))
    def each(
      s: String
    ): Either[(DecVerExtMatcher.ParseError, List[DecVerExtMatcher.ParseError]), List[DecVerExtMatcher]] = {
      val spaced      = s.split("\\s+")
      val hyphenIndex = spaced.indexWhere(_ === "-")
      if (hyphenIndex >= 0) {
        val (rangeBefore, rangeAfter) = spaced.splitAt(hyphenIndex)
        (
          rangeBefore
            .lastOption
            .toRight("Range start is missing")
            .flatMap(v => DecVerExt.parse(v.trim).left.map(_.render)),
          rangeAfter
            .drop(1)
            .headOption
            .toRight("Range end is missing")
            .flatMap(v => DecVerExt.parse(v.trim).left.map(_.render))
        ) match {
          case (Right(from), Right(to)) =>
            val range             = DecVerExtMatcher.range(from, to)
            val before            = rangeBefore
              .dropRight(1)
              .map(DecVerExtComparison.parse)
              .map(
                _.map(DecVerExtMatcher.comparison)
                  .left
                  .map(DecVerExtMatcher.ParseError.decVerExtComparisonParseFailure)
              )
            val after             = rangeAfter.drop(2)
            val (failed, success) =
              (before :+ Right(range))
                .foldLeft((List.empty[DecVerExtMatcher.ParseError], List.empty[DecVerExtMatcher])) {
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
                DecVerExtMatcher
                  .ParseError
                  .rangeParseFailure("Parsing 'to' in range failed: ", List(err), Some(before)),
                Nil
              )
            )
          case (Left(err), Right(end)) =>
            Left(
              (
                DecVerExtMatcher.ParseError.rangeParseFailure("Parsing 'from' in range failed: ", List(err), Some(end)),
                Nil
              )
            )
          case (Left(err1), Left(err2)) =>
            Left(
              (
                DecVerExtMatcher
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
              DecVerExtComparison
                .parse(_)
                .map(DecVerExtMatcher.comparison)
                .left
                .map(DecVerExtMatcher.ParseError.decVerExtComparisonParseFailure)
            )
            .foldLeft((List.empty[DecVerExtMatcher.ParseError], List.empty[DecVerExtMatcher])) {
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
      .foldLeft((List.empty[DecVerExtMatcher.ParseError], List.empty[And])) {
        case ((failed, success), Right(ands)) =>
          (failed, success :+ And(ands))
        case ((failed, success), Left((err, errs))) =>
          (failed ++ (err :: errs), success)
      }
    failed match {
      case Nil =>
        Right(DecVerExtMatchers(Or(success)))
      case err :: errs =>
        Left(ParseErrors(err, errs))
    }
  }

  def unsafeParse(matchers: String): DecVerExtMatchers =
    parse(matchers).fold(errs => sys.error(errs.render), identity)

  final case class Or(value: List[And]) extends AnyVal
  final case class And(value: List[DecVerExtMatcher]) extends AnyVal

  extension (decVerExtMatchers: DecVerExtMatchers) {

    def matches(decVerExt: DecVerExt): Boolean = decVerExtMatchers match {
      case DecVerExtMatchers(DecVerExtMatchers.Or(ors)) =>
        ors
          .find {
            case DecVerExtMatchers.And(ands) =>
              ands.forall(_.matches(decVerExt))
          }
          .fold(false)(_ => true)
    }

    def render: String = decVerExtMatchers match {
      case DecVerExtMatchers(Or(ands)) =>
        ands
          .map {
            case And(matcher) =>
              matcher.map(_.render).mkString(" ")
          }
          .mkString(" || ")
    }

  }

  final case class ParseErrors(
    private val error: DecVerExtMatcher.ParseError,
    private val errors: List[DecVerExtMatcher.ParseError]
  )
  object ParseErrors {

    extension (parseErrors: ParseErrors) {

      /** Returns List[DecVerExtMatcher.ParseError]. The List returned is guaranteed non-empty.
        */
      def allErrors: List[DecVerExtMatcher.ParseError] = parseErrors.error :: parseErrors.errors

      def render: String = allErrors.map(_.render).mkString("[", ", ", "]")
    }

    extension (eth: Either[ParseErrors, DecVerExtMatchers])  {
      def toDecVerExtParseError: Either[DecVerExtParseError, DecVerExtMatchers] =
        eth.left.map(DecVerExtParseError.decVerExtMatchersParseErrors)
    }
  }
}
