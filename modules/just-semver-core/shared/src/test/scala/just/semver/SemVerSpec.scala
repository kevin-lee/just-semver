package just.semver

import Anh.{alphabet, hyphen, num, numFromStringUnsafe}
import hedgehog._
import hedgehog.runner._
import just.Common._
import just.decver.{DecVer, Gens => DecVerGens}
import just.semver.SemVer.{Major, Minor, Patch}
import just.semver.expr.ComparisonOperator
import just.semver.matcher.{SemVerComparison, SemVerMatcher, SemVerMatchers, Gens => MatcherGens}

/** @author
  *   Kevin Lee
  * @since
  *   2018-11-04
  */
object SemVerSpec extends Properties {
  override def tests: List[Test] = List(
    example(
      """SemVer.parse("1.0.5") should return SementicVersion(Major(1), Minor(0), Patch(5), None, None)""",
      parseExample1
    ),
    example(
      """SemVer.parse("1.0.5-beta") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""",
      parseExamplePre1
    ),
    example(
      """SemVer.parse("1.0.5-a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""",
      parseExamplePre2
    ),
    example(
      """SemVer.parse("1.0.5-a-b.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""",
      parseExamplePre3
    ),
    example(
      """SemVer.parse("1.0.5-0") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""",
      parseExamplePre4
    ),
    example(
      """SemVer.parse("1.0.5-000a") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""",
      parseExamplePre5
    ),
    example("""SemVer.parse("1.0.5-001") should return Left(Invalid)""", parseExamplePreInvalid1),
    example(
      """SemVer.parse("1.0.5+1234") should return SementicVersion(Major(1), Minor(0), Patch(5), None, Some(build meta info)""",
      parseExampleMeta1
    ),
    example(
      """SemVer.parse("1.0.5+a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""",
      parseExampleMeta2
    ),
    example(
      """SemVer.parse("1.0.5+a-b.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""",
      parseExampleMeta3
    ),
    example(
      """SemVer.parse("1.0.5+0") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""",
      parseExampleMeta4
    ),
    example(
      """SemVer.parse("1.0.5+000a") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""",
      parseExampleMeta5
    ),
    example(
      """SemVer.parse("1.0.5+001") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""",
      parseExampleMeta6
    ),
    example(
      """SemVer.parse("1.0.5-beta+1234") should return SementicVersion(Major(1), Minor(0), Patch(5), None, Some(build meta info)""",
      parseExamplePreMeta1
    ),
    example(
      """SemVer.parse("1.0.5-a.3.7.xyz+a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""",
      parseExamplePreMeta2
    ),
    example(
      """SemVer.parse("1.0.5-a-b.xyz+a-b.xyz") should return SementicVersion(Major(1), Minor(0), Patch(5), Some(with pre-release info), None)""",
      parseExamplePreMeta3
    ),
    property(
      s"""test SemVer.semVer(major, minor, patch) should be equal to SemVer.parse(s"$${major.major}.$${minor.minor}.$${patch.patch}")""",
      testSemVerSemVer
    ),
    property("SemVer.increaseMajor", testSemVerIncreaseMajor),
    property("SemVer.increaseMinor", testSemVerIncreaseMinor),
    property("SemVer.increasePatch", testSemVerIncreasePatch),
    property("SemVer(same) == SemVer(same) should be true", testSemVerEqual),
    property("SemVer(different) == SemVer(different) should be false", testSemVerEqualDiffCase),
    property("SemVer(same) != SemVer(same) should be false", testSemVerNotEqualSameCase),
    property("SemVer(different) != SemVer(different) should be true", testSemVerNotEqualDiffCase),
    property("SemVer(same).compare(SemVer(same)) should be 0", testSemVerCompareEqualCase),
    property("SemVer(less).compare(SemVer(greater)) should the value less than 0", testSemVerCompareLess),
    property("SemVer(greater).compare(SemVer(less)) should the value more than 0", testSemVerCompareGreater),
    property("SemVer(less) < SemVer(greater) should be true", testSemVerLessTrue),
    property("SemVer(same) < SemVer(same) should be false", testSemVerLessFalseForSame),
    property("SemVer(greater) < SemVer(less) should be false", testSemVerLessFalse),
    property("SemVer(less) <= SemVer(greater) should be true", testSemVerLessOrEqualTrue),
    property("SemVer(same) <= SemVer(same) should be true", testSemVerLessOrEqualTrueForSame),
    property("SemVer(greater) <= SemVer(less) should be false", testSemVerLessOrEqualFalse),
    property("SemVer(greater) > SemVer(less) should be true", testSemVerGreaterTrue),
    property("SemVer(same) > SemVer(same) should be false", testSemVerGreaterFalseForSame),
    property("SemVer(less) > SemVer(greater) should be false", testSemVerGreaterFalse),
    property("SemVer(greater) >= SemVer(less) should be true", testSemVerGreaterOrEqualTrue),
    property("SemVer(same) >= SemVer(same) should be true", testSemVerGreaterOrEqualTrueForSame),
    property("SemVer(less) >= SemVer(greater) should be false", testSemVerGreaterOrEqualFalse),
    property("SemVer round trip SemVer.render(semVer)", roundTripSemVerRenderSemVer),
    property("SemVer round trip semVer.render", roundTripSemVerRender),
    property(
      "SemVer.majorMinorPatch(semVer) should return (SemVer.Major, SemVer.Minor, SemVer.Patch)",
      testSemVerMajorMinorPatch
    ),
    property("test Isomorphism: parse <=> render", testIsomorphismParseRender),
    property("test Isomorphism: parseUnsafe <=> render", testIsomorphismParseUnsafeRender)
  ) ++ List(
    example(
      "test  Example-1 SemVer(1.0.0).matches(SemVerMatchers(1.0.0 - 2.0.0)) should return true",
      MatchesSpec.testExample1
    ),
    example(
      "test  Example-2 SemVer(2.0.0).matches(SemVerMatchers(1.0.0 - 2.0.0)) should return true",
      MatchesSpec.testExample2
    ),
    example(
      "test  Example-3 SemVer(1.0.1).matches(SemVerMatchers(1.0.0 - 2.0.0)) should return true",
      MatchesSpec.testExample3
    ),
    example(
      "test  Example-4 SemVer(1.999.999).matches(SemVerMatchers(1.0.0 - 2.0.0)) should return true",
      MatchesSpec.testExample4
    ),
    example(
      "test  Example-5 SemVer(1.0.0).matches(SemVerMatchers(>1.0.0 <2.0.0)) should return false",
      MatchesSpec.testExample5
    ),
    example(
      "test  Example-6 SemVer(2.0.0).matches(SemVerMatchers(>1.0.0 <2.0.0)) should return false",
      MatchesSpec.testExample6
    ),
    example(
      "test  Example-7 SemVer(1.0.1).matches(SemVerMatchers(>1.0.0 <2.0.0)) should return true",
      MatchesSpec.testExample7
    ),
    example(
      "test  Example-8 SemVer(1.999.999).matches(SemVerMatchers(>1.0.0 <2.0.0)) should return true",
      MatchesSpec.testExample8
    ),
    example(
      "test  Example-9 SemVer(1.0.0).matches(SemVerMatchers(>=1.0.0 <=2.0.0)) should return true",
      MatchesSpec.testExample5
    ),
    example(
      "test Example-10 SemVer(2.0.0).matches(SemVerMatchers(>=1.0.0 <=2.0.0)) should return true",
      MatchesSpec.testExample6
    ),
    example(
      "test Example-11 SemVer(1.0.1).matches(SemVerMatchers(>=1.0.0 <=2.0.0)) should return true",
      MatchesSpec.testExample7
    ),
    example(
      "test Example-12 SemVer(1.999.999).matches(SemVerMatchers(>=1.0.0 <=2.0.0)) should return true",
      MatchesSpec.testExample8
    ),
    property(
      "test SemVer(Valid).matches(SemVerMatchers(Range || Comparison))",
      MatchesSpec.testSemVerValidMatchesSemVerMatchersRangeOrComparison
    ),
    property(
      "test SemVer(Valid).matches(SemVerMatchers(Comparison and Comparison))",
      MatchesSpec.testSemVerValidMatchesSemVerMatchersComparisonAndComparison
    ),
    property(
      "test SemVer.matches(SemVerMatchers(Range || Comparison and Comparison))",
      MatchesSpec.testSemVerValidMatchesSemVerMatchersRangeOrComparisonAndComparison
    )
  ) ++ List(
    example(
      "test  Example-1 SemVer(1.0.0).unsafeMatches(1.0.0 - 2.0.0) should return true",
      UnsafeMatchesSpec.testExample1
    ),
    example(
      "test  Example-2 SemVer(2.0.0).unsafeMatches(1.0.0 - 2.0.0) should return true",
      UnsafeMatchesSpec.testExample2
    ),
    example(
      "test  Example-3 SemVer(1.0.1).unsafeMatches(1.0.0 - 2.0.0) should return true",
      UnsafeMatchesSpec.testExample3
    ),
    example(
      "test  Example-4 SemVer(1.999.999).unsafeMatches(1.0.0 - 2.0.0) should return true",
      UnsafeMatchesSpec.testExample4
    ),
    example(
      "test  Example-5 SemVer(1.0.0).unsafeMatches(>1.0.0 <2.0.0) should return false",
      UnsafeMatchesSpec.testExample5
    ),
    example(
      "test  Example-6 SemVer(2.0.0).unsafeMatches(>1.0.0 <2.0.0) should return false",
      UnsafeMatchesSpec.testExample6
    ),
    example(
      "test  Example-7 SemVer(1.0.1).unsafeMatches(>1.0.0 <2.0.0) should return true",
      UnsafeMatchesSpec.testExample7
    ),
    example(
      "test  Example-8 SemVer(1.999.999).unsafeMatches(>1.0.0 <2.0.0) should return true",
      UnsafeMatchesSpec.testExample8
    ),
    example(
      "test  Example-9 SemVer(1.0.0).unsafeMatches(>=1.0.0 <=2.0.0) should return true",
      UnsafeMatchesSpec.testExample5
    ),
    example(
      "test Example-10 SemVer(2.0.0).unsafeMatches(>=1.0.0 <=2.0.0) should return true",
      UnsafeMatchesSpec.testExample6
    ),
    example(
      "test Example-11 SemVer(1.0.1).unsafeMatches(>=1.0.0 <=2.0.0) should return true",
      UnsafeMatchesSpec.testExample7
    ),
    example(
      "test Example-12 SemVer(1.999.999).unsafeMatches(>=1.0.0 <=2.0.0) should return true",
      UnsafeMatchesSpec.testExample8
    ),
    property(
      "test SemVer(Valid).unsafeMatches(Range || Comparison)",
      UnsafeMatchesSpec.testSemVerValidMatchesSemVerMatchersRangeOrComparison
    ),
    property(
      "test SemVer(Valid).unsafeMatches(Comparison and Comparison)",
      UnsafeMatchesSpec.testSemVerValidMatchesSemVerMatchersComparisonAndComparison
    ),
    property(
      "test SemVer.unsafeMatches(Range || Comparison and Comparison)",
      UnsafeMatchesSpec.testSemVerValidMatchesSemVerMatchersRangeOrComparisonAndComparison
    ),
    property(
      "SemVer(major, minor, patch).toDecVer should return DecVer(major, minor)",
      testSemVerToDecVer
    ),
    property(
      "SemVer.fromDecVer(DecVer(major, minor)) should return SemVer(major, minor, 0)",
      testSemVerFromDecVer
    )
  )

  def parseExample1: Result = {
    val input    = "1.0.5"
    val expected = Right(SemVer(Major(1), Minor(0), Patch(5), None, None))
    val actual   = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre1: Result = {
    val input    = "1.0.5-beta"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        Option(
          AdditionalInfo.PreRelease(List(Dsv(List(alphabet("beta")))))
        ),
        None
      )
    )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre2: Result = {
    val input    = "1.0.5-a.3.7.xyz"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        Option(
          AdditionalInfo.PreRelease(
            List(
              Dsv(
                List[Anh](alphabet("a"))
              ),
              Dsv(
                List[Anh](num(3))
              ),
              Dsv(
                List[Anh](num(7))
              ),
              Dsv(
                List[Anh](alphabet("xyz"))
              )
            )
          )
        ),
        None
      )
    )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre3: Result = {
    val input    = "1.0.5-a-b.xyz"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        Option(
          AdditionalInfo.PreRelease(
            List(
              Dsv(
                List[Anh](alphabet("a"), hyphen, alphabet("b"))
              ),
              Dsv(
                List[Anh](alphabet("xyz"))
              )
            )
          )
        ),
        None
      )
    )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre4: Result = {
    val input    = "1.0.5-0"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        Option(
          AdditionalInfo.PreRelease(
            List(
              Dsv(
                List[Anh](num(0))
              )
            )
          )
        ),
        None
      )
    )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre5: Result = {
    val input    = "1.0.5-000a"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        Option(
          AdditionalInfo.PreRelease(
            List(
              Dsv(
                List[Anh](numFromStringUnsafe("000"), alphabet("a"))
              )
            )
          )
        ),
        None
      )
    )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePreInvalid1: Result = {
    val input    = "1.0.5-001"
    val expected = Left(
      ParseError.preReleaseParseError(
        ParseError.leadingZeroNumError("001")
      )
    )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta1: Result = {
    val input    = "1.0.5+1234"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        None,
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(Dsv(List(num(1234))))
          )
        )
      )
    )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta2: Result = {
    val input    = "1.0.5+a.3.7.xyz"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        None,
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](alphabet("a"))
              ),
              Dsv(
                List[Anh](num(3))
              ),
              Dsv(
                List[Anh](num(7))
              ),
              Dsv(
                List[Anh](alphabet("xyz"))
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
    val input    = "1.0.5+a-b.xyz"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        None,
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](alphabet("a"), hyphen, alphabet("b"))
              ),
              Dsv(
                List[Anh](alphabet("xyz"))
              )
            )
          )
        )
      )
    )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta4: Result = {
    val input    = "1.0.5+0"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        None,
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](num(0))
              )
            )
          )
        )
      )
    )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta5: Result = {
    val input    = "1.0.5+000a"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        None,
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](numFromStringUnsafe("000"), alphabet("a"))
              )
            )
          )
        )
      )
    )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta6: Result = {
    val input    = "1.0.5+001"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        None,
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](numFromStringUnsafe("001"))
              )
            )
          )
        )
      )
    )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta1: Result = {
    val input    = "1.0.5-beta+1234"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        Option(
          AdditionalInfo.PreRelease(
            List(Dsv(List(alphabet("beta"))))
          )
        ),
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(Dsv(List(num(1234))))
          )
        )
      )
    )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta2: Result = {
    val input    = "1.0.5-a.3.7.xyz+a.3.7.xyz"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        Option(
          AdditionalInfo.PreRelease(
            List(
              Dsv(
                List[Anh](alphabet("a"))
              ),
              Dsv(
                List[Anh](num(3))
              ),
              Dsv(
                List[Anh](num(7))
              ),
              Dsv(
                List[Anh](alphabet("xyz"))
              )
            )
          )
        ),
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](alphabet("a"))
              ),
              Dsv(
                List[Anh](num(3))
              ),
              Dsv(
                List[Anh](num(7))
              ),
              Dsv(
                List[Anh](alphabet("xyz"))
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
    val input    = "1.0.5-a-b.xyz+a-b.xyz"
    val expected = Right(
      SemVer(
        Major(1),
        Minor(0),
        Patch(5),
        Option(
          AdditionalInfo.PreRelease(
            List(
              Dsv(
                List[Anh](alphabet("a"), hyphen, alphabet("b"))
              ),
              Dsv(
                List[Anh](alphabet("xyz"))
              )
            )
          )
        ),
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](alphabet("a"), hyphen, alphabet("b"))
              ),
              Dsv(
                List[Anh](alphabet("xyz"))
              )
            )
          )
        )
      )
    )

    val actual = SemVer.parse(input)
    actual ==== expected
  }

  def testSemVerSemVer: Property = for {
    major <- Gens.genMajor.log("major")
    minor <- Gens.genMinor.log("major")
    patch <- Gens.genPatch.log("major")
  } yield {
    SemVer.semVer(major, minor, patch).asRight[ParseError] ====
      SemVer.parse(s"${major.value.toString}.${minor.value.toString}.${patch.value.toString}")
  }

  def testSemVerIncreaseMajor: Property = for {
    v <- Gens.genSemVer.log("v")
  } yield {
    val expected = v.copy(major = Major(v.major.value + 1))
    val actual   = SemVer.increaseMajor(v)
    actual ==== expected
  }

  def testSemVerIncreaseMinor: Property = for {
    v <- Gens.genSemVer.log("v")
  } yield {
    val expected = v.copy(minor = Minor(v.minor.value + 1))
    val actual   = SemVer.increaseMinor(v)
    actual ==== expected
  }

  def testSemVerIncreasePatch: Property = for {
    v <- Gens.genSemVer.log("v")
  } yield {
    val expected = v.copy(patch = Patch(v.patch.value + 1))
    val actual   = SemVer.increasePatch(v)
    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSemVerEqual: Property = for {
    v <- Gens.genSemVer.log("v")
  } yield {
    Result.diffNamed("Failed: v == v is not true", v, v)(_ == _)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSemVerEqualDiffCase: Property = for {
    v1AndV2 <- Gens.genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("Failed: v1(diff) == v2(dff) is not false", v1, v2)((x, y) => !(x == y))
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSemVerNotEqualSameCase: Property = for {
    v <- Gens.genSemVer.log("v")
  } yield {
    Result.diffNamed("Failed: v != v is not false", v, v)((x, y) => !(x != y))
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSemVerNotEqualDiffCase: Property = for {
    v1AndV2 <- Gens.genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("Failed: v1(diff) != v2(dff) is not true", v1, v2)(_ != _)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSemVerCompareEqualCase: Property = for {
    v <- Gens.genSemVer.log("v")
  } yield {
    Result.diffNamed("Failed: v == v is not true", v, v)(_.compare(_) == 0)
  }

  def testSemVerCompareLess: Property = for {
    v1AndV2 <- Gens.genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.assert(v1.compare(v2) < 0)
  }

  def testSemVerCompareGreater: Property = for {
    v1AndV2 <- Gens.genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.assert(v2.compare(v1) > 0)
  }

  def testSemVerLessTrue: Property = for {
    v1AndV2 <- Gens.genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v1(less) < v2(greater) is not true ===", v1, v2)(_ < _)
  }

  def testSemVerLessFalseForSame: Property = for {
    v <- Gens.genSemVer.log("v")
  } yield {
    Result.diffNamed("=== Failed: v(same) < v(same) is not false ===", v, v)((x, y) => !(x < y))
  }

  def testSemVerLessFalse: Property = for {
    v1AndV2 <- Gens.genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v2(greater) < v1(less) is not false ===", v2, v1)((x, y) => !(x < y))
  }

  def testSemVerLessOrEqualTrue: Property = for {
    v1AndV2 <- Gens.genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v1(less) <= v2(greater) is not true ===", v1, v2)(_ <= _)
  }

  def testSemVerLessOrEqualTrueForSame: Property = for {
    v <- Gens.genSemVer.log("v")
  } yield {
    Result.diffNamed("=== Failed: v(same) <= v(same) is not true ===", v, v)(_ <= _)
  }

  def testSemVerLessOrEqualFalse: Property = for {
    v1AndV2 <- Gens.genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v2(greater) <= v1(less) is not false ===", v2, v1)((x, y) => !(x <= y))
  }

  def testSemVerGreaterTrue: Property = for {
    v1AndV2 <- Gens.genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v2(greater) > v1(less) is not true ===", v2, v1)(_ > _)
  }

  def testSemVerGreaterFalseForSame: Property = for {
    v <- Gens.genSemVer.log("v")
  } yield {
    Result.diffNamed("=== Failed: v(same) > v(same) is not false ===", v, v)((x, y) => !(x > y))
  }

  def testSemVerGreaterFalse: Property = for {
    v1AndV2 <- Gens.genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v1(less) > v2(greater) is not false ===", v1, v2)((x, y) => !(x > y))
  }

  def testSemVerGreaterOrEqualTrue: Property = for {
    v1AndV2 <- Gens.genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v2(greater) >= v1(less) is not true ===", v2, v1)(_ >= _)
  }

  def testSemVerGreaterOrEqualTrueForSame: Property = for {
    v <- Gens.genSemVer.log("v")
  } yield {
    Result.diffNamed("=== Failed: v(same) >= v(same) is not true ===", v, v)(_ >= _)
  }

  def testSemVerGreaterOrEqualFalse: Property = for {
    v1AndV2 <- Gens.genMinMaxSemVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v1(less) >= v2(greater) is not false ===", v1, v2)((x, y) => !(x >= y))
  }

  def roundTripSemVerRenderSemVer: Property = for {
    semVer <- Gens.genSemVer.log("semVer")
  } yield {
    val rendered = SemVer.render(semVer)
    val actual   = SemVer.parse(rendered)
    actual ==== Right(semVer)
  }

  def roundTripSemVerRender: Property = for {
    semVer <- Gens.genSemVer.log("semVer")
  } yield {
    val rendered = semVer.render
    val actual   = SemVer.parse(rendered)
    actual ==== Right(semVer)
  }

  def testSemVerMajorMinorPatch: Property = for {
    semVer <- Gens.genSemVer.log("semVer")
  } yield {
    val expected = (semVer.major, semVer.minor, semVer.patch)
    val actual   = SemVer.majorMinorPatch(semVer)
    actual ==== expected
  }

  def testIsomorphismParseRender: Property = for {
    semVer <- Gens.genSemVer.log("semVer")
  } yield {
    val input = semVer.render
    SemVer.parse(input) match {
      case Right(actual) =>
        val expected = semVer
        actual ==== expected

      case Left(err) =>
        Result.failure.log(s"SemVer.parse failed with error: ${err.render}")
    }
  }

  def testIsomorphismParseUnsafeRender: Property = for {
    semVer <- Gens.genSemVer.log("semVer")
  } yield {
    val input    = semVer.render
    val actual   = SemVer.unsafeParse(input)
    val expected = semVer
    actual ==== expected
  }

  object MatchesSpec {

    def testExample1: Result = {
      Result
        .assert(SemVer.unsafeParse("1.0.0").matches(SemVerMatchers.unsafeParse("1.0.0 - 2.0.0")))
        .log("SemVerMatchers(1.0.0 - 2.0.0).matches(1.0.0) failed")
    }

    def testExample2: Result = {
      Result
        .assert(SemVer.unsafeParse("2.0.0").matches(SemVerMatchers.unsafeParse("1.0.0 - 2.0.0")))
        .log("SemVerMatchers(1.0.0 - 2.0.0).matches(2.0.0) failed")
    }

    def testExample3: Result = {
      Result
        .assert(SemVer.unsafeParse("1.0.1").matches(SemVerMatchers.unsafeParse("1.0.0 - 2.0.0")))
        .log("SemVerMatchers(1.0.0 - 2.0.0).matches(1.0.1) failed")
    }

    def testExample4: Result = {
      Result
        .assert(SemVer.unsafeParse("1.999.999").matches(SemVerMatchers.unsafeParse("1.0.0 - 2.0.0")))
        .log("SemVerMatchers(1.0.0 - 2.0.0).matches(1.999.999) failed")
    }

    def testExample5: Result = {
      Result
        .diff(SemVer.unsafeParse("1.0.0").matches(SemVerMatchers.unsafeParse(">1.0.0 <2.0.0")), false)(_ === _)
        .log("SemVerMatchers(>1.0.0 <2.0.0).matches(1.0.0) failed")
    }

    def testExample6: Result = {
      Result
        .diff(SemVer.unsafeParse("2.0.0").matches(SemVerMatchers.unsafeParse(">1.0.0 <2.0.0")), false)(_ === _)
        .log("SemVerMatchers(>1.0.0 <2.0.0).matches(2.0.0) failed")
    }

    def testExample7: Result = {
      Result
        .assert(SemVer.unsafeParse("1.0.1").matches(SemVerMatchers.unsafeParse(">1.0.0 <2.0.0")))
        .log("SemVerMatchers(>1.0.0 <2.0.0).matches(1.0.1) failed")
    }

    def testExample8: Result = {
      Result
        .assert(SemVer.unsafeParse("1.999.999").matches(SemVerMatchers.unsafeParse(">1.0.0 <2.0.0")))
        .log("SemVerMatchers(>1.0.0 <2.0.0).matches(1.999.999) failed")
    }

    def testExample9: Result = {
      Result
        .assert(SemVer.unsafeParse("1.0.0").matches(SemVerMatchers.unsafeParse(">=1.0.0 <=2.0.0")))
        .log("SemVerMatchers(>=1.0.0 <=2.0.0).matches(1.0.0) failed")
    }

    def testExample10: Result = {
      Result
        .assert(SemVer.unsafeParse("2.0.0").matches(SemVerMatchers.unsafeParse(">=1.0.0 <=2.0.0")))
        .log("SemVerMatchers(>=1.0.0 <=2.0.0).matches(2.0.0) failed")
    }

    def testExample11: Result = {
      Result
        .assert(SemVer.unsafeParse("1.0.1").matches(SemVerMatchers.unsafeParse(">=1.0.0 <=2.0.0")))
        .log("SemVerMatchers(>=1.0.0 <=2.0.0).matches(1.0.1) failed")
    }

    def testExample12: Result = {
      Result
        .assert(SemVer.unsafeParse("1.999.999").matches(SemVerMatchers.unsafeParse(">=1.0.0 <=2.0.0")))
        .log("SemVerMatchers(>=1.0.0 <=2.0.0).matches(1.999.999) failed")
    }

    def testSemVerValidMatchesSemVerMatchersRangeOrComparison: Property = for {
      major  <- Gen.int(Range.linear(5, 10)).log("major")
      minor  <- Gen.int(Range.linear(1, 10)).log("minor")
      patch  <- Gen.int(Range.linear(1, 100)).log("patch")
      major2 <- Gen.int(Range.linear(5, 10)).map(_ + major).log("major2")
      minor2 <- Gen.int(Range.linear(1, 10)).map(_ + minor).log("minor2")
      patch2 <- Gen.int(Range.linear(1, 100)).map(_ + patch).log("patch2")
      op     <- MatcherGens.genComparisonOperator.log("op")
    } yield {
      val semVer = SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch))

      val v1       = SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor - 1), SemVer.Patch(patch - 1))
      val v2       = SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor + 1), SemVer.Patch(patch + 1))
      val matcher1 = SemVerMatcher.range(v1, v2)

      val semVer2  = SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2), SemVer.Patch(patch2))
      val matcher2 = SemVerMatcher.comparison(SemVerComparison(op, semVer2))

      // format: off
      val versions = op match {
        case ComparisonOperator.Lt =>
          List(
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2),     SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2 - 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2),     SemVer.Patch(patch2 - 1)),
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2 - 1), SemVer.Patch(patch2 - 1))
          )
        case ComparisonOperator.Le =>
          List(
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2),     SemVer.Patch(    patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2 - 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2),     SemVer.Patch(    patch2 - 1)),
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2 - 1), SemVer.Patch(patch2 - 1)),
            semVer2.copy()
          )
        case ComparisonOperator.Eql =>
          List(
            semVer2.copy()
          )
        case ComparisonOperator.Ne =>
          List(
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2),     SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2 - 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2),     SemVer.Patch(patch2 - 1)),
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2 - 1), SemVer.Patch(patch2 - 1)),
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2),     SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2 + 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2),     SemVer.Patch(patch2 + 1)),
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2 + 1), SemVer.Patch(patch2 + 1))
          )
        case ComparisonOperator.Gt =>
          List(
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2),     SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2 + 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2),     SemVer.Patch(patch2 + 1)),
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2 + 1), SemVer.Patch(patch2 + 1))
          )
        case ComparisonOperator.Ge =>
          List(
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2),     SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2 + 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2),     SemVer.Patch(patch2 + 1)),
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2 + 1), SemVer.Patch(patch2 + 1)),
            semVer2.copy()
          )
      }
      // format: on

      val semVerMatchers = SemVerMatchers.unsafeParse(s"${matcher1.render} || ${matcher2.render}")

      println(
        s"""# Range || Comparison:
           |> matchers: ${semVerMatchers.render}
           |>   semVer: ${semVer.render}
           |>  semVers: ${versions.map(_.render).mkString("[\n>    - ", "\n>    - ", "\n>  ]")}
           |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(semVer.matches(semVerMatchers))
            .log(
              s""" Range || Comparison - range test failed
                 |> matchers: ${semVerMatchers.render}
                 |>   semVer: ${semVer.render}
                 |""".stripMargin
            ),
          Result
            .assert(versions.forall(v => v.matches(semVerMatchers)))
            .log(
              s""" Range || Comparison - comparison test failed
                 |> matchers: ${semVerMatchers.render}
                 |>  semVers: ${versions.map(_.render).mkString("[\n>    - ", "\n>    - ", "\n>  ]")}
                 |""".stripMargin
            )
        )
      )
    }

    def testSemVerValidMatchesSemVerMatchersComparisonAndComparison: Property = for {
      v1V2SemVer <- MatcherGens
                      .genRangedSemVerComparison(
                        Range.linear(11, 30),
                        Range.linear(11, 50),
                        Range.linear(11, 100)
                      )
                      .log("(v1, v2, semVer)")
      (v1, v2, semVer) = v1V2SemVer
    } yield {
      val semVerMatchers = SemVerMatchers.unsafeParse(s"${v1.render} ${v2.render}")

      println(
        s"""# Comparison and Comparison
           |> matchers: ${semVerMatchers.render}
           |>   semVer: ${semVer.render}
           |""".stripMargin
      )

      Result
        .assert(semVer.matches(semVerMatchers))
        .log(
          s""" Comparison and Comparison - failed
             |> matchers: ${semVerMatchers.render}
             |>   semVer: ${semVer.render}
             |""".stripMargin
        )
    }

    def testSemVerValidMatchesSemVerMatchersRangeOrComparisonAndComparison: Property = for {
      rangeMatcherSemVerInRange <- MatcherGens
                                     .genSemVerMatcherRangeAndSemVerInRange(
                                       majorRange = Range.linear(11, 30),
                                       minorRange = Range.linear(11, 50),
                                       patchRange = Range.linear(11, 100)
                                     )
                                     .log("(rangeMatcher, semVerInRange)")
      (rangeMatcher, semVerInRange) = rangeMatcherSemVerInRange
      v1V2SemVer <- MatcherGens
                      .genRangedSemVerComparison(
                        majorRange = Range.linear(31, 100),
                        minorRange = Range.linear(51, 100),
                        patchRange = Range.linear(101, 1000)
                      )
                      .log("(v1, v2, semVer)")
      (v1, v2, semVer) = v1V2SemVer
    } yield {
      val semVerMatchers = SemVerMatchers.unsafeParse(s"${rangeMatcher.render} || ${v1.render} ${v2.render}")

      println(
        s"""# Range || Comparison and Comparison
           |>      matchers: ${semVerMatchers.render}
           |> semVerInRange: ${semVerInRange.render}
           |>  semVerInComp: ${semVer.render}
           |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(semVerInRange.matches(semVerMatchers))
            .log(
              s""" Range || Comparison and Comparison - testing range failed
                 |>      matchers: ${semVerMatchers.render}
                 |> semVerInRange: ${semVerInRange.render}
                 |""".stripMargin
            ),
          Result
            .assert(semVer.matches(semVerMatchers))
            .log(
              s""" Range || Comparison and Comparison - testing (comparison and comparison) failed
                 |>     matchers: ${semVerMatchers.render}
                 |> semVerInComp: ${semVer.render}
                 |""".stripMargin
            )
        )
      )
    }

  }

  object UnsafeMatchesSpec {

    def testExample1: Result = {
      Result
        .assert(SemVer.unsafeParse("1.0.0").unsafeMatches("1.0.0 - 2.0.0"))
        .log("SemVerMatchers(1.0.0 - 2.0.0).matches(1.0.0) failed")
    }

    def testExample2: Result = {
      Result
        .assert(SemVer.unsafeParse("2.0.0").unsafeMatches("1.0.0 - 2.0.0"))
        .log("SemVerMatchers(1.0.0 - 2.0.0).matches(2.0.0) failed")
    }

    def testExample3: Result = {
      Result
        .assert(SemVer.unsafeParse("1.0.1").unsafeMatches("1.0.0 - 2.0.0"))
        .log("SemVerMatchers(1.0.0 - 2.0.0).matches(1.0.1) failed")
    }

    def testExample4: Result = {
      Result
        .assert(SemVer.unsafeParse("1.999.999").unsafeMatches("1.0.0 - 2.0.0"))
        .log("SemVerMatchers(1.0.0 - 2.0.0).matches(1.999.999) failed")
    }

    def testExample5: Result = {
      Result
        .diff(SemVer.unsafeParse("1.0.0").unsafeMatches(">1.0.0 <2.0.0"), false)(_ === _)
        .log("SemVerMatchers(>1.0.0 <2.0.0).matches(1.0.0) failed")
    }

    def testExample6: Result = {
      Result
        .diff(SemVer.unsafeParse("2.0.0").unsafeMatches(">1.0.0 <2.0.0"), false)(_ === _)
        .log("SemVerMatchers(>1.0.0 <2.0.0).matches(2.0.0) failed")
    }

    def testExample7: Result = {
      Result
        .assert(SemVer.unsafeParse("1.0.1").unsafeMatches(">1.0.0 <2.0.0"))
        .log("SemVerMatchers(>1.0.0 <2.0.0).matches(1.0.1) failed")
    }

    def testExample8: Result = {
      Result
        .assert(SemVer.unsafeParse("1.999.999").unsafeMatches(">1.0.0 <2.0.0"))
        .log("SemVerMatchers(>1.0.0 <2.0.0).matches(1.999.999) failed")
    }

    def testExample9: Result = {
      Result
        .assert(SemVer.unsafeParse("1.0.0").unsafeMatches(">=1.0.0 <=2.0.0"))
        .log("SemVerMatchers(>=1.0.0 <=2.0.0).matches(1.0.0) failed")
    }

    def testExample10: Result = {
      Result
        .assert(SemVer.unsafeParse("2.0.0").unsafeMatches(">=1.0.0 <=2.0.0"))
        .log("SemVerMatchers(>=1.0.0 <=2.0.0).matches(2.0.0) failed")
    }

    def testExample11: Result = {
      Result
        .assert(SemVer.unsafeParse("1.0.1").unsafeMatches(">=1.0.0 <=2.0.0"))
        .log("SemVerMatchers(>=1.0.0 <=2.0.0).matches(1.0.1) failed")
    }

    def testExample12: Result = {
      Result
        .assert(SemVer.unsafeParse("1.999.999").unsafeMatches(">=1.0.0 <=2.0.0"))
        .log("SemVerMatchers(>=1.0.0 <=2.0.0).matches(1.999.999) failed")
    }

    def testSemVerValidMatchesSemVerMatchersRangeOrComparison: Property = for {
      major  <- Gen.int(Range.linear(5, 10)).log("major")
      minor  <- Gen.int(Range.linear(1, 10)).log("minor")
      patch  <- Gen.int(Range.linear(1, 100)).log("patch")
      major2 <- Gen.int(Range.linear(5, 10)).map(_ + major).log("major2")
      minor2 <- Gen.int(Range.linear(1, 10)).map(_ + minor).log("minor2")
      patch2 <- Gen.int(Range.linear(1, 100)).map(_ + patch).log("patch2")
      op     <- MatcherGens.genComparisonOperator.log("op")
    } yield {
      val semVer = SemVer.semVer(SemVer.Major(major), SemVer.Minor(minor), SemVer.Patch(patch))

      val v1       = SemVer.semVer(SemVer.Major(major - 1), SemVer.Minor(minor - 1), SemVer.Patch(patch - 1))
      val v2       = SemVer.semVer(SemVer.Major(major + 1), SemVer.Minor(minor + 1), SemVer.Patch(patch + 1))
      val matcher1 = SemVerMatcher.range(v1, v2)

      val semVer2  = SemVer.semVer(SemVer.Major(major2), SemVer.Minor(minor2), SemVer.Patch(patch2))
      val matcher2 = SemVerMatcher.comparison(SemVerComparison(op, semVer2))

      // format: off
      val versions = op match {
        case ComparisonOperator.Lt =>
          List(
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2),     SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2 - 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2),     SemVer.Patch(patch2 - 1)),
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2 - 1), SemVer.Patch(patch2 - 1))
          )
        case ComparisonOperator.Le =>
          List(
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2),     SemVer.Patch(    patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2 - 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2),     SemVer.Patch(    patch2 - 1)),
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2 - 1), SemVer.Patch(patch2 - 1)),
            semVer2.copy()
          )
        case ComparisonOperator.Eql =>
          List(
            semVer2.copy()
          )
        case ComparisonOperator.Ne =>
          List(
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2),     SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2 - 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2),     SemVer.Patch(patch2 - 1)),
            SemVer.semVer(SemVer.Major(major2 - 1), SemVer.Minor(minor2 - 1), SemVer.Patch(patch2 - 1)),
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2),     SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2 + 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2),     SemVer.Patch(patch2 + 1)),
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2 + 1), SemVer.Patch(patch2 + 1))
          )
        case ComparisonOperator.Gt =>
          List(
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2),     SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2 + 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2),     SemVer.Patch(patch2 + 1)),
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2 + 1), SemVer.Patch(patch2 + 1))
          )
        case ComparisonOperator.Ge =>
          List(
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2),     SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2 + 1), SemVer.Patch(patch2)),
            SemVer.semVer(SemVer.Major(major2),     SemVer.Minor(minor2),     SemVer.Patch(patch2 + 1)),
            SemVer.semVer(SemVer.Major(major2 + 1), SemVer.Minor(minor2 + 1), SemVer.Patch(patch2 + 1)),
            semVer2.copy()
          )
      }
      // format: on

      val semVerMatchers = s"${matcher1.render} || ${matcher2.render}"

      println(
        s"""# Range || Comparison:
           |> matchers: ${semVerMatchers}
           |>   semVer: ${semVer.render}
           |>  semVers: ${versions.map(_.render).mkString("[\n>    - ", "\n>    - ", "\n>  ]")}
           |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(semVer.unsafeMatches(semVerMatchers))
            .log(
              s""" Range || Comparison - range test failed
                 |> matchers: $semVerMatchers
                 |>   semVer: ${semVer.render}
                 |""".stripMargin
            ),
          Result
            .assert(versions.forall(v => v.unsafeMatches(semVerMatchers)))
            .log(
              s""" Range || Comparison - comparison test failed
                 |> matchers: $semVerMatchers
                 |>  semVers: ${versions.map(_.render).mkString("[\n>    - ", "\n>    - ", "\n>  ]")}
                 |""".stripMargin
            )
        )
      )
    }

    def testSemVerValidMatchesSemVerMatchersComparisonAndComparison: Property = for {
      v1V2SemVer <- MatcherGens
                      .genRangedSemVerComparison(
                        Range.linear(11, 30),
                        Range.linear(11, 50),
                        Range.linear(11, 100)
                      )
                      .log("(v1, v2, semVer)")
      (v1, v2, semVer) = v1V2SemVer
    } yield {
      val semVerMatchers = s"${v1.render} ${v2.render}"

      println(
        s"""# Comparison and Comparison
           |> matchers: $semVerMatchers
           |>   semVer: ${semVer.render}
           |""".stripMargin
      )

      Result
        .assert(semVer.unsafeMatches(semVerMatchers))
        .log(
          s""" Comparison and Comparison - failed
             |> matchers: ${semVerMatchers}
             |>   semVer: ${semVer.render}
             |""".stripMargin
        )
    }

    def testSemVerValidMatchesSemVerMatchersRangeOrComparisonAndComparison: Property = for {
      rangeMatcherSemVerInRange <- MatcherGens
                                     .genSemVerMatcherRangeAndSemVerInRange(
                                       majorRange = Range.linear(11, 30),
                                       minorRange = Range.linear(11, 50),
                                       patchRange = Range.linear(11, 100)
                                     )
                                     .log("(rangeMatcher, semVerInRange)")
      (rangeMatcher, semVerInRange) = rangeMatcherSemVerInRange
      v1V2SemVer <- MatcherGens
                      .genRangedSemVerComparison(
                        majorRange = Range.linear(31, 100),
                        minorRange = Range.linear(51, 100),
                        patchRange = Range.linear(101, 1000)
                      )
                      .log("(v1, v2, semVer)")
      (v1, v2, semVer) = v1V2SemVer
    } yield {
      val semVerMatchers = s"${rangeMatcher.render} || ${v1.render} ${v2.render}"

      println(
        s"""# Range || Comparison and Comparison
           |>      matchers: $semVerMatchers
           |> semVerInRange: ${semVerInRange.render}
           |>  semVerInComp: ${semVer.render}
           |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(semVerInRange.unsafeMatches(semVerMatchers))
            .log(
              s""" Range || Comparison and Comparison - testing range failed
                 |>      matchers: $semVerMatchers
                 |> semVerInRange: ${semVerInRange.render}
                 |""".stripMargin
            ),
          Result
            .assert(semVer.unsafeMatches(semVerMatchers))
            .log(
              s""" Range || Comparison and Comparison - testing (comparison and comparison) failed
                 |>     matchers: $semVerMatchers
                 |> semVerInComp: ${semVer.render}
                 |""".stripMargin
            )
        )
      )
    }

  }

  def testSemVerToDecVer: Property = for {
    semVer <- Gens.genSemVer.log("semVer")
  } yield {
    val expected = DecVer(DecVer.Major(semVer.major.value), DecVer.Minor(semVer.minor.value))
    val actual   = semVer.toDecVer
    actual ==== expected
  }

  def testSemVerFromDecVer: Property = for {
    decVer <- DecVerGens.genDecVer.log("decVer")
  } yield {
    val expected = SemVer.semVer(SemVer.Major(decVer.major.value), SemVer.Minor(decVer.minor.value), SemVer.patch0)
    val actual   = SemVer.fromDecVer(decVer)
    actual ==== expected
  }

}
