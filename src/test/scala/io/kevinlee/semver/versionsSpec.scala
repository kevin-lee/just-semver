package io.kevinlee.semver

import hedgehog._
import hedgehog.runner._
import io.kevinlee.semver.AlphaNumHyphen.{alphabet, hyphen, num, numFromStringUnsafe}
import io.kevinlee.semver.Gens._

/**
  * @author Kevin Lee
  * @since 2018-11-04
  */
object SemanticVersionMajorSpec extends Properties {

  override def tests: List[Test] = List(
    property("Two SemanticVersions with the same Major and the rest are equal then it should be equal", testSameMajors)
  , property("Two SemanticVersions with the different Majors and the rest are equal then it should be not equal", testDifferentMajors)
  , property("Test SemanticVersion(Major(less)) < SemanticVersion(Major(greater)) is true", testMajorLessCase)
  , property("Test SemanticVersion(Major(greater)) > SemanticVersion(Major(less)) is true", testMajorMoreCase)
  , property("Test SemanticVersion(same Major) <= SemanticVersion(same Major) is true", testLeeThanEqualWithSameMajors)
  , property("Test SemanticVersion(Major(less)) <= SemanticVersion(Major(greater)) is true", testLeeThanEqualWithLess)
  , property("Test SemanticVersion(same Major) >= SemanticVersion(same Major) is true", testMoreThanEqualWithSameMajors)
  , property("Test SemanticVersion(Major(greater)) >= SemanticVersion(Major(less)) is true", testMoreThanEqualWithGreater)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSameMajors: Property = for {
    major <- genMajor.log("major")
  } yield {
    val v1 = SemanticVersion.withMajor(major)
    val v2 = SemanticVersion.withMajor(major)
    Result.assert(v1 == v2).log("major == major")
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDifferentMajors: Property = for {
    major1AndMajor2 <- genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemanticVersion.withMajor(major1)
    val v2 = SemanticVersion.withMajor(major2)
    Result.assert(v1 != v2).log("major1 != major2")
  }

  def testMajorLessCase: Property = for {
    major1AndMajor2 <- genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemanticVersion.withMajor(major1)
    val v2 = SemanticVersion.withMajor(major2)
    Result.assert(v1 < v2).log("major1 < major2")
  }

  def testMajorMoreCase: Property = for {
    major1AndMajor2 <- genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemanticVersion.withMajor(major1)
    val v2 = SemanticVersion.withMajor(major2)
    Result.assert(v2 > v1).log("major2 > major1")
  }

  def testLeeThanEqualWithSameMajors: Property = for {
    major <- genMajor.log("major")
  } yield {
    val v1 = SemanticVersion.withMajor(major)
    val v2 = SemanticVersion.withMajor(major)
    Result.assert(v1 <= v2).log("major1 <= major2")
  }

  def testLeeThanEqualWithLess: Property = for {
    major1AndMajor2 <- genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemanticVersion.withMajor(major1)
    val v2 = SemanticVersion.withMajor(major2)
    Result.assert(v1 <= v2).log("major1 <= major2")
  }

  def testMoreThanEqualWithSameMajors: Property = for {
    major <- genMajor.log("major")
  } yield {
    val v1 = SemanticVersion.withMajor(major)
    val v2 = SemanticVersion.withMajor(major)
    Result.assert(v1 >= v2)
  }

  def testMoreThanEqualWithGreater: Property = for {
    major1AndMajor2 <- genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemanticVersion.withMajor(major1)
    val v2 = SemanticVersion.withMajor(major2)
    Result.assert(v2 >= v1).log("major2 >= major1")
  }

}

object SemanticVersionMinorSpec extends Properties {

  override def tests: List[Test] = List(
    property("Two SemanticVersions with the same Minor and the rest are equal then it should be equal", testSameMinors)
    , property("Two SemanticVersions with the different Minors and the rest are equal then it should be not equal", testDifferentMinors)
    , property("Test SemanticVersion(Minor(less)) < SemanticVersion(Minor(greater)) is true", testMinorLessCase)
    , property("Test SemanticVersion(Minor(greater)) > SemanticVersion(Minor(less)) is true", testMinorMoreCase)
    , property("Test SemanticVersion(same Minor) <= SemanticVersion(same Minor) is true", testLeeThanEqualWithSameMinors)
    , property("Test SemanticVersion(Minor(less)) <= SemanticVersion(Minor(greater)) is true", testLeeThanEqualWithLess)
    , property("Test SemanticVersion(same Minor) >= SemanticVersion(same Minor) is true", testMoreThanEqualWithSameMinors)
    , property("Test SemanticVersion(Minor(greater)) >= SemanticVersion(Minor(less)) is true", testMoreThanEqualWithGreater)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSameMinors: Property = for {
    minor <- genMinor.log("minor")
  } yield {
    val v1 = SemanticVersion.withMinor(minor)
    val v2 = SemanticVersion.withMinor(minor)
    Result.assert(v1 == v2).log("minor == minor")
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDifferentMinors: Property = for {
    minor1AndMinor2 <- genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemanticVersion.withMinor(minor1)
    val v2 = SemanticVersion.withMinor(minor2)
    Result.assert(v1 != v2).log("minor1 != minor2")
  }

  def testMinorLessCase: Property = for {
    minor1AndMinor2 <- genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemanticVersion.withMinor(minor1)
    val v2 = SemanticVersion.withMinor(minor2)
    Result.assert(v1 < v2).log("minor1 < minor2")
  }

  def testMinorMoreCase: Property = for {
    minor1AndMinor2 <- genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemanticVersion.withMinor(minor1)
    val v2 = SemanticVersion.withMinor(minor2)
    Result.assert(v2 > v1).log("minor2 > minor1")
  }

  def testLeeThanEqualWithSameMinors: Property = for {
    minor <- genMinor.log("minor")
  } yield {
    val v1 = SemanticVersion.withMinor(minor)
    val v2 = SemanticVersion.withMinor(minor)
    Result.assert(v1 <= v2).log("minor1 <= minor2")
  }

  def testLeeThanEqualWithLess: Property = for {
    minor1AndMinor2 <- genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemanticVersion.withMinor(minor1)
    val v2 = SemanticVersion.withMinor(minor2)
    Result.assert(v1 <= v2).log("minor1 <= minor2")
  }

  def testMoreThanEqualWithSameMinors: Property = for {
    minor <- genMinor.log("minor")
  } yield {
    val v1 = SemanticVersion.withMinor(minor)
    val v2 = SemanticVersion.withMinor(minor)
    Result.assert(v1 >= v2)
  }

  def testMoreThanEqualWithGreater: Property = for {
    minor1AndMinor2 <- genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemanticVersion.withMinor(minor1)
    val v2 = SemanticVersion.withMinor(minor2)
    Result.assert(v2 >= v1).log("minor2 >= minor1")
  }

}

object SemanticVersionPatchSpec extends Properties {

  override def tests: List[Test] = List(
    property("Two SemanticVersions with the same Patch and the rest are equal then it should be equal", testSamePatchs)
    , property("Two SemanticVersions with the different Patchs and the rest are equal then it should be not equal", testDifferentPatchs)
    , property("Test SemanticVersion(Patch(less)) < SemanticVersion(Patch(greater)) is true", testPatchLessCase)
    , property("Test SemanticVersion(Patch(greater)) > SemanticVersion(Patch(less)) is true", testPatchMoreCase)
    , property("Test SemanticVersion(same Patch) <= SemanticVersion(same Patch) is true", testLeeThanEqualWithSamePatchs)
    , property("Test SemanticVersion(Patch(less)) <= SemanticVersion(Patch(greater)) is true", testLeeThanEqualWithLess)
    , property("Test SemanticVersion(same Patch) >= SemanticVersion(same Patch) is true", testMoreThanEqualWithSamePatchs)
    , property("Test SemanticVersion(Patch(greater)) >= SemanticVersion(Patch(less)) is true", testMoreThanEqualWithGreater)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSamePatchs: Property = for {
    patch <- genPatch.log("patch")
  } yield {
    val v1 = SemanticVersion.withPatch(patch)
    val v2 = SemanticVersion.withPatch(patch)
    Result.assert(v1 == v2).log("patch == patch")
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDifferentPatchs: Property = for {
    patch1AndPatch2 <- genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemanticVersion.withPatch(patch1)
    val v2 = SemanticVersion.withPatch(patch2)
    Result.assert(v1 != v2).log("patch1 != patch2")
  }

  def testPatchLessCase: Property = for {
    patch1AndPatch2 <- genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemanticVersion.withPatch(patch1)
    val v2 = SemanticVersion.withPatch(patch2)
    Result.assert(v1 < v2).log("patch1 < patch2")
  }

  def testPatchMoreCase: Property = for {
    patch1AndPatch2 <- genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemanticVersion.withPatch(patch1)
    val v2 = SemanticVersion.withPatch(patch2)
    Result.assert(v2 > v1).log("patch2 > patch1")
  }

  def testLeeThanEqualWithSamePatchs: Property = for {
    patch <- genPatch.log("patch")
  } yield {
    val v1 = SemanticVersion.withPatch(patch)
    val v2 = SemanticVersion.withPatch(patch)
    Result.assert(v1 <= v2).log("patch1 <= patch2")
  }

  def testLeeThanEqualWithLess: Property = for {
    patch1AndPatch2 <- genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemanticVersion.withPatch(patch1)
    val v2 = SemanticVersion.withPatch(patch2)
    Result.assert(v1 <= v2).log("patch1 <= patch2")
  }

  def testMoreThanEqualWithSamePatchs: Property = for {
    patch <- genPatch.log("patch")
  } yield {
    val v1 = SemanticVersion.withPatch(patch)
    val v2 = SemanticVersion.withPatch(patch)
    Result.assert(v1 >= v2)
  }

  def testMoreThanEqualWithGreater: Property = for {
    patch1AndPatch2 <- genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemanticVersion.withPatch(patch1)
    val v2 = SemanticVersion.withPatch(patch2)
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

object SemanticVersionSpec extends Properties {
  override def tests: List[Test] = List(
      example("""SemanticVersion.parse("1.0.5") should return SementicVersion(Major(1), Minor(0), Patch(5), None, None)""", parseExample1)
    , example("""SemanticVersion.parse("1.0.5-beta") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePre1)
    , example("""SemanticVersion.parse("1.0.5-a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePre2)
    , example("""SemanticVersion.parse("1.0.5-a-b.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePre3)
    , example("""SemanticVersion.parse("1.0.5-0") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePre4)
    , example("""SemanticVersion.parse("1.0.5-000a") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePre5)
    , example("""SemanticVersion.parse("1.0.5-001") should return Left(Invalid)""", parseExamplePreInvalid1)
    , example("""SemanticVersion.parse("1.0.5+1234") should return SementicVersion(Major(1), Minor(0), Patch(5), None, Some(build meta info)""", parseExampleMeta1)
    , example("""SemanticVersion.parse("1.0.5+a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExampleMeta2)
    , example("""SemanticVersion.parse("1.0.5+a-b.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExampleMeta3)
    , example("""SemanticVersion.parse("1.0.5+0") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExampleMeta4)
    , example("""SemanticVersion.parse("1.0.5+000a") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExampleMeta5)
    , example("""SemanticVersion.parse("1.0.5+001") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExampleMeta6)
    , example("""SemanticVersion.parse("1.0.5-beta+1234") should return SementicVersion(Major(1), Minor(0), Patch(5), None, Some(build meta info)""", parseExamplePreMeta1)
    , example("""SemanticVersion.parse("1.0.5-a.3.7.xyz+a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePreMeta2)
    , example("""SemanticVersion.parse("1.0.5-a-b.xyz+a-b.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""", parseExamplePreMeta3)
    , property("SemanticVersion(same) == SemanticVersion(same) should be true", testSemanticVersionEqual)
    , property("SemanticVersion(less).compare(SemanticVersion(greater)) should the value less than 0", testSemanticVersionLess)
    , property("SemanticVersion(greater).compare(SemanticVersion(less)) should the value more than 0", testSemanticVersionGreater)
    , property("SemanticVersion round trip", roundTripSemanticVersion)
    )

  def parseExample1: Result = {
    val input = "1.0.5"
    val expected = Right(SemanticVersion(Major(1), Minor(0), Patch(5), None, None))
    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExamplePre1: Result = {
    val input = "1.0.5-beta"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExamplePre2: Result = {
    val input = "1.0.5-a.3.7.xyz"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExamplePre3: Result = {
    val input = "1.0.5-a-b.xyz"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExamplePre4: Result = {
    val input = "1.0.5-0"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExamplePre5: Result = {
    val input = "1.0.5-000a"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExampleMeta1: Result = {
    val input = "1.0.5+1234"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExampleMeta2: Result = {
    val input = "1.0.5+a.3.7.xyz"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExampleMeta3: Result = {
    val input = "1.0.5+a-b.xyz"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExampleMeta4: Result = {
    val input = "1.0.5+0"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExampleMeta5: Result = {
    val input = "1.0.5+000a"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExampleMeta6: Result = {
    val input = "1.0.5+001"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta1: Result = {
    val input = "1.0.5-beta+1234"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta2: Result = {
    val input = "1.0.5-a.3.7.xyz+a.3.7.xyz"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta3: Result = {
    val input = "1.0.5-a-b.xyz+a-b.xyz"
    val expected =
      Right(
        SemanticVersion(
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

    val actual = SemanticVersion.parse(input)
    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSemanticVersionEqual: Property = for {
    v <- genSemanticVersion.log("v")
  } yield {
    Result.assert(v == v).log("v == v")
  }

  def testSemanticVersionLess: Property = for {
    v1AndV2 <- genMinMaxSemanticVersions.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.assert(v1.compare(v2) < 0)
  }

  def testSemanticVersionGreater: Property = for {
    v1AndV2 <- genMinMaxSemanticVersions.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.assert(v2.compare(v1) > 0)
  }

  def roundTripSemanticVersion: Property = for {
    semanticVersion <- genSemanticVersion.log("semanticVersion")
  } yield {
    val rendered = semanticVersion.render
    val actual = SemanticVersion.parse(rendered)
    actual ==== Right(semanticVersion)
  }

}