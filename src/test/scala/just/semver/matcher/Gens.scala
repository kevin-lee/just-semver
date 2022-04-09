package just.semver.matcher

import hedgehog._
import just.semver.expr.ComparisonOperator
import just.semver.{SemVer, Gens => SemVerGens}

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

  def genSemVerMatcherRange(
    majorRange: Range[Int],
    minorRange: Range[Int],
    patchRange: Range[Int]
  ): Gen[SemVerMatcher] = genSemVerMatcherRangeAndSemverInRange(
    majorRange,
    minorRange,
    patchRange
  ).map { case (matcher, _) => matcher }

  def genSemVerMatcherRangeAndSemverInRange(
    majorRange: Range[Int],
    minorRange: Range[Int],
    patchRange: Range[Int]
  ): Gen[(SemVerMatcher, SemVer)] = for {
    v1  <- SemVerGens.genSemVerWithRange(majorRange, minorRange, patchRange)
    one <- Gen.element1(1, 2, 4)
    (m, n, p) = ((4 & one) >> 2, (2 & one) >> 1, 1 & one)
    v2     <- SemVerGens.genSemVerWithRange(
                Range.linear(v1.major.value + m, v1.major.value + 100),
                Range.linear(v1.minor.value + n, v1.minor.value + 100),
                Range.linear(v1.patch.value + p, v1.patch.value + 100)
              )
    semVer <- SemVerGens.genSemVerWithRange(
                Range.linear(v1.major.value, v2.major.value),
                Range.linear(v1.minor.value, v2.minor.value),
                Range.linear(v1.patch.value, v2.patch.value)
              )
  } yield (SemVerMatcher.range(v1, v2), v1.copy(major = semVer.major, minor = semVer.minor, patch = semVer.patch))

  def genSemVerMatcherComparison(
    majorRange: Range[Int],
    minorRange: Range[Int],
    patchRange: Range[Int]
  ): Gen[SemVerMatcher] = for {
    op     <- genComparisonOperator
    semVer <- SemVerGens.genSemVerWithRange(
                majorRange = majorRange,
                minorRange = minorRange,
                patchRange = patchRange
              )
  } yield SemVerMatcher.comparison(SemVerComparison(op, semVer))

  def genSemVerMatcher: Gen[SemVerMatcher] =
    Gen.frequency1(
      20 -> genSemVerMatcherRange(
        Range.linear(0, 100),
        Range.linear(0, 100),
        Range.linear(0, 1000)
      ),
      80 -> genSemVerMatcherComparison(
        majorRange = Range.linear(0, 100),
        minorRange = Range.linear(0, 500),
        patchRange = Range.linear(0, 1000)
      )
    )

  def genRangedSemVerComparison(
    majorRange: Range[Int],
    minorRange: Range[Int],
    patchRange: Range[Int]
  ): Gen[(SemVerComparison, SemVerComparison, SemVer)] = for {
    v1SemVer  <- SemVerGens.genSemVerWithRange(majorRange, minorRange, patchRange)
    m         <- Gen.int(Range.linear(1, 10))
    n         <- Gen.int(Range.linear(1, 10))
    p         <- Gen.int(Range.linear(1, 10))
    inclusive <- Gen.boolean
  } yield {
    inclusive match {
      case false =>
        val semVer = v1SemVer.copy(
          major = SemVer.Major(v1SemVer.major.value + m),
          minor = SemVer.Minor(v1SemVer.minor.value + n),
          patch = SemVer.Patch(v1SemVer.patch.value + p)
        )
        val v1     = SemVerComparison(
          ComparisonOperator.gt,
          v1SemVer
        )
        val v2     = SemVerComparison(
          ComparisonOperator.lt,
          v1.semVer
            .copy(
              major = SemVer.Major(semVer.major.value + m),
              minor = SemVer.Minor(semVer.minor.value + n),
              patch = SemVer.Patch(semVer.patch.value + p)
            )
        )
        (v1, v2, semVer)
      case true =>
        val semVer = v1SemVer.copy(
          major = SemVer.Major(v1SemVer.major.value + m),
          minor = SemVer.Minor(v1SemVer.minor.value + n),
          patch = SemVer.Patch(v1SemVer.patch.value + p)
        )
        val v1     = SemVerComparison(
          ComparisonOperator.ge,
          v1SemVer
        )
        val v2     = SemVerComparison(
          ComparisonOperator.le,
          v1.semVer
            .copy(
              major = SemVer.Major(semVer.major.value + m),
              minor = SemVer.Minor(semVer.minor.value + n),
              patch = SemVer.Patch(semVer.patch.value + p)
            )
        )
        (v1, v2, semVer)
    }

  }
}
