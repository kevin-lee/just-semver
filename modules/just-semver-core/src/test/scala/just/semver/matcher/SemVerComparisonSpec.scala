package just.semver.matcher

import hedgehog._
import hedgehog.runner._
import just.semver.{Gens => SemVerGens}

import scala.util.{Failure, Success, Try}

/** @author Kevin Lee
  * @since 2022-04-07
  */
object SemVerComparisonSpec extends Properties {
  override def tests: List[Test] = List(
    property("test SemVerComparison.parse(Valid)", testSemVerComparisonParseValid),
    property(
      "test SemVerComparison.parse(Invalid comparison operator)",
      testSemVerComparisonParseInvalidComparisonOperator
    ),
    property(
      "test SemVerComparison.parse(Invalid SemVer)",
      testSemVerComparisonParseInvalidSemVer
    ),
    property("test SemVerComparison.unsafeParse(Valid)", testSemVerComparisonUnsafeParseValid),
    property(
      "test SemVerComparison.unsafeParse(Invalid comparison operator)",
      testSemVerComparisonUnsafeParseInvalidComparisonOperator
    ),
    property(
      "test SemVerComparison.unsafeParse(Invalid SemVer)",
      testSemVerComparisonUnsafeParseInvalidSemVer
    )
  )

  def testSemVerComparisonParseValid: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    semVer <- SemVerGens.genSemVer.log("semVer")
  } yield {
    val input = s"${op.render}${semVer.render}"
    SemVerComparison.parse(input) match {
      case Right(actual) =>
        actual.comparisonOperator ==== op and actual.semVer ==== semVer
      case Left(err) =>
        Result.failure.log(s"Parsed SemVerComparison expected but got error instead: ${err.render}")
    }
  }

  def testSemVerComparisonParseInvalidComparisonOperator: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    semVer <- SemVerGens.genSemVer.log("semVer")
  } yield {
    val input = s"${op.render}${op.render}${op.render}${semVer.render}"
    SemVerComparison.parse(input) match {
      case Right(actual) =>
        Result
          .failure
          .log(s"SemVerComparisonParseError expected but got parsed SemVerComparison: ${actual.render}")
      case Left(actual) =>
        val expectedMessage = s"Failed to parse operator from $input"
        actual matchPattern {
          case SemVerComparison.ParseError(`expectedMessage`, _: String, None) =>
        }
    }
  }

  def testSemVerComparisonParseInvalidSemVer: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    semVer <- SemVerGens.genSemVer.log("semVer")
  } yield {
    val input = s"${op.render}${semVer.renderMajorMinorPatch}.${semVer.render}"
    SemVerComparison.parse(input) match {
      case Right(actual) =>
        Result
          .failure
          .log(s"SemVerComparisonParseError expected but got parsed SemVerComparison: ${actual.render}")
      case Left(actual) =>
        val opRendered      = op.render
        val expectedMessage = s"Parsing operator succeeded but failed to parse SemVer from $input"
        (actual matchPattern {
          case SemVerComparison.ParseError(
                `expectedMessage`,
                _: String,
                Some(`opRendered`)
              ) =>
        }).log(s"${actual.render} doesn't match the given pattern")
    }
  }

  def testSemVerComparisonUnsafeParseValid: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    semVer <- SemVerGens.genSemVer.log("semVer")
  } yield {
    val input  = s"${op.render}${semVer.render}"
    val actual = SemVerComparison.unsafeParse(input)
    actual.comparisonOperator ==== op and actual.semVer ==== semVer
  }

  def testSemVerComparisonUnsafeParseInvalidComparisonOperator: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    semVer <- SemVerGens.genSemVer.log("semVer")
  } yield {
    val input = s"${op.render}${op.render}${op.render}${semVer.render}"
    Try(SemVerComparison.unsafeParse(input)) match {
      case Success(actual) =>
        Result
          .failure
          .log(s"SemVerComparisonParseError expected but got parsed SemVerComparison: ${actual.render}")
      case Failure(actual) =>
        val errorMessage = actual.getMessage
        Result.all(
          List(
            Result.diff(errorMessage, s"Failed to parse operator from $input")(_.contains(_)),
            Result.diff(errorMessage, s"Success:)")(_.endsWith(_))
          )
        )
    }
  }

  def testSemVerComparisonUnsafeParseInvalidSemVer: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    semVer <- SemVerGens.genSemVer.log("semVer")
  } yield {
    val input = s"${op.render}${semVer.renderMajorMinorPatch}.${semVer.render}"
    Try(SemVerComparison.unsafeParse(input)) match {
      case Success(actual) =>
        Result
          .failure
          .log(s"SemVerComparisonParseError expected but got parsed SemVerComparison: ${actual.render}")
      case Failure(actual) =>
        val opRendered   = op.render
        val errorMessage = actual.getMessage
        Result.all(
          List(
            Result.diff(errorMessage, s"Parsing operator succeeded but failed to parse SemVer from $input")(
              _.contains(_)
            ),
            Result.diff(errorMessage, s"Success: $opRendered)")(_.endsWith(_))
          )
        )
    }
  }

}
