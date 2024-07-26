package just.decver

import hedgehog._
import just.semver.{Gens => SemVerGens}

/** @author Kevin Lee
  * @since 2022-06-10
  */
object DecVerExtGens {
  def genMajor: Gen[DecVerExt.Major] =
    SemVerGens.genNonNegativeInt.map(DecVerExt.Major(_))

  def genMinor: Gen[DecVerExt.Minor] =
    SemVerGens.genNonNegativeInt.map(DecVerExt.Minor(_))

  def genMajorWithMax(max: Int): Gen[DecVerExt.Major] =
    SemVerGens.genNonNegativeIntWithMax(max).map(DecVerExt.Major(_))

  def genMinorWithMax(max: Int): Gen[DecVerExt.Minor] =
    SemVerGens.genNonNegativeIntWithMax(max).map(DecVerExt.Minor(_))

  def genDecVerExt: Gen[DecVerExt] =
    for {
      major <- genMajor
      minor <- genMinor
    } yield DecVerExt(major, minor, None, None)

  def genMinMaxDecVerExts: Gen[(DecVerExt, DecVerExt)] =
    for {
      (major1, major2) <- SemVerGens.genMinMaxNonNegInts.map(SemVerGens.pairFromIntsTo(DecVerExt.Major.apply))
      (minor1, minor2) <- SemVerGens.genMinMaxNonNegInts.map(SemVerGens.pairFromIntsTo(DecVerExt.Minor.apply))
    } yield (DecVerExt(major1, minor1, None, None), DecVerExt(major2, minor2, None, None))

  def genLessAndGreaterDecVerExtPair: Gen[(DecVerExt, DecVerExt)] =
    for {
      major1 <- DecVerExtGens.genMajorWithMax(Int.MaxValue >> 1)
      minor1 <- DecVerExtGens.genMinorWithMax(Int.MaxValue >> 1)
      diff   <- Gen.int(Range.linear(1, Int.MaxValue >> 1))
      choice <- Gen.element1(1, 2)
      (m, n) = ((choice & 2) >> 1, choice & 1)
    } yield (
      DecVerExt(major1, minor1, None, None),
      DecVerExt(DecVerExt.Major(major1.value + diff * m), DecVerExt.Minor(minor1.value + diff * n), None, None)
    )
}
