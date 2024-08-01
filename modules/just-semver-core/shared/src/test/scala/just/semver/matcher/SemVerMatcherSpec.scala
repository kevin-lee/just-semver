package just.semver.matcher

import hedgehog._
import hedgehog.runner._
import just.Common._
import just.semver.SemVer
import just.semver.expr.ComparisonOperator

/** @author Kevin Lee
  * @since 2022-04-08
  */
object SemVerMatcherSpec extends Properties {
  override def tests: List[Test] = List(
    property("test SemVerMatcher.Range.matches(Valid)", testSemVerMatcherRangeMatchesValid),
    property("test SemVerMatcher.Range.matches(Invalid)", testSemVerMatcherRangeMatchesInvalid),
    property("test SemVerMatcher.Comparison.matches(Valid)", testSemVerMatcherComparisonMatchesValid),
    property("test SemVerMatcher.Comparison.matches(Invalid)", testSemVerMatcherComparisonMatchesInvalid)
  )

  def testSemVerMatcherRangeMatchesValid: Property = for {
    major <- Gen.int(Range.linear(5, 10)).log("major")
    minor <- Gen.int(Range.linear(1, 10)).log("minor")
    patch <- Gen.int(Range.linear(1, 100)).log("patch")
  } yield {
    val semVer = SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch))

    val v1 = SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor - 1), SemVer.Patch(patch - 1))
    val v2 = SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor + 1), SemVer.Patch(patch + 1))

    val matcher = SemVerMatcher.range(v1, v2)
    Result
      .assert(matcher.matches(semVer))
      .log(
        s"""Log:
           |>  Range: ${matcher.render}
           |> semVer: ${semVer.render}
           |""".stripMargin
      )
  }

  def testSemVerMatcherRangeMatchesInvalid: Property = for {
    major         <- Gen.int(Range.linear(5, 10)).log("major")
    minor         <- Gen.int(Range.linear(1, 10)).log("minor")
    patch         <- Gen.int(Range.linear(1, 100)).log("patch")
    majorMoreLess <- Gen.int(Range.linear(0, 1)).log("majorMoreLess")
    minorMoreLess <- Gen.int(Range.linear(0, 1)).log("minorMoreLess")
    patchMoreLess <- Gen.int(Range.linear(0, 1)).log("patchMoreLess")
  } yield {
    val patchMoreLess1 = if ((majorMoreLess + minorMoreLess + patchMoreLess) === 0) 1 else patchMoreLess
    val semVer1        = SemVer.semVer(
      SemVer.Major(major - majorMoreLess),
      SemVer.Minor(minor - minorMoreLess),
      SemVer.Patch(patch - patchMoreLess1)
    )
    val v1             = SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch))
    val v2             = SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor + 1), SemVer.Patch(patch + 1))
    val matcher        = SemVerMatcher.range(v1, v2)

    val semVer2 = v2.copy(
      major = SemVer.Major(v2.major.value + majorMoreLess),
      minor = SemVer.Minor(v2.minor.value + minorMoreLess),
      patch = SemVer.Patch(v2.patch.value + patchMoreLess1)
    )

    Result.all(
      List(
        Result
          .diff(matcher.matches(semVer1), false)(_ === _)
          .log(
            s"""Log:
           |>   Range: ${matcher.render}
           |> semVer1: ${semVer1.render}
           |""".stripMargin
          ),
        Result
          .diff(matcher.matches(semVer2), false)(_ === _)
          .log(
            s"""Log:
           |>   Range: ${matcher.render}
           |> semVer2: ${semVer2.render}
           |""".stripMargin
          )
      )
    )
  }

  def testSemVerMatcherComparisonMatchesValid: Property = for {
    major <- Gen.int(Range.linear(5, 10)).log("major")
    minor <- Gen.int(Range.linear(1, 10)).log("minor")
    patch <- Gen.int(Range.linear(1, 100)).log("patch")
    op    <- Gens.genComparisonOperator.log("op")
  } yield {
    val semVer = SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch))

    val versions = op match {
      case ComparisonOperator.Lt =>
        List(
          SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor - 1), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch - 1)),
          SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor - 1), SemVer.Patch(patch - 1))
        )
      case ComparisonOperator.Le =>
        List(
          SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor - 1), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch - 1)),
          SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor - 1), SemVer.Patch(patch - 1)),
          semVer.copy()
        )
      case ComparisonOperator.Eql =>
        List(
          semVer.copy()
        )
      case ComparisonOperator.Ne =>
        List(
          SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor - 1), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch - 1)),
          SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor - 1), SemVer.Patch(patch - 1)),
          SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor + 1), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch + 1)),
          SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor + 1), SemVer.Patch(patch + 1))
        )
      case ComparisonOperator.Gt =>
        List(
          SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor + 1), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch + 1)),
          SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor + 1), SemVer.Patch(patch + 1))
        )
      case ComparisonOperator.Ge =>
        List(
          SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor + 1), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch + 1)),
          SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor + 1), SemVer.Patch(patch + 1)),
          semVer.copy()
        )
    }

    val matcher = SemVerMatcher.comparison(SemVerComparison(op, semVer))

    Result
      .assert(versions.forall(matcher.matches))
      .log(
        s"""Log:
           |> Comparison: ${matcher.render}
           |>    semVers: ${versions.map(_.render).mkString("[\n>      - ", "\n>      - ", "\n>    ]")}
           |""".stripMargin
      )
  }

  def testSemVerMatcherComparisonMatchesInvalid: Property = for {
    major <- Gen.int(Range.linear(5, 10)).log("major")
    minor <- Gen.int(Range.linear(1, 10)).log("minor")
    patch <- Gen.int(Range.linear(1, 100)).log("patch")
    op    <- Gens.genComparisonOperator.log("op")
  } yield {
    val semVer = SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch))

    val versions = op match {
      case ComparisonOperator.Lt =>
        List(
          SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor + 1), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch + 1)),
          SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor + 1), SemVer.Patch(patch + 1)),
          semVer.copy()
        )
      case ComparisonOperator.Le =>
        List(
          SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor + 1), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch + 1)),
          SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor + 1), SemVer.Patch(patch + 1))
        )
      case ComparisonOperator.Eql =>
        List(
          SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor - 1), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch - 1)),
          SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor - 1), SemVer.Patch(patch - 1)),
          SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor + 1), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch + 1)),
          SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor + 1), SemVer.Patch(patch + 1))
        )
      case ComparisonOperator.Ne =>
        List(
          semVer.copy()
        )
      case ComparisonOperator.Gt =>
        List(
          SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor - 1), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch - 1)),
          SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor - 1), SemVer.Patch(patch - 1)),
          semVer.copy()
        )
      case ComparisonOperator.Ge =>
        List(
          SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor - 1), SemVer.Patch(patch)),
          SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch - 1)),
          SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor - 1), SemVer.Patch(patch - 1))
        )
    }

    val matcher = SemVerMatcher.comparison(SemVerComparison(op, semVer))

    Result
      .assert(versions.forall(!matcher.matches(_)))
      .log(
        s"""Log:
           |> Comparison: ${matcher.render}
           |>    semVers: ${versions.map(_.render).mkString("[\n>      - ", "\n>      - ", "\n>    ]")}
           |""".stripMargin
      )
  }

}
