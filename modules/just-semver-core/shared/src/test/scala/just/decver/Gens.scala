package just.decver

import hedgehog._
import just.semver.{Gens => SemVerGens}

/** @author Kevin Lee
  * @since 2022-06-10
  */
object Gens {
  def genMajor: Gen[DecVer.Major] =
    SemVerGens.genNonNegativeInt.map(DecVer.Major(_))

  def genMinor: Gen[DecVer.Minor] =
    SemVerGens.genNonNegativeInt.map(DecVer.Minor(_))

  def genMajorWithMax(max: Int): Gen[DecVer.Major] =
    SemVerGens.genNonNegativeIntWithMax(max).map(DecVer.Major(_))

  def genMinorWithMax(max: Int): Gen[DecVer.Minor] =
    SemVerGens.genNonNegativeIntWithMax(max).map(DecVer.Minor(_))

  def genDecVer: Gen[DecVer] =
    for {
      major <- genMajor
      minor <- genMinor
    } yield DecVer(major, minor)

  def genLessAndGreaterDecVerPair: Gen[(DecVer, DecVer)] =
    for {
      major1 <- Gens.genMajorWithMax(Int.MaxValue >> 1)
      minor1 <- Gens.genMinorWithMax(Int.MaxValue >> 1)
      diff   <- Gen.int(Range.linear(1, Int.MaxValue >> 1))
      choice <- Gen.element1(1, 2)
      (m, n) = ((choice & 2) >> 1, choice & 1)
    } yield (
      DecVer(major1, minor1),
      DecVer(DecVer.Major(major1.value + diff * m), DecVer.Minor(minor1.value + diff * n))
    )
}
