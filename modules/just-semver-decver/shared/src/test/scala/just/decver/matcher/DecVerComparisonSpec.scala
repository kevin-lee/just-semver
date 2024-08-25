package just.decver.matcher

import hedgehog._
import hedgehog.runner._
import just.decver.DecVerGens

import scala.util.{Failure, Success, Try}

/** @author Kevin Lee
  * @since 2022-04-07
  */
object DecVerComparisonSpec extends Properties {
  override def tests: List[Test] = List(
    property("test DecVerComparison.parse(Valid)", testDecVerComparisonParseValid),
    property(
      "test DecVerComparison.parse(Invalid comparison operator)",
      testDecVerComparisonParseInvalidComparisonOperator
    ),
    property(
      "test DecVerComparison.parse(Invalid DecVer)",
      testDecVerComparisonParseInvalidDecVer
    ),
    property("test DecVerComparison.unsafeParse(Valid)", testDecVerComparisonUnsafeParseValid),
    property(
      "test DecVerComparison.unsafeParse(Invalid comparison operator)",
      testDecVerComparisonUnsafeParseInvalidComparisonOperator
    ),
    property(
      "test DecVerComparison.unsafeParse(Invalid DecVer)",
      testDecVerComparisonUnsafeParseInvalidDecVer
    )
  )

  def testDecVerComparisonParseValid: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    decVer <- DecVerGens.genDecVer.log("decVer")
  } yield {
    val input = s"${op.render}${decVer.render}"
    DecVerComparison.parse(input) match {
      case Right(actual) =>
        actual.comparisonOperator ==== op and actual.decVer ==== decVer
      case Left(err) =>
        Result.failure.log(s"Parsed DecVerComparison expected but got error instead: ${err.render}")
    }
  }

  def testDecVerComparisonParseInvalidComparisonOperator: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    decVer <- DecVerGens.genDecVer.log("decVer")
  } yield {
    val input = s"${op.render}${op.render}${op.render}${decVer.render}"
    DecVerComparison.parse(input) match {
      case Right(actual) =>
        Result
          .failure
          .log(s"DecVerComparisonParseError expected but got parsed DecVerComparison: ${actual.render}")
      case Left(actual) =>
        val expectedMessage = s"Failed to parse operator from $input"
        actual matchPattern {
          case DecVerComparison.ParseError(`expectedMessage`, _: String, None) =>
        }
    }
  }

  def testDecVerComparisonParseInvalidDecVer: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    decVer <- DecVerGens.genDecVer.log("decVer")
  } yield {
    val input = s"${op.render}${decVer.renderMajorMinor}.${decVer.render}"
    DecVerComparison.parse(input) match {
      case Right(actual) =>
        Result
          .failure
          .log(s"DecVerComparisonParseError expected but got parsed DecVerComparison: ${actual.render}")
      case Left(actual) =>
        val opRendered      = op.render
        val expectedMessage = s"Parsing operator succeeded but failed to parse DecVer from $input"
        (actual matchPattern {
          case DecVerComparison.ParseError(
                `expectedMessage`,
                _: String,
                Some(`opRendered`)
              ) =>
        }).log(s"${actual.render} doesn't match the given pattern")
    }
  }

  def testDecVerComparisonUnsafeParseValid: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    decVer <- DecVerGens.genDecVer.log("decVer")
  } yield {
    val input  = s"${op.render}${decVer.render}"
    val actual = DecVerComparison.unsafeParse(input)
    actual.comparisonOperator ==== op and actual.decVer ==== decVer
  }

  def testDecVerComparisonUnsafeParseInvalidComparisonOperator: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    decVer <- DecVerGens.genDecVer.log("decVer")
  } yield {
    val input = s"${op.render}${op.render}${op.render}${decVer.render}"
    Try(DecVerComparison.unsafeParse(input)) match {
      case Success(actual) =>
        Result
          .failure
          .log(s"DecVerComparisonParseError expected but got parsed DecVerComparison: ${actual.render}")
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

  def testDecVerComparisonUnsafeParseInvalidDecVer: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    decVer <- DecVerGens.genDecVer.log("decVer")
  } yield {
    val input = s"${op.render}${decVer.renderMajorMinor}.${decVer.render}"
    Try(DecVerComparison.unsafeParse(input)) match {
      case Success(actual) =>
        Result
          .failure
          .log(s"DecVerComparisonParseError expected but got parsed DecVerComparison: ${actual.render}")
      case Failure(actual) =>
        val opRendered   = op.render
        val errorMessage = actual.getMessage
        Result.all(
          List(
            Result.diff(errorMessage, s"Parsing operator succeeded but failed to parse DecVer from $input")(
              _.contains(_)
            ),
            Result.diff(errorMessage, s"Success: $opRendered)")(_.endsWith(_))
          )
        )
    }
  }

}
