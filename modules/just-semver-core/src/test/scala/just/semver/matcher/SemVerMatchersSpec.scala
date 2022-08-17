package just.semver.matcher

import hedgehog._
import hedgehog.runner._
import just.Common._
import just.semver.SemVer
import just.semver.expr.ComparisonOperator

import scala.util.Try

/** @author Kevin Lee
  * @since 2022-04-08
  */
object SemVerMatchersSpec extends Properties {
  override def tests: List[Test] = List(
    property("test SemVerMatchers.parse(Valid)", testSemVerMatchersParseValid),
    property("test SemVerMatchers.parse(Invalid)", testSemVerMatchersParseInvalid),
    property("test SemVerMatchers.unsafeParse(Valid)", testSemVerMatchersParseValid),
    property("test SemVerMatchers.unsafeParse(Invalid)", testSemVerMatchersUnsafeParseInvalid)
  ) ++ List(
    example(
      "test  Example-1 SemVerMatchers(1.0.0 - 2.0.0).matches(1.0.0) should return true",
      MatchesSpec.testExample1
    ),
    example(
      "test  Example-2 SemVerMatchers(1.0.0 - 2.0.0).matches(2.0.0) should return true",
      MatchesSpec.testExample2
    ),
    example(
      "test  Example-3 SemVerMatchers(1.0.0 - 2.0.0).matches(1.0.1) should return true",
      MatchesSpec.testExample3
    ),
    example(
      "test  Example-4 SemVerMatchers(1.0.0 - 2.0.0).matches(1.999.999) should return true",
      MatchesSpec.testExample4
    ),
    example(
      "test  Example-5 SemVerMatchers(>1.0.0 <2.0.0).matches(1.0.0) should return false",
      MatchesSpec.testExample5
    ),
    example(
      "test  Example-6 SemVerMatchers(>1.0.0 <2.0.0).matches(2.0.0) should return false",
      MatchesSpec.testExample6
    ),
    example(
      "test  Example-7 SemVerMatchers(>1.0.0 <2.0.0).matches(1.0.1) should return true",
      MatchesSpec.testExample7
    ),
    example(
      "test  Example-8 SemVerMatchers(>1.0.0 <2.0.0).matches(1.999.999) should return true",
      MatchesSpec.testExample8
    ),
    example(
      "test  Example-9 SemVerMatchers(>=1.0.0 <=2.0.0).matches(1.0.0) should return true",
      MatchesSpec.testExample5
    ),
    example(
      "test Example-10 SemVerMatchers(>=1.0.0 <=2.0.0).matches(2.0.0) should return true",
      MatchesSpec.testExample6
    ),
    example(
      "test Example-11 SemVerMatchers(>=1.0.0 <=2.0.0).matches(1.0.1) should return true",
      MatchesSpec.testExample7
    ),
    example(
      "test Example-12 SemVerMatchers(>=1.0.0 <=2.0.0).matches(1.999.999) should return true",
      MatchesSpec.testExample8
    ),
    property(
      "test SemVerMatchers(Range || Comparison).matches(Valid)",
      MatchesSpec.testSemVerMatchersRangeOrComparisonMatchesValid
    ),
    property(
      "test SemVerMatchers(Comparison and Comparison).matches(Valid)",
      MatchesSpec.testSemVerMatchersComparisonAndComparisonMatchesValid
    ),
    property(
      "test SemVerMatchers(Range || Comparison and Comparison).matches(Valid)",
      MatchesSpec.testSemVerMatchersRangeOrComparisonAndComparisonMatchesValid
    )
  )

  def testSemVerMatchersParseValid: Property = for {
    semVerMatchers <- Gens
                        .genSemVerMatcher
                        .list(Range.linear(1, 5))
                        .list(Range.linear(1, 3))
                        .map { listOfList =>
                          SemVerMatchers(
                            SemVerMatchers.Or(listOfList.map { semVerMatchers =>
                              SemVerMatchers.And(semVerMatchers)
                            })
                          )
                        }
                        .log("semVerMatchers")
  } yield {
    val input = semVerMatchers.render
    SemVerMatchers.parse(input) match {
      case Right(actual) =>
        val expected = semVerMatchers
        actual ==== expected
      case Left(errs) =>
        Result
          .failure
          .log(
            s"""${errs.render}
               |input: $input
               |""".stripMargin
          )
    }
  }

  def testSemVerMatchersParseInvalid: Property = for {
    invalidSemVerMatchers <- Gen
                               .string(Gen.alpha, Range.linear(1, 10))
                               .log("invalidSemVerMatchers")
  } yield {
    SemVerMatchers.parse(invalidSemVerMatchers) match {
      case Right(actual) =>
        Result
          .failure
          .log(
            s"""Expected List[SemVerMatcher.ParseError] but got ${actual.render} instead}
               |input: $invalidSemVerMatchers
               |""".stripMargin
          )
      case Left(errs) =>
        Result.all(
          List(
            Result.assert(errs.allErrors.nonEmpty).log("SemVerMatchers.parse(invalid) failed but no errors found"),
            Result
              .assert(errs.allErrors.map(_.render).forall(s => s.contains("ParseError") && s.contains("at 0")))
              .log(s"""SemVerMatchers.parse(invalid) failed but doesn't have expected ParseError.
                  |> Errors: ${errs.allErrors.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
                  |""".stripMargin)
          )
        )
    }
  }

  def testSemVerMatchersUnsafeParseValid: Property = for {
    semVerMatchers <- Gens
                        .genSemVerMatcher
                        .list(Range.linear(1, 5))
                        .list(Range.linear(1, 3))
                        .map { listOfList =>
                          SemVerMatchers(
                            SemVerMatchers.Or(listOfList.map { semVerMatchers =>
                              SemVerMatchers.And(semVerMatchers)
                            })
                          )
                        }
                        .log("semVerMatchers")
  } yield {
    val input = semVerMatchers.render
    Try {
      SemVerMatchers.unsafeParse(input)
    } match {
      case scala.util.Success(actual) =>
        val expected = semVerMatchers
        actual ==== expected

      case scala.util.Failure(errs) =>
        Result
          .failure
          .log(
            s"""${errs.getMessage}
               |input: $input
               |""".stripMargin
          )
    }
  }

  def testSemVerMatchersUnsafeParseInvalid: Property = for {
    invalidSemVerMatchers <- Gen
                               .string(Gen.alpha, Range.linear(1, 10))
                               .log("invalidSemVerMatchers")
  } yield {
    Try {
      SemVerMatchers.unsafeParse(invalidSemVerMatchers)
    } match {
      case scala.util.Success(actual) =>
        Result
          .failure
          .log(
            s"""Expected List[SemVerMatcher.ParseError] but got ${actual.render} instead}
               |input: $invalidSemVerMatchers
               |""".stripMargin
          )
      case scala.util.Failure(errs) =>
        Result
          .assert(errs.getMessage.contains("ParseError") && errs.getMessage.contains("at 0"))
          .log(s"""SemVerMatchers.parse(invalid) failed but doesn't have expected ParseError.
                      |> Errors: ${errs.getMessage}
                      |""".stripMargin)

    }
  }

  object MatchesSpec {

    def testExample1: Result = {
      Result
        .assert(SemVerMatchers.unsafeParse("1.0.0 - 2.0.0").matches(SemVer.unsafeParse("1.0.0")))
        .log("SemVerMatchers(1.0.0 - 2.0.0).matches(1.0.0) failed")
    }

    def testExample2: Result = {
      Result
        .assert(SemVerMatchers.unsafeParse("1.0.0 - 2.0.0").matches(SemVer.unsafeParse("2.0.0")))
        .log("SemVerMatchers(1.0.0 - 2.0.0).matches(2.0.0) failed")
    }

    def testExample3: Result = {
      Result
        .assert(SemVerMatchers.unsafeParse("1.0.0 - 2.0.0").matches(SemVer.unsafeParse("1.0.1")))
        .log("SemVerMatchers(1.0.0 - 2.0.0).matches(1.0.1) failed")
    }

    def testExample4: Result = {
      Result
        .assert(SemVerMatchers.unsafeParse("1.0.0 - 2.0.0").matches(SemVer.unsafeParse("1.999.999")))
        .log("SemVerMatchers(1.0.0 - 2.0.0).matches(1.999.999) failed")
    }

    def testExample5: Result = {
      Result
        .diff(SemVerMatchers.unsafeParse(">1.0.0 <2.0.0").matches(SemVer.unsafeParse("1.0.0")), false)(_ === _)
        .log("SemVerMatchers(>1.0.0 <2.0.0).matches(1.0.0) failed")
    }

    def testExample6: Result = {
      Result
        .diff(SemVerMatchers.unsafeParse(">1.0.0 <2.0.0").matches(SemVer.unsafeParse("2.0.0")), false)(_ === _)
        .log("SemVerMatchers(>1.0.0 <2.0.0).matches(2.0.0) failed")
    }

    def testExample7: Result = {
      Result
        .assert(SemVerMatchers.unsafeParse(">1.0.0 <2.0.0").matches(SemVer.unsafeParse("1.0.1")))
        .log("SemVerMatchers(>1.0.0 <2.0.0).matches(1.0.1) failed")
    }

    def testExample8: Result = {
      Result
        .assert(SemVerMatchers.unsafeParse(">1.0.0 <2.0.0").matches(SemVer.unsafeParse("1.999.999")))
        .log("SemVerMatchers(>1.0.0 <2.0.0).matches(1.999.999) failed")
    }

    def testExample9: Result = {
      Result
        .assert(SemVerMatchers.unsafeParse(">=1.0.0 <=2.0.0").matches(SemVer.unsafeParse("1.0.0")))
        .log("SemVerMatchers(>=1.0.0 <=2.0.0).matches(1.0.0) failed")
    }

    def testExample10: Result = {
      Result
        .assert(SemVerMatchers.unsafeParse(">=1.0.0 <=2.0.0").matches(SemVer.unsafeParse("2.0.0")))
        .log("SemVerMatchers(>=1.0.0 <=2.0.0).matches(2.0.0) failed")
    }

    def testExample11: Result = {
      Result
        .assert(SemVerMatchers.unsafeParse(">=1.0.0 <=2.0.0").matches(SemVer.unsafeParse("1.0.1")))
        .log("SemVerMatchers(>=1.0.0 <=2.0.0).matches(1.0.1) failed")
    }

    def testExample12: Result = {
      Result
        .assert(SemVerMatchers.unsafeParse(">=1.0.0 <=2.0.0").matches(SemVer.unsafeParse("1.999.999")))
        .log("SemVerMatchers(>=1.0.0 <=2.0.0).matches(1.999.999) failed")
    }

    def testSemVerMatchersRangeOrComparisonMatchesValid: Property = for {
      major  <- Gen.int(Range.linear(5, 10)).log("major")
      minor  <- Gen.int(Range.linear(1, 10)).log("minor")
      patch  <- Gen.int(Range.linear(1, 100)).log("patch")
      major2 <- Gen.int(Range.linear(5, 10)).map(_ + major).log("major2")
      minor2 <- Gen.int(Range.linear(1, 10)).map(_ + minor).log("minor2")
      patch2 <- Gen.int(Range.linear(1, 100)).map(_ + patch).log("patch2")
      op     <- Gens.genComparisonOperator.log("op")
    } yield {
      val semVer = SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch))

      val v1       = SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor - 1), SemVer.Patch(patch - 1))
      val v2       = SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor + 1), SemVer.Patch(patch + 1))
      val matcher1 = SemVerMatcher.range(v1, v2)

      val semVer2  = SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2), SemVer.Patch(patch2))
      val matcher2 = SemVerMatcher.comparison(SemVerComparison(op, semVer2))

      // format: off
      val versions = op match {
        case ComparisonOperator.Lt =>
          List(
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2 - 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2), SemVer.Patch(patch2 - 1)),
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2 - 1), SemVer.Patch(patch2 - 1))
          )
        case ComparisonOperator.Le =>
          List(
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2 - 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2), SemVer.Patch(patch2 - 1)),
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2 - 1), SemVer.Patch(patch2 - 1)),
            semVer2.copy()
          )
        case ComparisonOperator.Eql =>
          List(
            semVer2.copy()
          )
        case ComparisonOperator.Ne =>
          List(
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2 - 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2), SemVer.Patch(patch2 - 1)),
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2 - 1), SemVer.Patch(patch2 - 1)),
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2 + 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2), SemVer.Patch(patch2 + 1)),
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2 + 1), SemVer.Patch(patch2 + 1))
          )
        case ComparisonOperator.Gt =>
          List(
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2 + 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2), SemVer.Patch(patch2 + 1)),
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2 + 1), SemVer.Patch(patch2 + 1))
          )
        case ComparisonOperator.Ge =>
          List(
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2 + 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2), SemVer.Patch(patch2 + 1)),
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2 + 1), SemVer.Patch(patch2 + 1)),
            semVer2.copy()
          )
      }
      // format: on

      val semVerMatchers = SemVerMatchers.unsafeParse(s"${matcher1.render} || ${matcher2.render}")

      println(
        s"""# Range || Comparison:
           |- matchers: ${semVerMatchers.render}
           |-   semVer: ${semVer.render}
           |-  semVers: ${versions.map(_.render).mkString("[\n>    - ", "\n>    - ", "\n>    ]")}
           |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(semVerMatchers.matches(semVer))
            .log(
              s""" Range || Comparison - range test failed
                 |> matchers: ${semVerMatchers.render}
                 |>   semVer: ${semVer.render}
                 |""".stripMargin
            ),
          Result
            .assert(versions.forall(v => semVerMatchers.matches(v)))
            .log(
              s""" Range || Comparison - comparison test failed
                 |> matchers: ${semVerMatchers.render}
                 |>  semVers: ${versions.map(_.render).mkString("[\n>    - ", "\n>    - ", "\n>    ]")}
                 |""".stripMargin
            )
        )
      )
    }

    def testSemVerMatchersComparisonAndComparisonMatchesValid: Property = for {
      v1V2SemVer <- Gens
                      .genRangedSemVerComparison(
                        Range.linear(11, 30),
                        Range.linear(11, 50),
                        Range.linear(11, 100)
                      )
                      .log("(v1, v2, semVer)")
      (v1, v2, semVer) = v1V2SemVer
    } yield {
      val semVerMatchers = SemVerMatchers.unsafeParse(s"${v1.render} ${v2.render}")

      println(
        s"""# Comparison and Comparison
           |- matchers: ${semVerMatchers.render}
           |-   semVer: ${semVer.render}
           |""".stripMargin
      )

      Result
        .assert(semVerMatchers.matches(semVer))
        .log(
          s""" Comparison and Comparison - failed
             |> matchers: ${semVerMatchers.render}
             |>   semVer: ${semVer.render}
             |""".stripMargin
        )
    }

    def testSemVerMatchersRangeOrComparisonAndComparisonMatchesValid: Property = for {
      rangeMatcherSemVerInRange <- Gens
                                     .genSemVerMatcherRangeAndSemverInRange(
                                       majorRange = Range.linear(11, 30),
                                       minorRange = Range.linear(11, 50),
                                       patchRange = Range.linear(11, 100)
                                     )
                                     .log("(rangeMatcher, semVerInRange)")
      (rangeMatcher, semVerInRange) = rangeMatcherSemVerInRange
      v1V2SemVer <- Gens
                      .genRangedSemVerComparison(
                        majorRange = Range.linear(31, 100),
                        minorRange = Range.linear(51, 100),
                        patchRange = Range.linear(101, 1000)
                      )
                      .log("(v1, v2, semVer)")
      (v1, v2, semVer) = v1V2SemVer
    } yield {
      val semVerMatchers = SemVerMatchers.unsafeParse(s"${rangeMatcher.render} || ${v1.render} ${v2.render}")

      println(
        s"""# Range || Comparison and Comparison
           |-      matchers: ${semVerMatchers.render}
           |- semVerInRange: ${semVerInRange.render}
           |-  semVerInComp: ${semVer.render}
           |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(semVerMatchers.matches(semVerInRange))
            .log(
              s""" Range || Comparison and Comparison - testing range failed
                 |>      matchers: ${semVerMatchers.render}
                 |> semVerInRange: ${semVerInRange.render}
                 |""".stripMargin
            ),
          Result
            .assert(semVerMatchers.matches(semVer))
            .log(
              s""" Range || Comparison and Comparison - testing (comparison and comparison) failed
                 |>     matchers: ${semVerMatchers.render}
                 |> semVerInComp: ${semVer.render}
                 |""".stripMargin
            )
        )
      )
    }
  }

}
