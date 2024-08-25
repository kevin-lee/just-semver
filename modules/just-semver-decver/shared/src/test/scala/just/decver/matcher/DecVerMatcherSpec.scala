package just.decver.matcher

import hedgehog._
import hedgehog.runner._
import just.Common._
import just.decver.DecVer
import just.semver.expr.ComparisonOperator

/** @author Kevin Lee
  * @since 2022-04-08
  */
object DecVerMatcherSpec extends Properties {
  override def tests: List[Test] = List(
    property("test DecVerMatcher.Range.matches(Valid)", testDecVerMatcherRangeMatchesValid),
    property("test DecVerMatcher.Range.matches(Invalid)", testDecVerMatcherRangeMatchesInvalid),
    property("test DecVerMatcher.Comparison.matches(Valid)", testDecVerMatcherComparisonMatchesValid),
    property("test DecVerMatcher.Comparison.matches(Invalid)", testDecVerMatcherComparisonMatchesInvalid)
  )

  def testDecVerMatcherRangeMatchesValid: Property = for {
    major <- Gen.int(Range.linear(5, 10)).log("major")
    minor <- Gen.int(Range.linear(1, 10)).log("minor")
  } yield {
    val decVer = DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor))

    val v1 = DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor - 1))
    val v2 = DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor + 1))

    val matcher = DecVerMatcher.range(v1, v2)
    Result
      .assert(matcher.matches(decVer))
      .log(
        s"""Log:
           |>     Range: ${matcher.render}
           |> decVer: ${decVer.render}
           |""".stripMargin
      )
  }

  def testDecVerMatcherRangeMatchesInvalid: Property = for {
    major <- Gen.int(Range.linear(5, 10)).log("major")
    minor <- Gen.int(Range.linear(2, 10)).log("minor")
    size  <- Gen.int(Range.linear(1, 10)).log("size")

    majorMoreLess <- Gen.int(Range.linear(0, 1)).log("majorMoreLess")
    minorMoreLess <- Gen.int(Range.linear(1, 2)).log("minorMoreLess")
  } yield {
    val decVer1 = DecVer.withMajorMinor(
      DecVer.Major(major - majorMoreLess),
      DecVer.Minor(minor - minorMoreLess),
    )

    val v1      = DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor))
    val v2      = DecVer.withMajorMinor(DecVer.Major(major + size), DecVer.Minor(minor + size))
    val matcher = DecVerMatcher.range(v1, v2)

    val decVer2 = v2.copy(
      major = DecVer.Major(v2.major.value + majorMoreLess),
      minor = DecVer.Minor(v2.minor.value + minorMoreLess),
    )

    Result.all(
      List(
        Result
          .diff(matcher.matches(decVer1), false)(_ === _)
          .log(
            s"""Log:
               |>      Range: ${matcher.render}
               |> decVer1: ${decVer1.render}
               |""".stripMargin
          ),
        Result
          .diff(matcher.matches(decVer2), false)(_ === _)
          .log(
            s"""Log:
               |>      Range: ${matcher.render}
               |> decVer2: ${decVer2.render}
               |""".stripMargin
          )
      )
    )
  }

  def testDecVerMatcherComparisonMatchesValid: Property = for {
    major <- Gen.int(Range.linear(5, 10)).log("major")
    minor <- Gen.int(Range.linear(1, 10)).log("minor")
    op    <- Gens.genComparisonOperator.log("op")
  } yield {
    val decVer = DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor))

    val versions = op match {
      case ComparisonOperator.Lt =>
        List(
          DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor - 1)),
          DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor - 1))
        )
      case ComparisonOperator.Le =>
        List(
          DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor - 1)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor - 1)),
          decVer.copy()
        )
      case ComparisonOperator.Eql =>
        List(
          decVer.copy()
        )
      case ComparisonOperator.Ne =>
        List(
          DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor - 1)),
          DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor - 1)),
          DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor + 1)),
          DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor + 1))
        )
      case ComparisonOperator.Gt =>
        List(
          DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor + 1)),
          DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor + 1))
        )
      case ComparisonOperator.Ge =>
        List(
          DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor + 1)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor + 1)),
          decVer.copy()
        )
    }

    val matcher = DecVerMatcher.comparison(DecVerComparison(op, decVer))

    Result
      .assert(versions.forall(matcher.matches))
      .log(
        s"""Log:
           |> Comparison: ${matcher.render}
           |> decVers: ${versions.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
           |""".stripMargin
      )
  }

  def testDecVerMatcherComparisonMatchesInvalid: Property = for {
    major <- Gen.int(Range.linear(5, 10)).log("major")
    minor <- Gen.int(Range.linear(1, 10)).log("minor")
    op    <- Gens.genComparisonOperator.log("op")
  } yield {
    val decVer = DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor))

    val versions = op match {
      case ComparisonOperator.Lt =>
        List(
          DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor + 1)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor + 1)),
          decVer.copy()
        )
      case ComparisonOperator.Le =>
        List(
          DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor + 1)),
          DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor + 1))
        )
      case ComparisonOperator.Eql =>
        List(
          DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor - 1)),
          DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor - 1)),
          DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor + 1)),
          DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor + 1))
        )
      case ComparisonOperator.Ne =>
        List(
          decVer.copy()
        )
      case ComparisonOperator.Gt =>
        List(
          DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor - 1)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor - 1)),
          decVer.copy()
        )
      case ComparisonOperator.Ge =>
        List(
          DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor)),
          DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor - 1)),
          DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor - 1))
        )
    }

    val matcher = DecVerMatcher.comparison(DecVerComparison(op, decVer))

    Result
      .assert(versions.forall(!matcher.matches(_)))
      .log(
        s"""Log:
           |> Comparison: ${matcher.render}
           |> decVers: ${versions.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
           |""".stripMargin
      )
  }

}
