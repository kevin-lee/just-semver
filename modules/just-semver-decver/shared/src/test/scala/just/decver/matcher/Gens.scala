package just.decver.matcher

import hedgehog._
import just.decver.{DecVer, DecVerGens}
import just.semver.expr.ComparisonOperator

/** @author Kevin Lee
  * @since 2022-04-03
  */
object Gens {

  def genComparisonOperator: Gen[ComparisonOperator] =
    Gen.element1(
      ComparisonOperator.lt,
      ComparisonOperator.le,
      ComparisonOperator.eql,
      ComparisonOperator.ne,
      ComparisonOperator.gt,
      ComparisonOperator.ge
    )

  def genDecVerMatcherRange(
    majorRange: Range[Int],
    minorRange: Range[Int],
  ): Gen[DecVerMatcher] = genDecVerMatcherRangeAndDecVerInRange(
    majorRange,
    minorRange,
  ).map { case (matcher, _) => matcher }

  def genDecVerMatcherRangeAndDecVerInRange(
    majorRange: Range[Int],
    minorRange: Range[Int],
  ): Gen[(DecVerMatcher, DecVer)] = for {
    v1  <- DecVerGens.genDecVerWithRange(majorRange, minorRange)
    one <- Gen.element1(1, 2, 4)
    (m, n, p) = ((4 & one) >> 2, (2 & one) >> 1, 1 & one)
    v2     <- DecVerGens.genDecVerWithRange(
                Range.linear(v1.major.value + m, v1.major.value + 100),
                Range.linear(v1.minor.value + n, v1.minor.value + 100),
              )
    semVer <- DecVerGens.genDecVerWithRange(
                Range.linear(v1.major.value, v2.major.value),
                Range.linear(v1.minor.value, v2.minor.value),
              )
  } yield (DecVerMatcher.range(v1, v2), v1.copy(major = semVer.major, minor = semVer.minor))

  def genDecVerMatcherComparison(
    majorRange: Range[Int],
    minorRange: Range[Int],
  ): Gen[DecVerMatcher] = for {
    op     <- genComparisonOperator
    semVer <- DecVerGens.genDecVerWithRange(
                majorRange = majorRange,
                minorRange = minorRange,
              )
  } yield DecVerMatcher.comparison(DecVerComparison(op, semVer))

  def genDecVerMatcher: Gen[DecVerMatcher] =
    Gen.frequency1(
      20 -> genDecVerMatcherRange(
        Range.linear(0, 100),
        Range.linear(0, 100),
      ),
      80 -> genDecVerMatcherComparison(
        majorRange = Range.linear(0, 100),
        minorRange = Range.linear(0, 500),
      )
    )

  def genRangedDecVerComparison(
    majorRange: Range[Int],
    minorRange: Range[Int],
  ): Gen[(DecVerComparison, DecVerComparison, DecVer)] = for {
    v1DecVer <- DecVerGens.genDecVerWithRange(majorRange, minorRange)
    m           <- Gen.int(Range.linear(1, 10))
    n           <- Gen.int(Range.linear(1, 10))
    inclusive   <- Gen.boolean
  } yield {
    inclusive match {
      case false =>
        val semVer = v1DecVer.copy(
          major = DecVer.Major(v1DecVer.major.value + m),
          minor = DecVer.Minor(v1DecVer.minor.value + n),
        )
        val v1     = DecVerComparison(
          ComparisonOperator.gt,
          v1DecVer
        )
        val v2     = DecVerComparison(
          ComparisonOperator.lt,
          v1.decVer
            .copy(
              major = DecVer.Major(semVer.major.value + m),
              minor = DecVer.Minor(semVer.minor.value + n),
            )
        )
        (v1, v2, semVer)
      case true =>
        val semVer = v1DecVer.copy(
          major = DecVer.Major(v1DecVer.major.value + m),
          minor = DecVer.Minor(v1DecVer.minor.value + n),
        )
        val v1     = DecVerComparison(
          ComparisonOperator.ge,
          v1DecVer
        )
        val v2     = DecVerComparison(
          ComparisonOperator.le,
          v1.decVer
            .copy(
              major = DecVer.Major(semVer.major.value + m),
              minor = DecVer.Minor(semVer.minor.value + n),
            )
        )
        (v1, v2, semVer)
    }

  }
}
