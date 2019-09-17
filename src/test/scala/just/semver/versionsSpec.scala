package just.semver

import hedgehog._
import hedgehog.runner._
import AlphaNumHyphen.{alphabet, hyphen, num, numFromStringUnsafe}
import Gens._

/**
  * @author Kevin Lee
  * @since 2018-11-04
  */
object SemVerMajorSpec extends Properties {

  override def tests: List[Test] = List(
    property("Two SemVers with the same Major and the rest are equal then it should be equal", testSameMajors)
  , property("Two SemVers with the different Majors and the rest are equal then it should be not equal", testDifferentMajors)
  , property("Test SemVer(Major(less)) < SemVer(Major(greater)) is true", testMajorLessCase)
  , property("Test SemVer(Major(greater)) > SemVer(Major(less)) is true", testMajorMoreCase)
  , property("Test SemVer(same Major) <= SemVer(same Major) is true", testLeeThanEqualWithSameMajors)
  , property("Test SemVer(Major(less)) <= SemVer(Major(greater)) is true", testLeeThanEqualWithLess)
  , property("Test SemVer(same Major) >= SemVer(same Major) is true", testMoreThanEqualWithSameMajors)
  , property("Test SemVer(Major(greater)) >= SemVer(Major(less)) is true", testMoreThanEqualWithGreater)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSameMajors: Property = for {
    major <- genMajor.log("major")
  } yield {
    val v1 = SemVer.withMajor(major)
    val v2 = SemVer.withMajor(major)
    Result.assert(v1 == v2).log("major == major")
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDifferentMajors: Property = for {
    major1AndMajor2 <- genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemVer.withMajor(major1)
    val v2 = SemVer.withMajor(major2)
    Result.assert(v1 != v2).log("major1 != major2")
  }

  def testMajorLessCase: Property = for {
    major1AndMajor2 <- genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemVer.withMajor(major1)
    val v2 = SemVer.withMajor(major2)
    Result.assert(v1 < v2).log("major1 < major2")
  }

  def testMajorMoreCase: Property = for {
    major1AndMajor2 <- genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemVer.withMajor(major1)
    val v2 = SemVer.withMajor(major2)
    Result.assert(v2 > v1).log("major2 > major1")
  }

  def testLeeThanEqualWithSameMajors: Property = for {
    major <- genMajor.log("major")
  } yield {
    val v1 = SemVer.withMajor(major)
    val v2 = SemVer.withMajor(major)
    Result.assert(v1 <= v2).log("major1 <= major2")
  }

  def testLeeThanEqualWithLess: Property = for {
    major1AndMajor2 <- genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemVer.withMajor(major1)
    val v2 = SemVer.withMajor(major2)
    Result.assert(v1 <= v2).log("major1 <= major2")
  }

  def testMoreThanEqualWithSameMajors: Property = for {
    major <- genMajor.log("major")
  } yield {
    val v1 = SemVer.withMajor(major)
    val v2 = SemVer.withMajor(major)
    Result.assert(v1 >= v2)
  }

  def testMoreThanEqualWithGreater: Property = for {
    major1AndMajor2 <- genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemVer.withMajor(major1)
    val v2 = SemVer.withMajor(major2)
    Result.assert(v2 >= v1).log("major2 >= major1")
  }

}

object SemVerMinorSpec extends Properties {

  override def tests: List[Test] = List(
    property("Two SemVers with the same Minor and the rest are equal then it should be equal", testSameMinors)
    , property("Two SemVers with the different Minors and the rest are equal then it should be not equal", testDifferentMinors)
    , property("Test SemVer(Minor(less)) < SemVer(Minor(greater)) is true", testMinorLessCase)
    , property("Test SemVer(Minor(greater)) > SemVer(Minor(less)) is true", testMinorMoreCase)
    , property("Test SemVer(same Minor) <= SemVer(same Minor) is true", testLeeThanEqualWithSameMinors)
    , property("Test SemVer(Minor(less)) <= SemVer(Minor(greater)) is true", testLeeThanEqualWithLess)
    , property("Test SemVer(same Minor) >= SemVer(same Minor) is true", testMoreThanEqualWithSameMinors)
    , property("Test SemVer(Minor(greater)) >= SemVer(Minor(less)) is true", testMoreThanEqualWithGreater)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSameMinors: Property = for {
    minor <- genMinor.log("minor")
  } yield {
    val v1 = SemVer.withMinor(minor)
    val v2 = SemVer.withMinor(minor)
    Result.assert(v1 == v2).log("minor == minor")
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDifferentMinors: Property = for {
    minor1AndMinor2 <- genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemVer.withMinor(minor1)
    val v2 = SemVer.withMinor(minor2)
    Result.assert(v1 != v2).log("minor1 != minor2")
  }

  def testMinorLessCase: Property = for {
    minor1AndMinor2 <- genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemVer.withMinor(minor1)
    val v2 = SemVer.withMinor(minor2)
    Result.assert(v1 < v2).log("minor1 < minor2")
  }

  def testMinorMoreCase: Property = for {
    minor1AndMinor2 <- genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemVer.withMinor(minor1)
    val v2 = SemVer.withMinor(minor2)
    Result.assert(v2 > v1).log("minor2 > minor1")
  }

  def testLeeThanEqualWithSameMinors: Property = for {
    minor <- genMinor.log("minor")
  } yield {
    val v1 = SemVer.withMinor(minor)
    val v2 = SemVer.withMinor(minor)
    Result.assert(v1 <= v2).log("minor1 <= minor2")
  }

  def testLeeThanEqualWithLess: Property = for {
    minor1AndMinor2 <- genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemVer.withMinor(minor1)
    val v2 = SemVer.withMinor(minor2)
    Result.assert(v1 <= v2).log("minor1 <= minor2")
  }

  def testMoreThanEqualWithSameMinors: Property = for {
    minor <- genMinor.log("minor")
  } yield {
    val v1 = SemVer.withMinor(minor)
    val v2 = SemVer.withMinor(minor)
    Result.assert(v1 >= v2)
  }

  def testMoreThanEqualWithGreater: Property = for {
    minor1AndMinor2 <- genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemVer.withMinor(minor1)
    val v2 = SemVer.withMinor(minor2)
    Result.assert(v2 >= v1).log("minor2 >= minor1")
  }

}

object SemVerPatchSpec extends Properties {

  override def tests: List[Test] = List(
    property("Two SemVers with the same Patch and the rest are equal then it should be equal", testSamePatchs)
    , property("Two SemVers with the different Patchs and the rest are equal then it should be not equal", testDifferentPatchs)
    , property("Test SemVer(Patch(less)) < SemVer(Patch(greater)) is true", testPatchLessCase)
    , property("Test SemVer(Patch(greater)) > SemVer(Patch(less)) is true", testPatchMoreCase)
    , property("Test SemVer(same Patch) <= SemVer(same Patch) is true", testLeeThanEqualWithSamePatchs)
    , property("Test SemVer(Patch(less)) <= SemVer(Patch(greater)) is true", testLeeThanEqualWithLess)
    , property("Test SemVer(same Patch) >= SemVer(same Patch) is true", testMoreThanEqualWithSamePatchs)
    , property("Test SemVer(Patch(greater)) >= SemVer(Patch(less)) is true", testMoreThanEqualWithGreater)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSamePatchs: Property = for {
    patch <- genPatch.log("patch")
  } yield {
    val v1 = SemVer.withPatch(patch)
    val v2 = SemVer.withPatch(patch)
    Result.assert(v1 == v2).log("patch == patch")
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDifferentPatchs: Property = for {
    patch1AndPatch2 <- genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemVer.withPatch(patch1)
    val v2 = SemVer.withPatch(patch2)
    Result.assert(v1 != v2).log("patch1 != patch2")
  }

  def testPatchLessCase: Property = for {
    patch1AndPatch2 <- genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemVer.withPatch(patch1)
    val v2 = SemVer.withPatch(patch2)
    Result.assert(v1 < v2).log("patch1 < patch2")
  }

  def testPatchMoreCase: Property = for {
    patch1AndPatch2 <- genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemVer.withPatch(patch1)
    val v2 = SemVer.withPatch(patch2)
    Result.assert(v2 > v1).log("patch2 > patch1")
  }

  def testLeeThanEqualWithSamePatchs: Property = for {
    patch <- genPatch.log("patch")
  } yield {
    val v1 = SemVer.withPatch(patch)
    val v2 = SemVer.withPatch(patch)
    Result.assert(v1 <= v2).log("patch1 <= patch2")
  }

  def testLeeThanEqualWithLess: Property = for {
    patch1AndPatch2 <- genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemVer.withPatch(patch1)
    val v2 = SemVer.withPatch(patch2)
    Result.assert(v1 <= v2).log("patch1 <= patch2")
  }

  def testMoreThanEqualWithSamePatchs: Property = for {
    patch <- genPatch.log("patch")
  } yield {
    val v1 = SemVer.withPatch(patch)
    val v2 = SemVer.withPatch(patch)
    Result.assert(v1 >= v2)
  }

  def testMoreThanEqualWithGreater: Property = for {
    patch1AndPatch2 <- genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemVer.withPatch(patch1)
    val v2 = SemVer.withPatch(patch2)
    Result.assert(v2 >= v1).log("patch2 >= patch1")
  }

}

object AlphaNumHyphenSpec extends Properties {

  override def tests: List[Test] = List(
    property("Num(same).compare(Num(same)) should return 0", testNumEqual)
  , property("Num(less).compare(Num(greater)) should return -1", testNumLess)
  , property("Num(greater).compare(Num(less)) should return 1", testNumMore)
  , property("AlphaHyphen(same).compare(AlphaHyphen(same)) should return 0", testAlphaHyphenEqual)
  , property("AlphaHyphen(less).compare(AlphaHyphen(greater)) should return the Int < 0", testAlphaHyphenLess)
  , property("AlphaHyphen(greater).compare(AlphaHyphen(less)) should return the Int > 0", testAlphaHyphenMore)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testNumEqual: Property = for {
    num <- genNum.log("num")
  } yield {
    num.compare(num) ==== 0 and Result.assert(num == num)
  }

  def testNumLess: Property = for {
    minMax <- genMinMaxNum.log("(num1, num2)")
    (num1, num2) = minMax
  } yield {
    num1.compare(num2) ==== -1
  }

  def testNumMore: Property = for {
    minMax <- genMinMaxNum.log("(num1, num2)")
    (num1, num2) = minMax
  } yield {
    num2.compare(num1) ==== 1
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testAlphaHyphenEqual: Property = for {
    alphaHyphen <- genAlphabet(10).log("alphaHyphen")
  } yield {
    alphaHyphen.compare(alphaHyphen) ==== 0 and Result.assert(alphaHyphen == alphaHyphen)
  }

  def testAlphaHyphenLess: Property = for {
    alphaHyphenPair <- genMinMaxAlphabet(10).log("(alphaHyphen1, alphaHyphen2)")
    (alphaHyphen1, alphaHyphen2) = alphaHyphenPair
  } yield {
    Result.assert(alphaHyphen1.compare(alphaHyphen2) < 0)
  }

  def testAlphaHyphenMore: Property = for {
    alphaHyphenPair <- genMinMaxAlphabet(10).log("(alphaHyphen1, alphaHyphen2)")
    (alphaHyphen1, alphaHyphen2) = alphaHyphenPair
  } yield {
    Result.assert(alphaHyphen2.compare(alphaHyphen1) > 0)
  }

}

object SemVerSpec extends Properties {
  override def tests: List[Test] = List(
      example("""SemVer.parse("1.0.5") should return SementicVersion(Major(1), Minor(0), Patch(5), None, None)""", parseExample1)
    , example("""SemVer.parse("1.0.5-beta") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePre1)
    , example("""SemVer.parse("1.0.5-a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePre2)
    , example("""SemVer.parse("1.0.5-a-b.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePre3)
    , example("""SemVer.parse("1.0.5-0") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePre4)
    , example("""SemVer.parse("1.0.5-000a") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePre5)
    , example("""SemVer.parse("1.0.5-001") should return Left(Invalid)""", parseExamplePreInvalid1)
    , example("""SemVer.parse("1.0.5+1234") should return SementicVersion(Major(1), Minor(0), Patch(5), None, Some(build meta info)""", parseExampleMeta1)
    , example("""SemVer.parse("1.0.5+a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExampleMeta2)
    , example("""SemVer.parse("1.0.5+a-b.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExampleMeta3)
    , example("""SemVer.parse("1.0.5+0") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExampleMeta4)
    , example("""SemVer.parse("1.0.5+000a") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExampleMeta5)
    , example("""SemVer.parse("1.0.5+001") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExampleMeta6)
    , example("""SemVer.parse("1.0.5-beta+1234") should return SementicVersion(Major(1), Minor(0), Patch(5), None, Some(build meta info)""", parseExamplePreMeta1)
    , example("""SemVer.parse("1.0.5-a.3.7.xyz+a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePreMeta2)
    , example("""SemVer.parse("1.0.5-a-b.xyz+a-b.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePreMeta3)
    , property("SemVer(same) == SemVer(same) should be true", testSemVerEqual)
    , property("SemVer(less).compare(SemVer(greater)) should the value less than 0", testSemVerLess)
    , property("SemVer(greater).compare(SemVer(less)) should the value more than 0", testSemVerGreater)
    , property("SemVer round trip", roundTripSemVer)
    )

  def parseExample1: Result = {
    val input = "1.0.5"
    val expected = Right(SemVer(Major(1), Minor(0), Patch(5), None, None))
    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre1: Result = {
    val input = "1.0.5-beta"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , Option(
            AdditionalInfo.PreRelease(
              Identifier(List(AlphaNumHyphenGroup(List(alphabet("beta"))))
              ))
            )
          , None
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre2: Result = {
    val input = "1.0.5-a.3.7.xyz"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , Option(
            AdditionalInfo.PreRelease(
              Identifier(
                List(
                  AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](alphabet("a"))
                  )
                , AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](num(3))
                  )
                , AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](num(7))
                  )
                , AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](alphabet("xyz"))
                  )
                )
              )
            )
          )
          , None
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre3: Result = {
    val input = "1.0.5-a-b.xyz"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , Option(
            AdditionalInfo.PreRelease(
              Identifier(List(
                AlphaNumHyphenGroup(
                  List[AlphaNumHyphen](alphabet("a"), hyphen, alphabet("b"))
                )
              , AlphaNumHyphenGroup(
                  List[AlphaNumHyphen](alphabet("xyz"))
                )
              ))
            )
          )
          , None
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre4: Result = {
    val input = "1.0.5-0"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , Option(
            AdditionalInfo.PreRelease(
              Identifier(List(
                AlphaNumHyphenGroup(
                  List[AlphaNumHyphen](num(0))
                )
              ))
            )
          )
          , None
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre5: Result = {
    val input = "1.0.5-000a"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , Option(
            AdditionalInfo.PreRelease(
              Identifier(List(
                AlphaNumHyphenGroup(
                  List[AlphaNumHyphen](numFromStringUnsafe("000"), alphabet("a"))
                )
              ))
            )
          )
          , None
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePreInvalid1: Result = {
    val input = "1.0.5-001"
    val expected =
      Left(
        ParseError.preReleaseParseError(
          ParseError.leadingZeroNumError("001")
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta1: Result = {
    val input = "1.0.5+1234"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , None
          , Option(
              AdditionalInfo.BuildMetaInfo(
                Identifier(List(AlphaNumHyphenGroup(List(num(1234)))))
              )
            )
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta2: Result = {
    val input = "1.0.5+a.3.7.xyz"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , None
          , Option(
            AdditionalInfo.BuildMetaInfo(
              Identifier(
                List(
                  AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](alphabet("a"))
                  )
                , AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](num(3))
                  )
                , AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](num(7))
                  )
                , AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](alphabet("xyz"))
                  )
                )
              )
            )
          )
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta3: Result = {
    val input = "1.0.5+a-b.xyz"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , None
          , Option(
            AdditionalInfo.BuildMetaInfo(
              Identifier(List(
                AlphaNumHyphenGroup(
                  List[AlphaNumHyphen](alphabet("a"), hyphen, alphabet("b"))
                )
              , AlphaNumHyphenGroup(
                  List[AlphaNumHyphen](alphabet("xyz"))
                )
              ))
            )
          )
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta4: Result = {
    val input = "1.0.5+0"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , None
          , Option(
            AdditionalInfo.BuildMetaInfo(
              Identifier(List(
                AlphaNumHyphenGroup(
                  List[AlphaNumHyphen](num(0))
                )
              ))
            )
          )
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta5: Result = {
    val input = "1.0.5+000a"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , None
          , Option(
            AdditionalInfo.BuildMetaInfo(
              Identifier(List(
                AlphaNumHyphenGroup(
                  List[AlphaNumHyphen](numFromStringUnsafe("000"), alphabet("a"))
                )
              ))
            )
          )
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta6: Result = {
    val input = "1.0.5+001"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , None
          , Option(
            AdditionalInfo.BuildMetaInfo(
              Identifier(List(
                AlphaNumHyphenGroup(
                  List[AlphaNumHyphen](numFromStringUnsafe("001"))
                )
              ))
            )
          )
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta1: Result = {
    val input = "1.0.5-beta+1234"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , Option(
              AdditionalInfo.PreRelease(
                Identifier(List(AlphaNumHyphenGroup(List(alphabet("beta")))))
              )
            )
          , Option(
              AdditionalInfo.BuildMetaInfo(
                Identifier(List(AlphaNumHyphenGroup(List(num(1234)))))
              )
            )
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta2: Result = {
    val input = "1.0.5-a.3.7.xyz+a.3.7.xyz"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , Option(
            AdditionalInfo.PreRelease(
              Identifier(
                List(
                  AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](alphabet("a"))
                  )
                , AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](num(3))
                  )
                , AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](num(7))
                  )
                , AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](alphabet("xyz"))
                  )
                )
              )
            )
          )
          , Option(
            AdditionalInfo.BuildMetaInfo(
              Identifier(
                List(
                  AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](alphabet("a"))
                  )
                , AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](num(3))
                  )
                , AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](num(7))
                  )
                , AlphaNumHyphenGroup(
                    List[AlphaNumHyphen](alphabet("xyz"))
                  )
                )
              )
            )
          )
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta3: Result = {
    val input = "1.0.5-a-b.xyz+a-b.xyz"
    val expected =
      Right(
        SemVer(
            Major(1)
          , Minor(0)
          , Patch(5)
          , Option(
            AdditionalInfo.PreRelease(
              Identifier(List(
                AlphaNumHyphenGroup(
                  List[AlphaNumHyphen](alphabet("a"), hyphen, alphabet("b"))
                )
              , AlphaNumHyphenGroup(
                  List[AlphaNumHyphen](alphabet("xyz"))
                )
              ))
            )
          )
          , Option(
            AdditionalInfo.BuildMetaInfo(
              Identifier(List(
                AlphaNumHyphenGroup(
                  List[AlphaNumHyphen](alphabet("a"), hyphen, alphabet("b"))
                )
              , AlphaNumHyphenGroup(
                  List[AlphaNumHyphen](alphabet("xyz"))
                )
              ))
            )
          )
        )
      )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSemVerEqual: Property = for {
    v <- genSemVer.log("v")
  } yield {
    Result.assert(v == v).log("v == v")
  }

  def testSemVerLess: Property = for {
    v1AndV2 <- genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.assert(v1.compare(v2) < 0)
  }

  def testSemVerGreater: Property = for {
    v1AndV2 <- genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.assert(v2.compare(v1) > 0)
  }

  def roundTripSemVer: Property = for {
    semVer <- genSemVer.log("semVer")
  } yield {
    val rendered = semVer.render
    val actual = SemVer.parse(rendered)
    actual ==== Right(semVer)
  }

}