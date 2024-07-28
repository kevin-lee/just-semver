package just.decver.matcher

import hedgehog._
import just.decver.{DecVerExt, DecVerExtGens}
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

  def genDecVerExtMatcherRange(
    majorRange: Range[Int],
    minorRange: Range[Int],
  ): Gen[DecVerExtMatcher] = genDecVerExtMatcherRangeAndDecVerInRange(
    majorRange,
    minorRange,
  ).map { case (matcher, _) => matcher }

  def genDecVerExtMatcherRangeAndDecVerInRange(
    majorRange: Range[Int],
    minorRange: Range[Int],
  ): Gen[(DecVerExtMatcher, DecVerExt)] = for {
    v1  <- DecVerExtGens.genDecVerExtWithRange(majorRange, minorRange)
    one <- Gen.element1(1, 2, 4)
    (m, n, p) = ((4 & one) >> 2, (2 & one) >> 1, 1 & one)
    v2     <- DecVerExtGens.genDecVerExtWithRange(
                Range.linear(v1.major.value + m, v1.major.value + 100),
                Range.linear(v1.minor.value + n, v1.minor.value + 100),
              )
    semVer <- DecVerExtGens.genDecVerExtWithRange(
                Range.linear(v1.major.value, v2.major.value),
                Range.linear(v1.minor.value, v2.minor.value),
              )
  } yield (DecVerExtMatcher.range(v1, v2), v1.copy(major = semVer.major, minor = semVer.minor))

  def genDecVerExtMatcherComparison(
    majorRange: Range[Int],
    minorRange: Range[Int],
  ): Gen[DecVerExtMatcher] = for {
    op     <- genComparisonOperator
    semVer <- DecVerExtGens.genDecVerExtWithRange(
                majorRange = majorRange,
                minorRange = minorRange,
              )
  } yield DecVerExtMatcher.comparison(DecVerExtComparison(op, semVer))

  def genDecVerExtMatcher: Gen[DecVerExtMatcher] =
    Gen.frequency1(
      20 -> genDecVerExtMatcherRange(
        Range.linear(0, 100),
        Range.linear(0, 100),
      ),
      80 -> genDecVerExtMatcherComparison(
        majorRange = Range.linear(0, 100),
        minorRange = Range.linear(0, 500),
      )
    )

  def genRangedDecVerExtComparison(
    majorRange: Range[Int],
    minorRange: Range[Int],
  ): Gen[(DecVerExtComparison, DecVerExtComparison, DecVerExt)] = for {
    v1DecVerExt <- DecVerExtGens.genDecVerExtWithRange(majorRange, minorRange)
    m           <- Gen.int(Range.linear(1, 10))
    n           <- Gen.int(Range.linear(1, 10))
    inclusive   <- Gen.boolean
  } yield {
    inclusive match {
      case false =>
        val semVer = v1DecVerExt.copy(
          major = DecVerExt.Major(v1DecVerExt.major.value + m),
          minor = DecVerExt.Minor(v1DecVerExt.minor.value + n),
        )
        val v1     = DecVerExtComparison(
          ComparisonOperator.gt,
          v1DecVerExt
        )
        val v2     = DecVerExtComparison(
          ComparisonOperator.lt,
          v1.decVerExt
            .copy(
              major = DecVerExt.Major(semVer.major.value + m),
              minor = DecVerExt.Minor(semVer.minor.value + n),
            )
        )
        (v1, v2, semVer)
      case true =>
        val semVer = v1DecVerExt.copy(
          major = DecVerExt.Major(v1DecVerExt.major.value + m),
          minor = DecVerExt.Minor(v1DecVerExt.minor.value + n),
        )
        val v1     = DecVerExtComparison(
          ComparisonOperator.ge,
          v1DecVerExt
        )
        val v2     = DecVerExtComparison(
          ComparisonOperator.le,
          v1.decVerExt
            .copy(
              major = DecVerExt.Major(semVer.major.value + m),
              minor = DecVerExt.Minor(semVer.minor.value + n),
            )
        )
        (v1, v2, semVer)
    }

  }
}
