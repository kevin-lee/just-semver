package just.decver.matcher

import hedgehog._
import hedgehog.runner._
import just.decver.DecVerExtGens

import scala.util.{Failure, Success, Try}

/** @author Kevin Lee
  * @since 2022-04-07
  */
object DecVerExtComparisonSpec extends Properties {
  override def tests: List[Test] = List(
    property("test DecVerExtComparison.parse(Valid)", testDecVerExtComparisonParseValid),
    property(
      "test DecVerExtComparison.parse(Invalid comparison operator)",
      testDecVerExtComparisonParseInvalidComparisonOperator
    ),
    property(
      "test DecVerExtComparison.parse(Invalid DecVerExt)",
      testDecVerExtComparisonParseInvalidDecVerExt
    ),
    property("test DecVerExtComparison.unsafeParse(Valid)", testDecVerExtComparisonUnsafeParseValid),
    property(
      "test DecVerExtComparison.unsafeParse(Invalid comparison operator)",
      testDecVerExtComparisonUnsafeParseInvalidComparisonOperator
    ),
    property(
      "test DecVerExtComparison.unsafeParse(Invalid DecVerExt)",
      testDecVerExtComparisonUnsafeParseInvalidDecVerExt
    )
  )

  def testDecVerExtComparisonParseValid: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    decVerExt <- DecVerExtGens.genDecVerExt.log("decVerExt")
  } yield {
    val input = s"${op.render}${decVerExt.render}"
    DecVerExtComparison.parse(input) match {
      case Right(actual) =>
        actual.comparisonOperator ==== op and actual.decVerExt ==== decVerExt
      case Left(err) =>
        Result.failure.log(s"Parsed DecVerExtComparison expected but got error instead: ${err.render}")
    }
  }

  def testDecVerExtComparisonParseInvalidComparisonOperator: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    decVerExt <- DecVerExtGens.genDecVerExt.log("decVerExt")
  } yield {
    val input = s"${op.render}${op.render}${op.render}${decVerExt.render}"
    DecVerExtComparison.parse(input) match {
      case Right(actual) =>
        Result
          .failure
          .log(s"DecVerExtComparisonParseError expected but got parsed DecVerExtComparison: ${actual.render}")
      case Left(actual) =>
        val expectedMessage = s"Failed to parse operator from $input"
        actual matchPattern {
          case DecVerExtComparison.ParseError(`expectedMessage`, _: String, None) =>
        }
    }
  }

  def testDecVerExtComparisonParseInvalidDecVerExt: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    decVerExt <- DecVerExtGens.genDecVerExt.log("decVerExt")
  } yield {
    val input = s"${op.render}${decVerExt.renderMajorMinor}.${decVerExt.render}"
    DecVerExtComparison.parse(input) match {
      case Right(actual) =>
        Result
          .failure
          .log(s"DecVerExtComparisonParseError expected but got parsed DecVerExtComparison: ${actual.render}")
      case Left(actual) =>
        val opRendered      = op.render
        val expectedMessage = s"Parsing operator succeeded but failed to parse DecVerExt from $input"
        (actual matchPattern {
          case DecVerExtComparison.ParseError(
                `expectedMessage`,
                _: String,
                Some(`opRendered`)
              ) =>
        }).log(s"${actual.render} doesn't match the given pattern")
    }
  }

  def testDecVerExtComparisonUnsafeParseValid: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    decVerExt <- DecVerExtGens.genDecVerExt.log("decVerExt")
  } yield {
    val input  = s"${op.render}${decVerExt.render}"
    val actual = DecVerExtComparison.unsafeParse(input)
    actual.comparisonOperator ==== op and actual.decVerExt ==== decVerExt
  }

  def testDecVerExtComparisonUnsafeParseInvalidComparisonOperator: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    decVerExt <- DecVerExtGens.genDecVerExt.log("decVerExt")
  } yield {
    val input = s"${op.render}${op.render}${op.render}${decVerExt.render}"
    Try(DecVerExtComparison.unsafeParse(input)) match {
      case Success(actual) =>
        Result
          .failure
          .log(s"DecVerExtComparisonParseError expected but got parsed DecVerExtComparison: ${actual.render}")
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

  def testDecVerExtComparisonUnsafeParseInvalidDecVerExt: Property = for {
    op     <- Gens.genComparisonOperator.log("op")
    decVerExt <- DecVerExtGens.genDecVerExt.log("decVerExt")
  } yield {
    val input = s"${op.render}${decVerExt.renderMajorMinor}.${decVerExt.render}"
    Try(DecVerExtComparison.unsafeParse(input)) match {
      case Success(actual) =>
        Result
          .failure
          .log(s"DecVerExtComparisonParseError expected but got parsed DecVerExtComparison: ${actual.render}")
      case Failure(actual) =>
        val opRendered   = op.render
        val errorMessage = actual.getMessage
        Result.all(
          List(
            Result.diff(errorMessage, s"Parsing operator succeeded but failed to parse DecVerExt from $input")(
              _.contains(_)
            ),
            Result.diff(errorMessage, s"Success: $opRendered)")(_.endsWith(_))
          )
        )
    }
  }

}
