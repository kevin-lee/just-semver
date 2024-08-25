package just.decver

import hedgehog._
import just.semver.AdditionalInfo.{BuildMetaInfo, PreRelease}
import just.semver.{CommonGens, Gens => SemVerGens}

/** @author Kevin Lee
  * @since 2022-06-10
  */
object DecVerGens {
  def genMajor: Gen[DecVer.Major] =
    SemVerGens.genNonNegativeInt.map(DecVer.Major(_))

  def genMinor: Gen[DecVer.Minor] =
    SemVerGens.genNonNegativeInt.map(DecVer.Minor(_))

  def genMajorWithMax(max: Int): Gen[DecVer.Major] =
    SemVerGens.genNonNegativeIntWithMax(max).map(DecVer.Major(_))

  def genMinorWithMax(max: Int): Gen[DecVer.Minor] =
    SemVerGens.genNonNegativeIntWithMax(max).map(DecVer.Minor(_))

  def genMajorWithRange(range: Range[Int]): Gen[DecVer.Major] =
    CommonGens.genVersionNumberWithRange(range).map(DecVer.Major(_))

  def genMinorWithRange(range: Range[Int]): Gen[DecVer.Minor] =
    CommonGens.genVersionNumberWithRange(range).map(DecVer.Minor(_))

  def genDecVer: Gen[DecVer] =
    for {
      major <- genMajor
      minor <- genMinor
      pre   <- Gen.frequency1[Option[PreRelease]](
                 5 -> Gen.constant(None),
                 7 -> SemVerGens.genPreRelease.map(Some(_))
               )
      meta  <- Gen.frequency1[Option[BuildMetaInfo]](
                 5 -> Gen.constant(None),
                 7 -> SemVerGens.genBuildMetaInfo.map(Some(_))
               )
    } yield DecVer(major, minor, pre, meta)

  def genMinMaxDecVers: Gen[(DecVer, DecVer)] =
    for {
      (major1, major2) <- SemVerGens.genMinMaxNonNegInts.map(SemVerGens.pairFromIntsTo(DecVer.Major.apply))
      (minor1, minor2) <- SemVerGens.genMinMaxNonNegInts.map(SemVerGens.pairFromIntsTo(DecVer.Minor.apply))
      pre1             <- Gen.frequency1[Option[PreRelease]](
                            5 -> Gen.constant(None),
                            7 -> SemVerGens.genPreRelease.map(Some(_))
                          )
      meta1            <- Gen.frequency1[Option[BuildMetaInfo]](
                            5 -> Gen.constant(None),
                            7 -> SemVerGens.genBuildMetaInfo.map(Some(_))
                          )
      pre2             <- Gen.frequency1[Option[PreRelease]](
                            5 -> Gen.constant(None),
                            7 -> SemVerGens.genPreRelease.map(Some(_))
                          )
      meta2            <- Gen.frequency1[Option[BuildMetaInfo]](
                            5 -> Gen.constant(None),
                            7 -> SemVerGens.genBuildMetaInfo.map(Some(_))
                          )
    } yield (DecVer(major1, minor1, pre1, meta1), DecVer(major2, minor2, pre2, meta2))

  def genLessAndGreaterDecVerPair: Gen[(DecVer, DecVer)] =
    for {
      major1 <- DecVerGens.genMajorWithMax(Int.MaxValue >> 1)
      minor1 <- DecVerGens.genMinorWithMax(Int.MaxValue >> 1)
      diff   <- Gen.int(Range.linear(1, Int.MaxValue >> 1))
      choice <- Gen.element1(1, 2)
      (m, n) = ((choice & 2) >> 1, choice & 1)
      pre1  <- Gen.frequency1[Option[PreRelease]](
                 5 -> Gen.constant(None),
                 7 -> SemVerGens.genPreRelease.map(Some(_))
               )
      meta1 <- Gen.frequency1[Option[BuildMetaInfo]](
                 5 -> Gen.constant(None),
                 7 -> SemVerGens.genBuildMetaInfo.map(Some(_))
               )
      pre2  <- Gen.frequency1[Option[PreRelease]](
                 5 -> Gen.constant(None),
                 7 -> SemVerGens.genPreRelease.map(Some(_))
               )
      meta2 <- Gen.frequency1[Option[BuildMetaInfo]](
                 5 -> Gen.constant(None),
                 7 -> SemVerGens.genBuildMetaInfo.map(Some(_))
               )
    } yield (
      DecVer(major1, minor1, pre1, meta1),
      DecVer(DecVer.Major(major1.value + diff * m), DecVer.Minor(minor1.value + diff * n), pre2, meta2)
    )

  def genDecVerWithOnlyMajorMinorPatch(
    majorRange: Range[Int],
    minorRange: Range[Int],
  ): Gen[DecVer] = for {
    major <- genMajorWithRange(majorRange)
    minor <- genMinorWithRange(minorRange)
  } yield DecVer(major, minor, None, None)

  def genDecVerWithRange(majorRange: Range[Int], minorRange: Range[Int]): Gen[DecVer] =
    for {
      semVer          <- genDecVerWithOnlyMajorMinorPatch(majorRange, minorRange)
      maybePreAndMeta <- Gen
                           .frequency1(
                             5 -> SemVerGens
                               .genPreReleaseAndBuildMetaInfo
                               .map[(Option[PreRelease], Option[BuildMetaInfo])] {
                                 case (pre, meta) =>
                                   ((Some(pre), Some(meta)))
                               },
                             5 -> Gen
                               .frequency1[Option[PreRelease]](
                                 5 -> Gen.constant(None),
                                 7 -> SemVerGens.genPreRelease.map(Some(_))
                               )
                               .flatMap { preRelease =>
                                 Gen
                                   .frequency1[Option[BuildMetaInfo]](
                                     5 -> Gen.constant(None),
                                     7 -> SemVerGens.genBuildMetaInfo.map(Some(_))
                                   )
                                   .map(meta => (preRelease, meta))

                               }
                           )
                           .option

    } yield maybePreAndMeta match {
      case Some((pre, meta)) =>
        semVer.copy(pre = pre, buildMetadata = meta)
      case None =>
        semVer
    }

}
