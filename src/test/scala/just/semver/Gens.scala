package just.semver

import hedgehog._
import just.GenPlus
import just.fp.syntax._
import just.semver.SemVer.{Major, Minor, Patch}

import scala.annotation.tailrec

/**
  * @author Kevin Lee
  * @since 2018-11-04
  */
object Gens {

  def genAlphabetChar: Gen[Char] =
    Gen.frequency1(
      8 -> Gen.char('a', 'z'), 2 -> Gen.char('A', 'Z')
    )

  def genDigitChar: Gen[Char] =
    Gen.char('0', '9')

  def genHyphen: Gen[AlphaNumHyphen] =
    Gen.constant(AlphaNumHyphen.Hyphen)

  def genMinMax[T : Ordering](genOrderedPair: Gen[(T, T)]): Gen[(T, T)] =
    genOrderedPair.map { case (x, y) =>
      if (implicitly[Ordering[T]].compare(x, y) <= 0) (x, y) else (y, x)
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
      if (x !== y) y
      else if (y === Int.MaxValue) 0
      else y + 1
    (x, z)
  }

  def genMinMaxNonNegInts: Gen[(Int, Int)] =
    Gens.genMinMax(Gens.genDifferentNonNegIntPair)

  def pairFromIntsTo[T](constructor: Int => T): ((Int, Int)) => (T, T) =
    pair => (constructor(pair._1), constructor(pair._2))


  def genMajor: Gen[Major] =
    genNonNegativeInt.map(Major)

  def genMinMaxMajors: Gen[(Major, Major)] =
    genMinMaxNonNegInts.map(pairFromIntsTo(Major))

  def genMinor: Gen[Minor] =
    genNonNegativeInt.map(Minor)

  def genMinMaxMinors: Gen[(Minor, Minor)] =
    genMinMaxNonNegInts.map(pairFromIntsTo(Minor))

  def genPatch: Gen[Patch] =
    genNonNegativeInt.map(Patch)

  def genMinMaxPatches: Gen[(Patch, Patch)] =
    genMinMaxNonNegInts.map(pairFromIntsTo(Patch))

  def genNum: Gen[AlphaNumHyphen] =
    genInt(0, 999).map(AlphaNumHyphen.num)

  def genMinMaxNum: Gen[(AlphaNumHyphen, AlphaNumHyphen)] =
    genMinMaxNonNegInts.map(pairFromIntsTo(AlphaNumHyphen.num))

  def genAlphabetString(max: Int): Gen[String] =
    Gen.string(genAlphabetChar, Range.linear(1, max))

  def genAlphabet(max: Int): Gen[AlphaNumHyphen] =
    genAlphabetString(max).map(AlphaNumHyphen.alphabet)

  def genDifferentAlphabetPair(max: Int): Gen[(AlphaNumHyphen, AlphaNumHyphen)] =
    for {
      x <- genAlphabetString(max)
      y <- genAlphabetString(max)
      z <- genAlphabetChar
    } yield {
      if (x === y)
        (AlphaNumHyphen.alphabet(x), AlphaNumHyphen.alphabet(y + String.valueOf(z)))
      else
        (AlphaNumHyphen.alphabet(x), AlphaNumHyphen.alphabet(y))
    }

  def genMinMaxAlphabet(max: Int): Gen[(AlphaNumHyphen, AlphaNumHyphen)] =
    genMinMax(genDifferentAlphabetPair(max))

  def combineAlphaNumHyphen(alps: List[AlphaNumHyphen]): List[AlphaNumHyphen] = {
    @tailrec
    def combine(x: AlphaNumHyphen, xs: List[AlphaNumHyphen], acc: List[AlphaNumHyphen]): List[AlphaNumHyphen] =
      (x, xs) match {
        case (AlphaNumHyphen.Alphabet(a1), AlphaNumHyphen.Alphabet(a2) :: rest) =>
          combine(AlphaNumHyphen.Alphabet(a1 + a2), rest, acc)
        case (a@AlphaNumHyphen.Alphabet(_), AlphaNumHyphen.Num(n) :: rest) =>
          combine(AlphaNumHyphen.Num(n), rest, a :: acc)
        case (a@AlphaNumHyphen.Alphabet(_), AlphaNumHyphen.Hyphen :: rest) =>
          combine(AlphaNumHyphen.Hyphen, rest, a :: acc)
        case (AlphaNumHyphen.Num(n1), AlphaNumHyphen.Num(n2) :: rest) =>
          combine(AlphaNumHyphen.Num(n1 + n2), rest, acc)
        case (n@AlphaNumHyphen.Num(_), (a@AlphaNumHyphen.Alphabet(_)) :: rest) =>
          combine(a, rest, n :: acc)
        case (n@AlphaNumHyphen.Num(_), AlphaNumHyphen.Hyphen :: rest) =>
          combine(AlphaNumHyphen.Hyphen, rest, n :: acc)
        case (AlphaNumHyphen.Hyphen, AlphaNumHyphen.Hyphen :: rest) =>
          combine(AlphaNumHyphen.Hyphen, rest, AlphaNumHyphen.Hyphen :: acc)
        case (AlphaNumHyphen.Hyphen, (a@AlphaNumHyphen.Alphabet(_)) :: rest) =>
          combine(a, rest, AlphaNumHyphen.Hyphen :: acc)
        case (AlphaNumHyphen.Hyphen, (n@AlphaNumHyphen.Num(_)) :: rest) =>
          combine(n, rest, AlphaNumHyphen.Hyphen :: acc)
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

  def genAlphaNumHyphenGroup: Gen[AlphaNumHyphenGroup] = for {
    values <- Gen.choice1[AlphaNumHyphen](genNum, genAlphabet(10), genHyphen).list(Range.linear(1, 3))
    combined = combineAlphaNumHyphen(values)
  } yield AlphaNumHyphenGroup(combined)

  def genIdentifier(alphaNumHyphenGroupGen: Gen[AlphaNumHyphenGroup]): Gen[AdditionalInfo.Identifier] =
    alphaNumHyphenGroupGen.list(Range.linear(1, 5)).map(AdditionalInfo.Identifier(_))

  def toValidNum(alps: List[AlphaNumHyphen]): List[AlphaNumHyphen] = alps match {
    case AlphaNumHyphen.Num(n) :: Nil =>
      if (n === "0" || n.takeWhile(_ === '0').length === 0)
        alps
      else
        AlphaNumHyphen.Num(n.toInt.toString) :: Nil
    case _ =>
      alps
  }

  def genPreRelease: Gen[AdditionalInfo.PreRelease] = genIdentifier(
    for {
      alpnhGroup <-genAlphaNumHyphenGroup
      AlphaNumHyphenGroup(alps) = alpnhGroup
      newAlps = toValidNum(alps)
    } yield AlphaNumHyphenGroup(newAlps)
  ).map(AdditionalInfo.PreRelease)

  def genBuildMetaInfo: Gen[AdditionalInfo.BuildMetaInfo] =
    genIdentifier(genAlphaNumHyphenGroup)
      .map(AdditionalInfo.BuildMetaInfo)

  def genMinMaxAlphaNumHyphenGroup: Gen[(AlphaNumHyphenGroup, AlphaNumHyphenGroup)] = for {
    minMaxAlps <- Gen.frequency1(
        5 -> genMinMaxNum, 3 -> genMinMaxAlphabet(10), 1 -> genHyphen.map(x => (x, x))
      ).list(Range.linear(1, 3))
    (minAlps, maxAlps) = minMaxAlps.foldLeft(
        (List.empty[AlphaNumHyphen], List.empty[AlphaNumHyphen])
      ) { case ((ids1, ids2), (id1, id2)) =>
        (ids1 :+ id1, ids2 :+ id2)
      }
  } yield (AlphaNumHyphenGroup(minAlps), AlphaNumHyphenGroup(maxAlps))

  def genMinMaxIdentifier(
    minMaxAlphaNumHyphenGroupGen: Gen[(AlphaNumHyphenGroup, AlphaNumHyphenGroup)]
  ): Gen[(AdditionalInfo.Identifier, AdditionalInfo.Identifier)] = for {
    minMaxIds <- genMinMaxAlphaNumHyphenGroup.list(Range.linear(1, 3))
    (minIds, maxIds) =
      minMaxIds.foldLeft(
        (List.empty[AlphaNumHyphenGroup], List.empty[AlphaNumHyphenGroup])
      ) {
        case ((ids1, ids2), (id1, id2)) =>
          (ids1 :+ id1, ids2 :+ id2)
      }
  } yield (AdditionalInfo.Identifier(minIds), AdditionalInfo.Identifier(maxIds))

  def toValidMinMaxNum(minAlps: List[AlphaNumHyphen], maxAlps: List[AlphaNumHyphen]): (List[AlphaNumHyphen], List[AlphaNumHyphen]) =
    (minAlps, maxAlps) match {
      case (AlphaNumHyphen.Num(_) :: Nil, AlphaNumHyphen.Num(_) :: Nil) =>
        val newMinAlps = toValidNum(minAlps)
        val newMaxAlps = toValidNum(maxAlps)
        (newMinAlps, newMaxAlps) match {
          case (AlphaNumHyphen.Num(n1) :: Nil, AlphaNumHyphen.Num(n2) :: Nil) =>
            val i1 = n1.toInt
            val i2 = n2.toInt
            if (i1 === i2) {
              val i2a = i2 + 1
              if (i2a < 0)
                (AlphaNumHyphen.num(i1 - 1) :: Nil, AlphaNumHyphen.num(i2) :: Nil)
              else
                (AlphaNumHyphen.num(i1) :: Nil, AlphaNumHyphen.num(i2a) :: Nil)
            } else {
              (newMinAlps, newMaxAlps)
            }
          case (_, _) =>
            (newMinAlps, newMaxAlps)
        }
      case (AlphaNumHyphen.Num(_) :: Nil, _) =>
        (toValidNum(minAlps), maxAlps)
      case (_, AlphaNumHyphen.Num(_) :: Nil) =>
        (minAlps, toValidNum(maxAlps))
      case (_, _) =>
        (minAlps, maxAlps)
    }

  def genMinMaxPreRelease: Gen[(AdditionalInfo.PreRelease, AdditionalInfo.PreRelease)] = genMinMaxIdentifier(
    for {
      minMaxAlpGroup <- genMinMaxAlphaNumHyphenGroup
      (AlphaNumHyphenGroup(minAlps), AlphaNumHyphenGroup(maxAlps)) = minMaxAlpGroup
      (newMinAlps, newMaxAlps) = toValidMinMaxNum(minAlps, maxAlps)
    } yield (AlphaNumHyphenGroup(newMinAlps), AlphaNumHyphenGroup(newMaxAlps))
  ).map { case (min, max) => (AdditionalInfo.PreRelease(min), AdditionalInfo.PreRelease(max)) }

  def genMinMaxBuildMetaInfo: Gen[(AdditionalInfo.BuildMetaInfo, AdditionalInfo.BuildMetaInfo)] =
    genMinMaxIdentifier(genMinMaxAlphaNumHyphenGroup)
      .map { case (min, max) => (AdditionalInfo.BuildMetaInfo(min), AdditionalInfo.BuildMetaInfo(max)) }

  def genSemVer: Gen[SemVer] = for {
    major <- genMajor
    minor <- genMinor
    patch <- genPatch
    pre <- genPreRelease.option
    meta <- genBuildMetaInfo.option
  } yield SemVer(major, minor, patch, pre, meta)

  def genMinMaxSemVers: Gen[(SemVer, SemVer)] = for {
    majorPair <- genMinMaxMajors
    minorPair <- genMinMaxMinors
    patchPair <- genMinMaxPatches
    pre <- genMinMaxPreRelease.option
    meta <- genMinMaxBuildMetaInfo.option
  } yield {
    val (pre1, pre2) =
      pre.fold[(Option[AdditionalInfo.PreRelease], Option[AdditionalInfo.PreRelease])]((None, None))(
        xy => (Option(xy._1), Option(xy._2))
      )
    val (meta1, meta2) =
      meta.fold[(Option[AdditionalInfo.BuildMetaInfo], Option[AdditionalInfo.BuildMetaInfo])]((None, None))(
        xy => (Option(xy._1), Option(xy._2))
      )

    (SemVer(
        majorPair._1
      , minorPair._1
      , patchPair._1
      , pre1
      , meta1
    ),
    SemVer(
        majorPair._2
      , minorPair._2
      , patchPair._2
      , pre2
      , meta2
    ))
  }

}
