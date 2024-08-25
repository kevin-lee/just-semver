package just.decver.matcher

import hedgehog._
import hedgehog.runner._
import just.Common._
import just.decver.DecVer
import just.semver.expr.ComparisonOperator

import scala.util.Try

/** @author Kevin Lee
  * @since 2022-04-08
  */
object DecVerMatchersSpec extends Properties {
  override def tests: List[Test] = List(
    property("test DecVerMatchers.parse(Valid)", testDecVerMatchersParseValid),
    property("test DecVerMatchers.parse(Invalid)", testDecVerMatchersParseInvalid),
    property("test DecVerMatchers.unsafeParse(Valid)", testDecVerMatchersParseValid),
    property("test DecVerMatchers.unsafeParse(Invalid)", testDecVerMatchersUnsafeParseInvalid)
  ) ++ List(
    example(
      "test  Example-1 DecVerMatchers(1.0 - 2.0).matches(1.0) should return true",
      MatchesSpec.testExample1
    ),
    example(
      "test  Example-2 DecVerMatchers(1.0 - 2.0).matches(2.0) should return true",
      MatchesSpec.testExample2
    ),
    example(
      "test  Example-3 DecVerMatchers(1.0 - 2.0).matches(1.1) should return true",
      MatchesSpec.testExample3
    ),
    example(
      "test  Example-4 DecVerMatchers(1.0 - 2.0).matches(1.999) should return true",
      MatchesSpec.testExample4
    ),
    example(
      "test  Example-5 DecVerMatchers(>1.0 <2.0).matches(1.0) should return false",
      MatchesSpec.testExample5
    ),
    example(
      "test  Example-6 DecVerMatchers(>1.0 <2.0).matches(2.0) should return false",
      MatchesSpec.testExample6
    ),
    example(
      "test  Example-7 DecVerMatchers(>1.0 <2.0).matches(1.1) should return true",
      MatchesSpec.testExample7
    ),
    example(
      "test  Example-8 DecVerMatchers(>1.0 <2.0).matches(1.999) should return true",
      MatchesSpec.testExample8
    ),
    example(
      "test  Example-9 DecVerMatchers(>=1.0 <=2.0).matches(1.0) should return true",
      MatchesSpec.testExample9
    ),
    example(
      "test Example-10 DecVerMatchers(>=1.0 <=2.0).matches(2.0) should return true",
      MatchesSpec.testExample10
    ),
    example(
      "test Example-11 DecVerMatchers(>=1.0 <=2.0).matches(1.1) should return true",
      MatchesSpec.testExample11
    ),
    example(
      "test Example-12 DecVerMatchers(>=1.0 <=2.0).matches(1.999) should return true",
      MatchesSpec.testExample12
    ),
    property(
      "test DecVerMatchers(Range || Comparison).matches(Valid)",
      MatchesSpec.testDecVerMatchersRangeOrComparisonMatchesValid
    ),
    property(
      "test DecVerMatchers(Comparison and Comparison).matches(Valid)",
      MatchesSpec.testDecVerMatchersComparisonAndComparisonMatchesValid
    ),
    property(
      "test DecVerMatchers(Range || Comparison and Comparison).matches(Valid)",
      MatchesSpec.testDecVerMatchersRangeOrComparisonAndComparisonMatchesValid
    )
  )

  def testDecVerMatchersParseValid: Property = for {
    decVerMatchers <- Gens
                        .genDecVerMatcher
                        .list(Range.linear(1, 5))
                        .list(Range.linear(1, 3))
                        .map { listOfList =>
                          DecVerMatchers(
                            DecVerMatchers.Or(listOfList.map { decVerMatchers =>
                              DecVerMatchers.And(decVerMatchers)
                            })
                          )
                        }
                        .log("decVerMatchers")
  } yield {
    val input = decVerMatchers.render
    DecVerMatchers.parse(input) match {
      case Right(actual) =>
        val expected = decVerMatchers
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

  def testDecVerMatchersParseInvalid: Property = for {
    invalidDecVerMatchers <- Gen
                               .string(Gen.alpha, Range.linear(1, 10))
                               .log("invalidDecVerMatchers")
  } yield {
    DecVerMatchers.parse(invalidDecVerMatchers) match {
      case Right(actual) =>
        Result
          .failure
          .log(
            s"""Expected List[DecVerMatcher.ParseError] but got ${actual.render} instead}
               |input: $invalidDecVerMatchers
               |""".stripMargin
          )
      case Left(errs) =>
        Result.all(
          List(
            Result.assert(errs.allErrors.nonEmpty).log("DecVerMatchers.parse(invalid) failed but no errors found"),
            Result
              .assert(errs.allErrors.map(_.render).forall(s => s.contains("ParseError") && s.contains("at 0")))
              .log(s"""DecVerMatchers.parse(invalid) failed but doesn't have expected ParseError.
                  |> Errors: ${errs.allErrors.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
                  |""".stripMargin)
          )
        )
    }
  }

  def testDecVerMatchersUnsafeParseValid: Property = for {
    decVerMatchers <- Gens
                        .genDecVerMatcher
                        .list(Range.linear(1, 5))
                        .list(Range.linear(1, 3))
                        .map { listOfList =>
                          DecVerMatchers(
                            DecVerMatchers.Or(listOfList.map { decVerMatchers =>
                              DecVerMatchers.And(decVerMatchers)
                            })
                          )
                        }
                        .log("decVerMatchers")
  } yield {
    val input = decVerMatchers.render
    Try {
      DecVerMatchers.unsafeParse(input)
    } match {
      case scala.util.Success(actual) =>
        val expected = decVerMatchers
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

  def testDecVerMatchersUnsafeParseInvalid: Property = for {
    invalidDecVerMatchers <- Gen
                               .string(Gen.alpha, Range.linear(1, 10))
                               .log("invalidDecVerMatchers")
  } yield {
    Try {
      DecVerMatchers.unsafeParse(invalidDecVerMatchers)
    } match {
      case scala.util.Success(actual) =>
        Result
          .failure
          .log(
            s"""Expected List[DecVerMatcher.ParseError] but got ${actual.render} instead}
               |input: $invalidDecVerMatchers
               |""".stripMargin
          )
      case scala.util.Failure(errs) =>
        Result
          .assert(errs.getMessage.contains("ParseError") && errs.getMessage.contains("at 0"))
          .log(s"""DecVerMatchers.parse(invalid) failed but doesn't have expected ParseError.
                      |> Errors: ${errs.getMessage}
                      |""".stripMargin)

    }
  }

  object MatchesSpec {

    def testExample1: Result = {
      Result
        .assert(DecVerMatchers.unsafeParse("1.0 - 2.0").matches(DecVer.unsafeParse("1.0")))
        .log("DecVerMatchers(1.0 - 2.0).matches(1.0) failed")
    }

    def testExample2: Result = {
      Result
        .assert(DecVerMatchers.unsafeParse("1.0 - 2.0").matches(DecVer.unsafeParse("2.0")))
        .log("DecVerMatchers(1.0 - 2.0).matches(2.0) failed")
    }

    def testExample3: Result = {
      Result
        .assert(DecVerMatchers.unsafeParse("1.0 - 2.0").matches(DecVer.unsafeParse("1.1")))
        .log("DecVerMatchers(1.0 - 2.0).matches(1.1) failed")
    }

    def testExample4: Result = {
      Result
        .assert(DecVerMatchers.unsafeParse("1.0 - 2.0").matches(DecVer.unsafeParse("1.999")))
        .log("DecVerMatchers(1.0 - 2.0).matches(1.999) failed")
    }

    def testExample5: Result = {
      Result
        .diff(DecVerMatchers.unsafeParse(">1.0 <2.0").matches(DecVer.unsafeParse("1.0")), false)(_ === _)
        .log("DecVerMatchers(>1.0 <2.0).matches(1.0) failed")
    }

    def testExample6: Result = {
      Result
        .diff(DecVerMatchers.unsafeParse(">1.0 <2.0").matches(DecVer.unsafeParse("2.0")), false)(_ === _)
        .log("DecVerMatchers(>1.0 <2.0).matches(2.0) failed")
    }

    def testExample7: Result = {
      Result
        .assert(DecVerMatchers.unsafeParse(">1.0 <2.0").matches(DecVer.unsafeParse("1.1")))
        .log("DecVerMatchers(>1.0 <2.0).matches(1.1) failed")
    }

    def testExample8: Result = {
      Result
        .assert(DecVerMatchers.unsafeParse(">1.0 <2.0").matches(DecVer.unsafeParse("1.999")))
        .log("DecVerMatchers(>1.0 <2.0).matches(1.999) failed")
    }

    def testExample9: Result = {
      Result
        .assert(DecVerMatchers.unsafeParse(">=1.0 <=2.0").matches(DecVer.unsafeParse("1.0")))
        .log("DecVerMatchers(>=1.0 <=2.0).matches(1.0) failed")
    }

    def testExample10: Result = {
      Result
        .assert(DecVerMatchers.unsafeParse(">=1.0 <=2.0").matches(DecVer.unsafeParse("2.0")))
        .log("DecVerMatchers(>=1.0 <=2.0).matches(2.0) failed")
    }

    def testExample11: Result = {
      Result
        .assert(DecVerMatchers.unsafeParse(">=1.0 <=2.0").matches(DecVer.unsafeParse("1.1")))
        .log("DecVerMatchers(>=1.0 <=2.0).matches(1.1) failed")
    }

    def testExample12: Result = {
      Result
        .assert(DecVerMatchers.unsafeParse(">=1.0 <=2.0").matches(DecVer.unsafeParse("1.999")))
        .log("DecVerMatchers(>=1.0 <=2.0).matches(1.999) failed")
    }

    def testDecVerMatchersRangeOrComparisonMatchesValid: Property = for {
      major  <- Gen.int(Range.linear(5, 10)).log("major")
      minor  <- Gen.int(Range.linear(1, 10)).log("minor")
      major2 <- Gen.int(Range.linear(5, 10)).map(_ + major).log("major2")
      minor2 <- Gen.int(Range.linear(1, 10)).map(_ + minor).log("minor2")
      op     <- Gens.genComparisonOperator.log("op")
    } yield {
      val decVer = DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor))

      val v1       = DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor - 1))
      val v2       = DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor + 1))
      val matcher1 = DecVerMatcher.range(v1, v2)

      val decVer2  = DecVer.withMajorMinor(DecVer.Major(major2), DecVer.Minor(minor2))
      val matcher2 = DecVerMatcher.comparison(DecVerComparison(op, decVer2))

      // format: off
      val versions = op match {
        case ComparisonOperator.Lt =>
          List(
            DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2)),
            DecVer.withMajorMinor(DecVer.Major(major2), DecVer.Minor(minor2 - 1)),
            DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2 - 1))
          )
        case ComparisonOperator.Le =>
          List(
            DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2)),
            DecVer.withMajorMinor(DecVer.Major(major2), DecVer.Minor(minor2 - 1)),
            DecVer.withMajorMinor(DecVer.Major(major2), DecVer.Minor(minor2)),
            DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2 - 1)),
            decVer2.copy()
          )
        case ComparisonOperator.Eql =>
          List(
            decVer2.copy()
          )
        case ComparisonOperator.Ne =>
          List(
            DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2)),
            DecVer.withMajorMinor(DecVer.Major(major2), DecVer.Minor(minor2 - 1)),
            DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2 - 1)),
            DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2)),
            DecVer.withMajorMinor(DecVer.Major(major2), DecVer.Minor(minor2 + 1)),
            DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2 + 1))
          )
        case ComparisonOperator.Gt =>
          List(
            DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2)),
            DecVer.withMajorMinor(DecVer.Major(major2), DecVer.Minor(minor2 + 1)),
            DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2 + 1))
          )
        case ComparisonOperator.Ge =>
          List(
            DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2)),
            DecVer.withMajorMinor(DecVer.Major(major2), DecVer.Minor(minor2 + 1)),
            DecVer.withMajorMinor(DecVer.Major(major2), DecVer.Minor(minor2)),
            DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2 + 1)),
            decVer2.copy()
          )
      }
      // format: on

      val decVerMatchers = DecVerMatchers.unsafeParse(s"${matcher1.render} || ${matcher2.render}")

      println(
        s"""# Range || Comparison:
           |-   matchers: ${decVerMatchers.render}
           |-  decVer: ${decVer.render}
           |- decVers: ${versions.map(_.render).mkString("[\n>    - ", "\n>    - ", "\n>    ]")}
           |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(decVerMatchers.matches(decVer))
            .log(
              s""" Range || Comparison - range test failed
                 |>  matchers: ${decVerMatchers.render}
                 |> decVer: ${decVer.render}
                 |""".stripMargin
            ),
          Result
            .assert(versions.forall(v => decVerMatchers.matches(v)))
            .log(
              s""" Range || Comparison - comparison test failed
                 |>   matchers: ${decVerMatchers.render}
                 |> decVers: ${versions.map(_.render).mkString("[\n>    - ", "\n>    - ", "\n>    ]")}
                 |""".stripMargin
            )
        )
      )
    }

    def testDecVerMatchersComparisonAndComparisonMatchesValid: Property = for {
      v1V2DecVer <- Gens
                      .genRangedDecVerComparison(
                        Range.linear(11, 30),
                        Range.linear(11, 50),
                      )
                      .log("(v1, v2, decVer)")
      (v1, v2, decVer) = v1V2DecVer
    } yield {
      val decVerMatchers = DecVerMatchers.unsafeParse(s"${v1.render} ${v2.render}")

      println(
        s"""# Comparison and Comparison
           |-  matchers: ${decVerMatchers.render}
           |- decVer: ${decVer.render}
           |""".stripMargin
      )

      Result
        .assert(decVerMatchers.matches(decVer))
        .log(
          s""" Comparison and Comparison - failed
             |>  matchers: ${decVerMatchers.render}
             |> decVer: ${decVer.render}
             |""".stripMargin
        )
    }

    def testDecVerMatchersRangeOrComparisonAndComparisonMatchesValid: Property = for {
      rangeMatcherDecVerInRange <- Gens
                                     .genDecVerMatcherRangeAndDecVerInRange(
                                       majorRange = Range.linear(11, 30),
                                       minorRange = Range.linear(11, 50),
                                     )
                                     .log("(rangeMatcher, decVerInRange)")
      (rangeMatcher, decVerInRange) = rangeMatcherDecVerInRange
      v1V2DecVer <- Gens
                      .genRangedDecVerComparison(
                        majorRange = Range.linear(31, 100),
                        minorRange = Range.linear(51, 100),
                      )
                      .log("(v1, v2, decVer)")
      (v1, v2, decVer) = v1V2DecVer
    } yield {
      val decVerMatchers = DecVerMatchers.unsafeParse(s"${rangeMatcher.render} || ${v1.render} ${v2.render}")

      println(
        s"""# Range || Comparison and Comparison
           |-         matchers: ${decVerMatchers.render}
           |- decVerInRange: ${decVerInRange.render}
           |-  decVerInComp: ${decVer.render}
           |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(decVerMatchers.matches(decVerInRange))
            .log(
              s""" Range || Comparison and Comparison - testing range failed
                 |>         matchers: ${decVerMatchers.render}
                 |> decVerInRange: ${decVerInRange.render}
                 |""".stripMargin
            ),
          Result
            .assert(decVerMatchers.matches(decVer))
            .log(
              s""" Range || Comparison and Comparison - testing (comparison and comparison) failed
                 |>        matchers: ${decVerMatchers.render}
                 |> decVerInComp: ${decVer.render}
                 |""".stripMargin
            )
        )
      )
    }
  }

}
