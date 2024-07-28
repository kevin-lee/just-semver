package just.decver.matcher

import hedgehog._
import hedgehog.runner._
import just.Common._
import just.decver.DecVerExt
import just.semver.expr.ComparisonOperator

import scala.util.Try

/** @author Kevin Lee
  * @since 2022-04-08
  */
object DecVerExtMatchersSpec extends Properties {
  override def tests: List[Test] = List(
    property("test DecVerExtMatchers.parse(Valid)", testDecVerExtMatchersParseValid),
    property("test DecVerExtMatchers.parse(Invalid)", testDecVerExtMatchersParseInvalid),
    property("test DecVerExtMatchers.unsafeParse(Valid)", testDecVerExtMatchersParseValid),
    property("test DecVerExtMatchers.unsafeParse(Invalid)", testDecVerExtMatchersUnsafeParseInvalid)
  ) ++ List(
    example(
      "test  Example-1 DecVerExtMatchers(1.0 - 2.0).matches(1.0) should return true",
      MatchesSpec.testExample1
    ),
    example(
      "test  Example-2 DecVerExtMatchers(1.0 - 2.0).matches(2.0) should return true",
      MatchesSpec.testExample2
    ),
    example(
      "test  Example-3 DecVerExtMatchers(1.0 - 2.0).matches(1.1) should return true",
      MatchesSpec.testExample3
    ),
    example(
      "test  Example-4 DecVerExtMatchers(1.0 - 2.0).matches(1.999) should return true",
      MatchesSpec.testExample4
    ),
    example(
      "test  Example-5 DecVerExtMatchers(>1.0 <2.0).matches(1.0) should return false",
      MatchesSpec.testExample5
    ),
    example(
      "test  Example-6 DecVerExtMatchers(>1.0 <2.0).matches(2.0) should return false",
      MatchesSpec.testExample6
    ),
    example(
      "test  Example-7 DecVerExtMatchers(>1.0 <2.0).matches(1.1) should return true",
      MatchesSpec.testExample7
    ),
    example(
      "test  Example-8 DecVerExtMatchers(>1.0 <2.0).matches(1.999) should return true",
      MatchesSpec.testExample8
    ),
    example(
      "test  Example-9 DecVerExtMatchers(>=1.0 <=2.0).matches(1.0) should return true",
      MatchesSpec.testExample9
    ),
    example(
      "test Example-10 DecVerExtMatchers(>=1.0 <=2.0).matches(2.0) should return true",
      MatchesSpec.testExample10
    ),
    example(
      "test Example-11 DecVerExtMatchers(>=1.0 <=2.0).matches(1.1) should return true",
      MatchesSpec.testExample11
    ),
    example(
      "test Example-12 DecVerExtMatchers(>=1.0 <=2.0).matches(1.999) should return true",
      MatchesSpec.testExample12
    ),
    property(
      "test DecVerExtMatchers(Range || Comparison).matches(Valid)",
      MatchesSpec.testDecVerExtMatchersRangeOrComparisonMatchesValid
    ),
    property(
      "test DecVerExtMatchers(Comparison and Comparison).matches(Valid)",
      MatchesSpec.testDecVerExtMatchersComparisonAndComparisonMatchesValid
    ),
    property(
      "test DecVerExtMatchers(Range || Comparison and Comparison).matches(Valid)",
      MatchesSpec.testDecVerExtMatchersRangeOrComparisonAndComparisonMatchesValid
    )
  )

  def testDecVerExtMatchersParseValid: Property = for {
    decVerExtMatchers <- Gens
                        .genDecVerExtMatcher
                        .list(Range.linear(1, 5))
                        .list(Range.linear(1, 3))
                        .map { listOfList =>
                          DecVerExtMatchers(
                            DecVerExtMatchers.Or(listOfList.map { decVerExtMatchers =>
                              DecVerExtMatchers.And(decVerExtMatchers)
                            })
                          )
                        }
                        .log("decVerExtMatchers")
  } yield {
    val input = decVerExtMatchers.render
    DecVerExtMatchers.parse(input) match {
      case Right(actual) =>
        val expected = decVerExtMatchers
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

  def testDecVerExtMatchersParseInvalid: Property = for {
    invalidDecVerExtMatchers <- Gen
                               .string(Gen.alpha, Range.linear(1, 10))
                               .log("invalidDecVerExtMatchers")
  } yield {
    DecVerExtMatchers.parse(invalidDecVerExtMatchers) match {
      case Right(actual) =>
        Result
          .failure
          .log(
            s"""Expected List[DecVerExtMatcher.ParseError] but got ${actual.render} instead}
               |input: $invalidDecVerExtMatchers
               |""".stripMargin
          )
      case Left(errs) =>
        Result.all(
          List(
            Result.assert(errs.allErrors.nonEmpty).log("DecVerExtMatchers.parse(invalid) failed but no errors found"),
            Result
              .assert(errs.allErrors.map(_.render).forall(s => s.contains("ParseError") && s.contains("at 0")))
              .log(s"""DecVerExtMatchers.parse(invalid) failed but doesn't have expected ParseError.
                  |> Errors: ${errs.allErrors.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
                  |""".stripMargin)
          )
        )
    }
  }

  def testDecVerExtMatchersUnsafeParseValid: Property = for {
    decVerExtMatchers <- Gens
                        .genDecVerExtMatcher
                        .list(Range.linear(1, 5))
                        .list(Range.linear(1, 3))
                        .map { listOfList =>
                          DecVerExtMatchers(
                            DecVerExtMatchers.Or(listOfList.map { decVerExtMatchers =>
                              DecVerExtMatchers.And(decVerExtMatchers)
                            })
                          )
                        }
                        .log("decVerExtMatchers")
  } yield {
    val input = decVerExtMatchers.render
    Try {
      DecVerExtMatchers.unsafeParse(input)
    } match {
      case scala.util.Success(actual) =>
        val expected = decVerExtMatchers
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

  def testDecVerExtMatchersUnsafeParseInvalid: Property = for {
    invalidDecVerExtMatchers <- Gen
                               .string(Gen.alpha, Range.linear(1, 10))
                               .log("invalidDecVerExtMatchers")
  } yield {
    Try {
      DecVerExtMatchers.unsafeParse(invalidDecVerExtMatchers)
    } match {
      case scala.util.Success(actual) =>
        Result
          .failure
          .log(
            s"""Expected List[DecVerExtMatcher.ParseError] but got ${actual.render} instead}
               |input: $invalidDecVerExtMatchers
               |""".stripMargin
          )
      case scala.util.Failure(errs) =>
        Result
          .assert(errs.getMessage.contains("ParseError") && errs.getMessage.contains("at 0"))
          .log(s"""DecVerExtMatchers.parse(invalid) failed but doesn't have expected ParseError.
                      |> Errors: ${errs.getMessage}
                      |""".stripMargin)

    }
  }

  object MatchesSpec {

    def testExample1: Result = {
      Result
        .assert(DecVerExtMatchers.unsafeParse("1.0 - 2.0").matches(DecVerExt.unsafeParse("1.0")))
        .log("DecVerExtMatchers(1.0 - 2.0).matches(1.0) failed")
    }

    def testExample2: Result = {
      Result
        .assert(DecVerExtMatchers.unsafeParse("1.0 - 2.0").matches(DecVerExt.unsafeParse("2.0")))
        .log("DecVerExtMatchers(1.0 - 2.0).matches(2.0) failed")
    }

    def testExample3: Result = {
      Result
        .assert(DecVerExtMatchers.unsafeParse("1.0 - 2.0").matches(DecVerExt.unsafeParse("1.1")))
        .log("DecVerExtMatchers(1.0 - 2.0).matches(1.1) failed")
    }

    def testExample4: Result = {
      Result
        .assert(DecVerExtMatchers.unsafeParse("1.0 - 2.0").matches(DecVerExt.unsafeParse("1.999")))
        .log("DecVerExtMatchers(1.0 - 2.0).matches(1.999) failed")
    }

    def testExample5: Result = {
      Result
        .diff(DecVerExtMatchers.unsafeParse(">1.0 <2.0").matches(DecVerExt.unsafeParse("1.0")), false)(_ === _)
        .log("DecVerExtMatchers(>1.0 <2.0).matches(1.0) failed")
    }

    def testExample6: Result = {
      Result
        .diff(DecVerExtMatchers.unsafeParse(">1.0 <2.0").matches(DecVerExt.unsafeParse("2.0")), false)(_ === _)
        .log("DecVerExtMatchers(>1.0 <2.0).matches(2.0) failed")
    }

    def testExample7: Result = {
      Result
        .assert(DecVerExtMatchers.unsafeParse(">1.0 <2.0").matches(DecVerExt.unsafeParse("1.1")))
        .log("DecVerExtMatchers(>1.0 <2.0).matches(1.1) failed")
    }

    def testExample8: Result = {
      Result
        .assert(DecVerExtMatchers.unsafeParse(">1.0 <2.0").matches(DecVerExt.unsafeParse("1.999")))
        .log("DecVerExtMatchers(>1.0 <2.0).matches(1.999) failed")
    }

    def testExample9: Result = {
      Result
        .assert(DecVerExtMatchers.unsafeParse(">=1.0 <=2.0").matches(DecVerExt.unsafeParse("1.0")))
        .log("DecVerExtMatchers(>=1.0 <=2.0).matches(1.0) failed")
    }

    def testExample10: Result = {
      Result
        .assert(DecVerExtMatchers.unsafeParse(">=1.0 <=2.0").matches(DecVerExt.unsafeParse("2.0")))
        .log("DecVerExtMatchers(>=1.0 <=2.0).matches(2.0) failed")
    }

    def testExample11: Result = {
      Result
        .assert(DecVerExtMatchers.unsafeParse(">=1.0 <=2.0").matches(DecVerExt.unsafeParse("1.1")))
        .log("DecVerExtMatchers(>=1.0 <=2.0).matches(1.1) failed")
    }

    def testExample12: Result = {
      Result
        .assert(DecVerExtMatchers.unsafeParse(">=1.0 <=2.0").matches(DecVerExt.unsafeParse("1.999")))
        .log("DecVerExtMatchers(>=1.0 <=2.0).matches(1.999) failed")
    }

    def testDecVerExtMatchersRangeOrComparisonMatchesValid: Property = for {
      major  <- Gen.int(Range.linear(5, 10)).log("major")
      minor  <- Gen.int(Range.linear(1, 10)).log("minor")
      major2 <- Gen.int(Range.linear(5, 10)).map(_ + major).log("major2")
      minor2 <- Gen.int(Range.linear(1, 10)).map(_ + minor).log("minor2")
      op     <- Gens.genComparisonOperator.log("op")
    } yield {
      val decVerExt = DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor))

      val v1       = DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor - 1))
      val v2       = DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor + 1))
      val matcher1 = DecVerExtMatcher.range(v1, v2)

      val decVerExt2  = DecVerExt.withMajorMinor(DecVerExt.Major(major2), DecVerExt.Minor(minor2))
      val matcher2 = DecVerExtMatcher.comparison(DecVerExtComparison(op, decVerExt2))

      // format: off
      val versions = op match {
        case ComparisonOperator.Lt =>
          List(
            DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2), DecVerExt.Minor(minor2 - 1)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2 - 1))
          )
        case ComparisonOperator.Le =>
          List(
            DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2), DecVerExt.Minor(minor2 - 1)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2), DecVerExt.Minor(minor2)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2 - 1)),
            decVerExt2.copy()
          )
        case ComparisonOperator.Eql =>
          List(
            decVerExt2.copy()
          )
        case ComparisonOperator.Ne =>
          List(
            DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2), DecVerExt.Minor(minor2 - 1)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2 - 1)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2), DecVerExt.Minor(minor2 + 1)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2 + 1))
          )
        case ComparisonOperator.Gt =>
          List(
            DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2), DecVerExt.Minor(minor2 + 1)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2 + 1))
          )
        case ComparisonOperator.Ge =>
          List(
            DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2), DecVerExt.Minor(minor2 + 1)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2), DecVerExt.Minor(minor2)),
            DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2 + 1)),
            decVerExt2.copy()
          )
      }
      // format: on

      val decVerExtMatchers = DecVerExtMatchers.unsafeParse(s"${matcher1.render} || ${matcher2.render}")

      println(
        s"""# Range || Comparison:
           |-   matchers: ${decVerExtMatchers.render}
           |-  decVerExt: ${decVerExt.render}
           |- decVerExts: ${versions.map(_.render).mkString("[\n>    - ", "\n>    - ", "\n>    ]")}
           |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(decVerExtMatchers.matches(decVerExt))
            .log(
              s""" Range || Comparison - range test failed
                 |>  matchers: ${decVerExtMatchers.render}
                 |> decVerExt: ${decVerExt.render}
                 |""".stripMargin
            ),
          Result
            .assert(versions.forall(v => decVerExtMatchers.matches(v)))
            .log(
              s""" Range || Comparison - comparison test failed
                 |>   matchers: ${decVerExtMatchers.render}
                 |> decVerExts: ${versions.map(_.render).mkString("[\n>    - ", "\n>    - ", "\n>    ]")}
                 |""".stripMargin
            )
        )
      )
    }

    def testDecVerExtMatchersComparisonAndComparisonMatchesValid: Property = for {
      v1V2DecVerExt <- Gens
                      .genRangedDecVerExtComparison(
                        Range.linear(11, 30),
                        Range.linear(11, 50),
                      )
                      .log("(v1, v2, decVerExt)")
      (v1, v2, decVerExt) = v1V2DecVerExt
    } yield {
      val decVerExtMatchers = DecVerExtMatchers.unsafeParse(s"${v1.render} ${v2.render}")

      println(
        s"""# Comparison and Comparison
           |-  matchers: ${decVerExtMatchers.render}
           |- decVerExt: ${decVerExt.render}
           |""".stripMargin
      )

      Result
        .assert(decVerExtMatchers.matches(decVerExt))
        .log(
          s""" Comparison and Comparison - failed
             |>  matchers: ${decVerExtMatchers.render}
             |> decVerExt: ${decVerExt.render}
             |""".stripMargin
        )
    }

    def testDecVerExtMatchersRangeOrComparisonAndComparisonMatchesValid: Property = for {
      rangeMatcherDecVerExtInRange <- Gens
                                     .genDecVerExtMatcherRangeAndDecVerInRange(
                                       majorRange = Range.linear(11, 30),
                                       minorRange = Range.linear(11, 50),
                                     )
                                     .log("(rangeMatcher, decVerExtInRange)")
      (rangeMatcher, decVerExtInRange) = rangeMatcherDecVerExtInRange
      v1V2DecVerExt <- Gens
                      .genRangedDecVerExtComparison(
                        majorRange = Range.linear(31, 100),
                        minorRange = Range.linear(51, 100),
                      )
                      .log("(v1, v2, decVerExt)")
      (v1, v2, decVerExt) = v1V2DecVerExt
    } yield {
      val decVerExtMatchers = DecVerExtMatchers.unsafeParse(s"${rangeMatcher.render} || ${v1.render} ${v2.render}")

      println(
        s"""# Range || Comparison and Comparison
           |-         matchers: ${decVerExtMatchers.render}
           |- decVerExtInRange: ${decVerExtInRange.render}
           |-  decVerExtInComp: ${decVerExt.render}
           |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(decVerExtMatchers.matches(decVerExtInRange))
            .log(
              s""" Range || Comparison and Comparison - testing range failed
                 |>         matchers: ${decVerExtMatchers.render}
                 |> decVerExtInRange: ${decVerExtInRange.render}
                 |""".stripMargin
            ),
          Result
            .assert(decVerExtMatchers.matches(decVerExt))
            .log(
              s""" Range || Comparison and Comparison - testing (comparison and comparison) failed
                 |>        matchers: ${decVerExtMatchers.render}
                 |> decVerExtInComp: ${decVerExt.render}
                 |""".stripMargin
            )
        )
      )
    }
  }

}
