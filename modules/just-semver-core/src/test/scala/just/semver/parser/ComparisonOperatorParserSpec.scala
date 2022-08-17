package just.semver.parser

import hedgehog._
import hedgehog.runner._
import just.semver.matcher.Gens

/** @author Kevin Lee
  * @since 2022-04-03
  */
object ComparisonOperatorParserSpec extends Properties {
  override def tests: List[Test] = List(
    property("test ComparisonOperatorParser.parse(valid)", testComparisonOperatorParserValidCase),
    property("test ComparisonOperatorParser.parse(invalid)", testComparisonOperatorParserInvalidCase),
    property(
      "test ComparisonOperatorParser.parse(invalid with only valid chars)",
      testComparisonOperatorParserInvalidWithValidCharsCase
    )
  )

  def testComparisonOperatorParserValidCase: Property = for {
    comparisonOperator <- Gens.genComparisonOperator.log("comparisonOperator")

    rest <- Gen.string(Gen.alphaNum, Range.linear(1, 10)).log("rest")
  } yield {
    val input = s"${comparisonOperator.render}$rest"

    ComparisonOperatorParser.parse(input) ==== Right((comparisonOperator, rest))
  }

  def testComparisonOperatorParserInvalidCase: Property = for {
    rest <- Gen.string(Gen.alphaNum, Range.linear(1, 10)).log("rest")
  } yield {
    val input = rest

    ComparisonOperatorParser.parse(input) ==== Left(ParserError(s"Error at 0", None, Some(rest)))
  }

  def testComparisonOperatorParserInvalidWithValidCharsCase: Property = for {
    comparisonOperator1 <- Gens.genComparisonOperator.log("comparisonOperator1")
    comparisonOperator2 <- Gens.genComparisonOperator.log("comparisonOperator2")
    comparisonOperator3 <- Gens.genComparisonOperator.log("comparisonOperator3")

    rest <- Gen.string(Gen.alphaNum, Range.linear(1, 10)).log("rest")
  } yield {
    val input = s"${comparisonOperator1.render}${comparisonOperator2.render}${comparisonOperator3.render}$rest"

    ComparisonOperatorParser.parse(input) ==== Left(
      ParserError(
        s"Parsed successfully but failed to create ComparisonOperator: Error: Unknown or invalid operator",
        Some(s"${comparisonOperator1.render}${comparisonOperator2.render}${comparisonOperator3.render}"),
        Some(rest)
      )
    )
  }
}
