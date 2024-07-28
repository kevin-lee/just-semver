package just.decver.matcher

import hedgehog._
import hedgehog.runner._
import just.Common._
import just.decver.DecVerExt
import just.semver.expr.ComparisonOperator

/** @author Kevin Lee
  * @since 2022-04-08
  */
object DecVerExtMatcherSpec extends Properties {
  override def tests: List[Test] = List(
    property("test DecVerExtMatcher.Range.matches(Valid)", testDecVerExtMatcherRangeMatchesValid),
    property("test DecVerExtMatcher.Range.matches(Invalid)", testDecVerExtMatcherRangeMatchesInvalid),
    property("test DecVerExtMatcher.Comparison.matches(Valid)", testDecVerExtMatcherComparisonMatchesValid),
    property("test DecVerExtMatcher.Comparison.matches(Invalid)", testDecVerExtMatcherComparisonMatchesInvalid)
  )

  def testDecVerExtMatcherRangeMatchesValid: Property = for {
    major <- Gen.int(Range.linear(5, 10)).log("major")
    minor <- Gen.int(Range.linear(1, 10)).log("minor")
  } yield {
    val decVerExt = DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor))

    val v1 = DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor - 1))
    val v2 = DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor + 1))

    val matcher = DecVerExtMatcher.range(v1, v2)
    Result
      .assert(matcher.matches(decVerExt))
      .log(
        s"""Log:
           |>     Range: ${matcher.render}
           |> decVerExt: ${decVerExt.render}
           |""".stripMargin
      )
  }

  def testDecVerExtMatcherRangeMatchesInvalid: Property = for {
    major <- Gen.int(Range.linear(5, 10)).log("major")
    minor <- Gen.int(Range.linear(2, 10)).log("minor")
    size  <- Gen.int(Range.linear(1, 10)).log("size")

    majorMoreLess <- Gen.int(Range.linear(0, 1)).log("majorMoreLess")
    minorMoreLess <- Gen.int(Range.linear(1, 2)).log("minorMoreLess")
  } yield {
    val decVerExt1 = DecVerExt.withMajorMinor(
      DecVerExt.Major(major - majorMoreLess),
      DecVerExt.Minor(minor - minorMoreLess),
    )

    val v1      = DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor))
    val v2      = DecVerExt.withMajorMinor(DecVerExt.Major(major + size), DecVerExt.Minor(minor + size))
    val matcher = DecVerExtMatcher.range(v1, v2)

    val decVerExt2 = v2.copy(
      major = DecVerExt.Major(v2.major.value + majorMoreLess),
      minor = DecVerExt.Minor(v2.minor.value + minorMoreLess),
    )

    Result.all(
      List(
        Result
          .diff(matcher.matches(decVerExt1), false)(_ === _)
          .log(
            s"""Log:
               |>      Range: ${matcher.render}
               |> decVerExt1: ${decVerExt1.render}
               |""".stripMargin
          ),
        Result
          .diff(matcher.matches(decVerExt2), false)(_ === _)
          .log(
            s"""Log:
               |>      Range: ${matcher.render}
               |> decVerExt2: ${decVerExt2.render}
               |""".stripMargin
          )
      )
    )
  }

  def testDecVerExtMatcherComparisonMatchesValid: Property = for {
    major <- Gen.int(Range.linear(5, 10)).log("major")
    minor <- Gen.int(Range.linear(1, 10)).log("minor")
    op    <- Gens.genComparisonOperator.log("op")
  } yield {
    val decVerExt = DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor))

    val versions = op match {
      case ComparisonOperator.Lt =>
        List(
          DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor - 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor - 1))
        )
      case ComparisonOperator.Le =>
        List(
          DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor - 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor - 1)),
          decVerExt.copy()
        )
      case ComparisonOperator.Eql =>
        List(
          decVerExt.copy()
        )
      case ComparisonOperator.Ne =>
        List(
          DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor - 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor - 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor + 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor + 1))
        )
      case ComparisonOperator.Gt =>
        List(
          DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor + 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor + 1))
        )
      case ComparisonOperator.Ge =>
        List(
          DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor + 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor + 1)),
          decVerExt.copy()
        )
    }

    val matcher = DecVerExtMatcher.comparison(DecVerExtComparison(op, decVerExt))

    Result
      .assert(versions.forall(matcher.matches))
      .log(
        s"""Log:
           |> Comparison: ${matcher.render}
           |> decVerExts: ${versions.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
           |""".stripMargin
      )
  }

  def testDecVerExtMatcherComparisonMatchesInvalid: Property = for {
    major <- Gen.int(Range.linear(5, 10)).log("major")
    minor <- Gen.int(Range.linear(1, 10)).log("minor")
    op    <- Gens.genComparisonOperator.log("op")
  } yield {
    val decVerExt = DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor))

    val versions = op match {
      case ComparisonOperator.Lt =>
        List(
          DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor + 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor + 1)),
          decVerExt.copy()
        )
      case ComparisonOperator.Le =>
        List(
          DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor + 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor + 1))
        )
      case ComparisonOperator.Eql =>
        List(
          DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor - 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor - 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor + 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor + 1))
        )
      case ComparisonOperator.Ne =>
        List(
          decVerExt.copy()
        )
      case ComparisonOperator.Gt =>
        List(
          DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor - 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor - 1)),
          decVerExt.copy()
        )
      case ComparisonOperator.Ge =>
        List(
          DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor - 1)),
          DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor - 1))
        )
    }

    val matcher = DecVerExtMatcher.comparison(DecVerExtComparison(op, decVerExt))

    Result
      .assert(versions.forall(!matcher.matches(_)))
      .log(
        s"""Log:
           |> Comparison: ${matcher.render}
           |> decVerExts: ${versions.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
           |""".stripMargin
      )
  }

}
