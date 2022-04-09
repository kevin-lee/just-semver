package just.semver.parser

import just.semver.{Compat, SemVer, Gens => SemVerGens}
import hedgehog._
import hedgehog.runner._
import just.semver.SemVer._
import just.semver.expr.ComparisonOperator
import just.semver.matcher.Gens

/** @author Kevin Lee
  * @since 2022-04-06
  */
object ParserSpec extends Properties with Compat {
  override def tests: List[Test] = List(
    property("test", testParser)
  )

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testParser: Property = for {
    op <- Gens.genComparisonOperator.log("op")
    v  <- SemVerGens.genSemVer.log("v")
  } yield {
    Parser
      .charsIn(">!=<")
      .parse(
        s"${op.render}${v.render}"
      )
      .left
      .map(_.render)
      .flatMap {
        case (rest, op) =>
          ComparisonOperator
            .parse(op)
            .flatMap { operator =>
              SemVer
                .parse(rest)
                .map { version =>
                  (operator, version)
                }
                .left
                .map(_.render)
            }
      } match {
      case Right((actualOp, actualV)) =>
        actualOp ==== op and actualV ==== v

      case Left(err) =>
        Result.failure.log(s"Parse failed with error: $err")
    }
  }
}
