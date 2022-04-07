package just.semver

import hedgehog._

import just.Common._
import just.GenPlus

import just.semver.AdditionalInfo.{BuildMetaInfo, PreRelease}
import just.semver.SemVer.{Major, Minor, Patch}

import scala.annotation.tailrec

/** @author
  *   Kevin Lee
  * @since
  *   2018-11-04
  */
object Gens {

  def genAlphabetChar: Gen[Char] =
    Gen.frequency1(
      8 -> Gen.char('a', 'z'),
      2 -> Gen.char('A', 'Z')
    )

  def genDigitChar: Gen[Char] =
    Gen.char('0', '9')

  def genHyphen: Gen[Anh] =
    Gen.constant(Anh.Hyphen)

  def genMinMax[T: Ordering](genOrderedPair: Gen[(T, T)]): Gen[(T, T)] =
    genOrderedPair.map {
      case (x, y) =>
        if (implicitly[Ordering[T]].compare(x, y) <= 0)
          (x, y)
        else
          (y, x)
    }

  def genInt(min: Int, max: Int): Gen[Int] =
    GenPlus.range(Range.linear(min, max))(Gen.int)

  def genNonNegativeInt: Gen[Int] =
    GenPlus.range(Range.linear(0, Int.MaxValue))(Gen.int)

  def genDifferentNonNegIntPair: Gen[(Int, Int)] = for {
    x <- genNonNegativeInt
    y <- genNonNegativeInt
  } yield {
    val z =
      if (x !== y)
        y
      else if (y === Int.MaxValue)
        0
      else
        y + 1
    (x, z)
  }

  def genMinMaxNonNegInts: Gen[(Int, Int)] =
    Gens.genMinMax(Gens.genDifferentNonNegIntPair)

  def pairFromIntsTo[T](constructor: Int => T): ((Int, Int)) => (T, T) =
    pair => (constructor(pair._1), constructor(pair._2))

  def genMajor: Gen[Major] =
    genNonNegativeInt.map(Major.apply)

  def genMinMaxMajors: Gen[(Major, Major)] =
    genMinMaxNonNegInts.map(pairFromIntsTo(Major.apply))

  def genMinor: Gen[Minor] =
    genNonNegativeInt.map(Minor.apply)

  def genMinMaxMinors: Gen[(Minor, Minor)] =
    genMinMaxNonNegInts.map(pairFromIntsTo(Minor.apply))

  def genPatch: Gen[Patch] =
    genNonNegativeInt.map(Patch.apply)

  def genMinMaxPatches: Gen[(Patch, Patch)] =
    genMinMaxNonNegInts.map(pairFromIntsTo(Patch.apply))

  def genNum: Gen[Anh] =
    genInt(0, 999).map(Anh.num)

  def genMinMaxNum: Gen[(Anh, Anh)] =
    genMinMaxNonNegInts.map(pairFromIntsTo(Anh.num))

  def genAlphabetString(max: Int): Gen[String] =
    Gen.string(genAlphabetChar, Range.linear(1, max))

  def genAlphabet(max: Int): Gen[Anh] =
    genAlphabetString(max).map(Anh.alphabet)

  def genDifferentAlphabetPair(max: Int): Gen[(Anh, Anh)] =
    for {
      x <- genAlphabetString(max)
      y <- genAlphabetString(max)
      z <- genAlphabetChar
    } yield {
      if (x === y)
        (Anh.alphabet(x), Anh.alphabet(y + String.valueOf(z)))
      else
        (Anh.alphabet(x), Anh.alphabet(y))
    }

  def genMinMaxAlphabet(max: Int): Gen[(Anh, Anh)] =
    genMinMax(genDifferentAlphabetPair(max))

  def combineAlphaNumHyphen(alps: List[Anh]): List[Anh] = {
    @tailrec
    def combine(x: Anh, xs: List[Anh], acc: List[Anh]): List[Anh] =
      (x, xs) match {
        case (Anh.Alphabet(a1), Anh.Alphabet(a2) :: rest) =>
          combine(Anh.Alphabet(a1 + a2), rest, acc)
        case (a @ Anh.Alphabet(_), Anh.Num(n) :: rest) =>
          combine(Anh.Num(n), rest, a :: acc)
        case (a @ Anh.Alphabet(_), Anh.Hyphen :: rest) =>
          combine(Anh.Hyphen, rest, a :: acc)
        case (Anh.Num(n1), Anh.Num(n2) :: rest) =>
          combine(Anh.Num(n1 + n2), rest, acc)
        case (n @ Anh.Num(_), (a @ Anh.Alphabet(_)) :: rest) =>
          combine(a, rest, n :: acc)
        case (n @ Anh.Num(_), Anh.Hyphen :: rest) =>
          combine(Anh.Hyphen, rest, n :: acc)
        case (Anh.Hyphen, Anh.Hyphen :: rest) =>
          combine(Anh.Hyphen, rest, Anh.Hyphen :: acc)
        case (Anh.Hyphen, (a @ Anh.Alphabet(_)) :: rest) =>
          combine(a, rest, Anh.Hyphen :: acc)
        case (Anh.Hyphen, (n @ Anh.Num(_)) :: rest) =>
          combine(n, rest, Anh.Hyphen :: acc)
        case (_, Nil) =>
          (x :: acc).reverse
      }
    alps match {
      case a :: as =>
        combine(a, as, List.empty)
      case Nil =>
        Nil
    }
  }

  def genAlphaNumHyphenGroup: Gen[Dsv] = for {
    values <- Gen.choice1[Anh](genNum, genAlphabet(10), genHyphen).list(Range.linear(1, 3))
    combined = combineAlphaNumHyphen(values)
  } yield Dsv(combined)

  def toValidNum(alps: List[Anh]): List[Anh] = alps match {
    case Anh.Num(n) :: Nil =>
      if (n === "0" || n.takeWhile(_ === '0').length === 0)
        alps
      else
        Anh.Num(n.toInt.toString) :: Nil
    case _ =>
      alps
  }

  def genPreRelease: Gen[AdditionalInfo.PreRelease] =
    (for {
      alpnhGroup <- genAlphaNumHyphenGroup
      Dsv(alps) = alpnhGroup
      newAlps   = toValidNum(alps)
    } yield Dsv(newAlps))
      .list(Range.linear(1, 5))
      .map(PreRelease.apply)

  def genBuildMetaInfo: Gen[AdditionalInfo.BuildMetaInfo] =
    genAlphaNumHyphenGroup
      .list(Range.linear(1, 5))
      .map(BuildMetaInfo.apply)

  def genMinMaxAlphaNumHyphenGroup: Gen[(Dsv, Dsv)] = for {
    minMaxAlps <- Gen
                    .frequency1(
                      5 -> genMinMaxNum,
                      3 -> genMinMaxAlphabet(10),
                      1 -> genHyphen.map(x => (x, x))
                    )
                    .list(Range.linear(1, 3))
    (minAlps, maxAlps) =
      minMaxAlps.foldRight(
        (List.empty[Anh], List.empty[Anh])
      ) {
        case ((id1, id2), (ids1, ids2)) =>
          (id1 :: ids1, id2 :: ids2)
      }
  } yield (Dsv(minAlps), Dsv(maxAlps))

  def genMinMaxAlphaNumHyphenGroupList(
    minMaxAlphaNumHyphenGroupGen: Gen[(Dsv, Dsv)]
  ): Gen[(List[Dsv], List[Dsv])] = for {
    minMaxIds <- minMaxAlphaNumHyphenGroupGen.list(Range.linear(1, 3))
    (minIds, maxIds) =
      minMaxIds.foldRight(
        (List.empty[Dsv], List.empty[Dsv])
      ) {
        case ((id1, id2), (ids1, ids2)) =>
          (id1 :: ids1, id2 :: ids2)
      }
  } yield (minIds, maxIds)

  def toValidMinMaxNum(minAlps: List[Anh], maxAlps: List[Anh]): (List[Anh], List[Anh]) =
    (minAlps, maxAlps) match {
      case (Anh.Num(_) :: Nil, Anh.Num(_) :: Nil) =>
        val newMinAlps = toValidNum(minAlps)
        val newMaxAlps = toValidNum(maxAlps)
        (newMinAlps, newMaxAlps) match {
          case (Anh.Num(n1) :: Nil, Anh.Num(n2) :: Nil) =>
            val i1 = n1.toInt
            val i2 = n2.toInt
            if (i1 === i2) {
              val i2a = i2 + 1
              if (i2a < 0)
                (Anh.num(i1 - 1) :: Nil, Anh.num(i2) :: Nil)
              else
                (Anh.num(i1) :: Nil, Anh.num(i2a) :: Nil)
            } else {
              (newMinAlps, newMaxAlps)
            }

          case (_, _) =>
            (newMinAlps, newMaxAlps)
        }

      case (Anh.Num(_) :: Nil, _) =>
        (toValidNum(minAlps), maxAlps)
      case (_, Anh.Num(_) :: Nil) =>
        (minAlps, toValidNum(maxAlps))
      case (_, _) =>
        (minAlps, maxAlps)
    }

  def genMinMaxPreRelease: Gen[(AdditionalInfo.PreRelease, AdditionalInfo.PreRelease)] =
    genMinMaxAlphaNumHyphenGroupList(
      for {
        minMaxAlpGroup <- genMinMaxAlphaNumHyphenGroup
        (Dsv(minAlps), Dsv(maxAlps)) = minMaxAlpGroup
        (newMinAlps, newMaxAlps)     = toValidMinMaxNum(minAlps, maxAlps)
      } yield (Dsv(newMinAlps), Dsv(newMaxAlps))
    ).map {
      case (min, max) =>
        (AdditionalInfo.PreRelease(min), AdditionalInfo.PreRelease(max))
    }

  def genMinMaxBuildMetaInfo: Gen[(AdditionalInfo.BuildMetaInfo, AdditionalInfo.BuildMetaInfo)] =
    genMinMaxAlphaNumHyphenGroupList(genMinMaxAlphaNumHyphenGroup)
      .map {
        case (min, max) =>
          (AdditionalInfo.BuildMetaInfo(min), AdditionalInfo.BuildMetaInfo(max))
      }

  def genSemVer: Gen[SemVer] = for {
    major <- genMajor
    minor <- genMinor
    patch <- genPatch
    pre   <- genPreRelease.option
    meta  <- genBuildMetaInfo.option
  } yield SemVer(major, minor, patch, pre, meta)

  def genMinMaxSemVers: Gen[(SemVer, SemVer)] = for {
    (majorPair1, majorPair2) <- genMinMaxMajors
    (minorPair1, minorPair2) <- genMinMaxMinors
    (patchPair1, patchPair2) <- genMinMaxPatches
    pre                      <- genMinMaxPreRelease.option
    meta                     <- genMinMaxBuildMetaInfo.option
  } yield {
    val (pre1, pre2)   =
      pre.fold(
        (none[AdditionalInfo.PreRelease], none[AdditionalInfo.PreRelease])
      ) {
        case (xy1, xy2) =>
          (xy1.some, xy2.some)
      }
    val (meta1, meta2) =
      meta.fold(
        (none[AdditionalInfo.BuildMetaInfo], none[AdditionalInfo.BuildMetaInfo])
      ) {
        case (xy1, xy2) =>
          (xy1.some, xy2.some)
      }

    (
      SemVer(
        majorPair1,
        minorPair1,
        patchPair1,
        pre1,
        meta1
      ),
      SemVer(
        majorPair2,
        minorPair2,
        patchPair2,
        pre2,
        meta2
      )
    )
  }

}
