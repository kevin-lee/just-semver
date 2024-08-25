package just.decver

import hedgehog._
import hedgehog.runner._
import just.Common._
import just.decver.DecVer.{Major, Minor}
import just.decver.matcher.{DecVerComparison, DecVerMatcher, DecVerMatchers, Gens => MatcherGens}
import just.semver.SemVer
import just.semver.{Gens => SemVerGens}
//import just.decver.{Gens => DecVerGens}
import just.semver.expr.ComparisonOperator
import just.semver.{AdditionalInfo, Anh, Dsv}

/** @author
  *   Kevin Lee
  * @since
  *   2018-11-04
  */
object DecVerSpec extends Properties {
  override def tests: List[Test] = List(
    example(
      """DecVer.parse("1.0") should return SementicVersion(Major(1), Minor(0), None, None)""",
      parseExample1
    ),
    example(
      """DecVer.parse("1.0-beta") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePre1
    ),
    example(
      """DecVer.parse("1.0-a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePre2
    ),
    example(
      """DecVer.parse("1.0-a-b.xyz") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePre3
    ),
    example(
      """DecVer.parse("1.0-0") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePre4
    ),
    example(
      """DecVer.parse("1.0-000a") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePre5
    ),
    example("""DecVer.parse("1.0-001") should return Left(Invalid)""", parseExamplePreInvalid1),
    example(
      """DecVer.parse("1.0+1234") should return SementicVersion(Major(1), Minor(0), None, Some(build meta info)""",
      parseExampleMeta1
    ),
    example(
      """DecVer.parse("1.0+a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExampleMeta2
    ),
    example(
      """DecVer.parse("1.0+a-b.xyz") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExampleMeta3
    ),
    example(
      """DecVer.parse("1.0+0") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExampleMeta4
    ),
    example(
      """DecVer.parse("1.0+000a") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExampleMeta5
    ),
    example(
      """DecVer.parse("1.0+001") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExampleMeta6
    ),
    example(
      """DecVer.parse("1.0-beta+1234") should return SementicVersion(Major(1), Minor(0), None, Some(build meta info)""",
      parseExamplePreMeta1
    ),
    example(
      """DecVer.parse("1.0-a.3.7.xyz+a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePreMeta2
    ),
    example(
      """DecVer.parse("1.0-a-b.xyz+a-b.xyz") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePreMeta3
    ),
    property(
      s"""test DecVer.withMajorMinor(major, minor) should be equal to DecVer.parse(s"$${major.major}.$${minor.minor}")""",
      testDecVerDecVer
    ),
    property("DecVer.increaseMajor", testDecVerIncreaseMajor),
    property("DecVer.increaseMinor", testDecVerIncreaseMinor),
    property("DecVer(same) == DecVer(same) should be true", testDecVerEqual),
    property("DecVer(different) == DecVer(different) should be false", testDecVerEqualDiffCase),
    property("DecVer(same) != DecVer(same) should be false", testDecVerNotEqualSameCase),
    property("DecVer(different) != DecVer(different) should be true", testDecVerNotEqualDiffCase),
    property("DecVer(same).compare(DecVer(same)) should be 0", testDecVerCompareEqualCase),
    property("DecVer(less).compare(DecVer(greater)) should the value less than 0", testDecVerCompareLess),
    property("DecVer(greater).compare(DecVer(less)) should the value more than 0", testDecVerCompareGreater),
    property("DecVer(less) < DecVer(greater) should be true", testDecVerLessTrue),
    property("DecVer(same) < DecVer(same) should be false", testDecVerLessFalseForSame),
    property("DecVer(greater) < DecVer(less) should be false", testDecVerLessFalse),
    property("DecVer(less) <= DecVer(greater) should be true", testDecVerLessOrEqualTrue),
    property("DecVer(same) <= DecVer(same) should be true", testDecVerLessOrEqualTrueForSame),
    property("DecVer(greater) <= DecVer(less) should be false", testDecVerLessOrEqualFalse),
    property("DecVer(greater) > DecVer(less) should be true", testDecVerGreaterTrue),
    property("DecVer(same) > DecVer(same) should be false", testDecVerGreaterFalseForSame),
    property("DecVer(less) > DecVer(greater) should be false", testDecVerGreaterFalse),
    property("DecVer(greater) >= DecVer(less) should be true", testDecVerGreaterOrEqualTrue),
    property("DecVer(same) >= DecVer(same) should be true", testDecVerGreaterOrEqualTrueForSame),
    property("DecVer(less) >= DecVer(greater) should be false", testDecVerGreaterOrEqualFalse),
    property("DecVer round trip DecVer.render(decVer)", roundTripDecVerRenderDecVer),
    property("DecVer round trip decVer.render", roundTripDecVerRender),
    property("test Isomorphism: parse <=> render", testIsomorphismParseRender),
    property("test Isomorphism: parseUnsafe <=> render", testIsomorphismParseUnsafeRender)
  ) ++ List(
    example(
      "test Example-1 DecVer(1.0).matches(DecVerMatchers(1.0 - 2.0)) should return true",
      MatchesSpec.testExample1
    ),
    example(
      "test Example-2 DecVer(2.0).matches(DecVerMatchers(1.0 - 2.0)) should return true",
      MatchesSpec.testExample2
    ),
    example(
      "test Example-3 DecVer(1.1).matches(DecVerMatchers(1.0 - 2.0)) should return true",
      MatchesSpec.testExample3
    ),
    example(
      "test Example-4 DecVer(1.999).matches(DecVerMatchers(1.0 - 2.0)) should return true",
      MatchesSpec.testExample4
    ),
    example(
      "test Example-5 DecVer(1.0).matches(DecVerMatchers(>1.0 <2.0)) should return false",
      MatchesSpec.testExample5
    ),
    example(
      "test Example-6 DecVer(2.0).matches(DecVerMatchers(>1.0 <2.0)) should return false",
      MatchesSpec.testExample6
    ),
    example(
      "test Example-7 DecVer(1.1).matches(DecVerMatchers(>1.0 <2.0)) should return true",
      MatchesSpec.testExample7
    ),
    example(
      "test Example-8 DecVer(1.999).matches(DecVerMatchers(>1.0 <2.0)) should return true",
      MatchesSpec.testExample8
    ),
    example(
      "test Example-9 DecVer(1.0).matches(DecVerMatchers(>=1.0 <=2.0)) should return true",
      MatchesSpec.testExample5
    ),
    example(
      "test Example-10 DecVer(2.0).matches(DecVerMatchers(>=1.0 <=2.0)) should return true",
      MatchesSpec.testExample6
    ),
    example(
      "test Example-11 DecVer(1.1).matches(DecVerMatchers(>=1.0 <=2.0)) should return true",
      MatchesSpec.testExample7
    ),
    example(
      "test Example-12 DecVer(1.999).matches(DecVerMatchers(>=1.0 <=2.0)) should return true",
      MatchesSpec.testExample8
    ),
    property(
      "test DecVer(Valid).matches(DecVerMatchers(Range || Comparison))",
      MatchesSpec.testDecVerValidMatchesDecVerMatchersRangeOrComparison
    ),
    property(
      "test DecVer(Valid).matches(DecVerMatchers(Comparison and Comparison))",
      MatchesSpec.testDecVerValidMatchesDecVerMatchersComparisonAndComparison
    ),
    property(
      "test DecVer.matches(DecVerMatchers(Range || Comparison and Comparison))",
      MatchesSpec.testDecVerValidMatchesDecVerMatchersRangeOrComparisonAndComparison
    )
  ) ++ List(
    example(
      "test Example-1 DecVer(1.0).unsafeMatches(1.0 - 2.0) should return true",
      UnsafeMatchesSpec.testExample1
    ),
    example(
      "test Example-2 DecVer(2.0).unsafeMatches(1.0 - 2.0) should return true",
      UnsafeMatchesSpec.testExample2
    ),
    example(
      "test Example-3 DecVer(1.1).unsafeMatches(1.0 - 2.0) should return true",
      UnsafeMatchesSpec.testExample3
    ),
    example(
      "test Example-4 DecVer(1.999).unsafeMatches(1.0 - 2.0) should return true",
      UnsafeMatchesSpec.testExample4
    ),
    example(
      "test Example-5 DecVer(1.0).unsafeMatches(>1.0 <2.0) should return false",
      UnsafeMatchesSpec.testExample5
    ),
    example(
      "test Example-6 DecVer(2.0).unsafeMatches(>1.0 <2.0) should return false",
      UnsafeMatchesSpec.testExample6
    ),
    example(
      "test Example-7 DecVer(1.1).unsafeMatches(>1.0 <2.0) should return true",
      UnsafeMatchesSpec.testExample7
    ),
    example(
      "test Example-8 DecVer(1.999).unsafeMatches(>1.0 <2.0) should return true",
      UnsafeMatchesSpec.testExample8
    ),
    example(
      "test Example-9 DecVer(1.0).unsafeMatches(>=1.0 <=2.0) should return true",
      UnsafeMatchesSpec.testExample5
    ),
    example(
      "test Example-10 DecVer(2.0).unsafeMatches(>=1.0 <=2.0) should return true",
      UnsafeMatchesSpec.testExample6
    ),
    example(
      "test Example-11 DecVer(1.1).unsafeMatches(>=1.0 <=2.0) should return true",
      UnsafeMatchesSpec.testExample7
    ),
    example(
      "test Example-12 DecVer(1.999).unsafeMatches(>=1.0 <=2.0) should return true",
      UnsafeMatchesSpec.testExample8
    ),
    property(
      "test DecVer(Valid).unsafeMatches(Range || Comparison)",
      UnsafeMatchesSpec.testDecVerValidMatchesDecVerMatchersRangeOrComparison
    ),
    property(
      "test DecVer(Valid).unsafeMatches(Comparison and Comparison)",
      UnsafeMatchesSpec.testDecVerValidMatchesDecVerMatchersComparisonAndComparison
    ),
    property(
      "test DecVer.unsafeMatches(Range || Comparison and Comparison)",
      UnsafeMatchesSpec.testDecVerValidMatchesDecVerMatchersRangeOrComparisonAndComparison
    ),
    property(
      "DecVer(major, minor).toSemVer should return SemVer(major, minor, 0)",
      testDecVerToSemVer
    ),
    property(
      "DecVer.fromDecVer(DecVer(major, minor)) should return DecVer(major, minor)",
      testDecVerFromSemVer
    )
  )

  def parseExample1: Result = {
    val input    = "1.0"
    val expected = Right(DecVer(Major(1), Minor(0), None, None))
    val actual   = DecVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre1: Result = {
    val input    = "1.0-beta"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        Option(
          AdditionalInfo.PreRelease(List(Dsv(List(Anh.alphabet("beta")))))
        ),
        None
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre2: Result = {
    val input    = "1.0-a.3.7.xyz"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        Option(
          AdditionalInfo.PreRelease(
            List(
              Dsv(
                List[Anh](Anh.alphabet("a"))
              ),
              Dsv(
                List[Anh](Anh.num(3))
              ),
              Dsv(
                List[Anh](Anh.num(7))
              ),
              Dsv(
                List[Anh](Anh.alphabet("xyz"))
              )
            )
          )
        ),
        None
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre3: Result = {
    val input    = "1.0-a-b.xyz"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        Option(
          AdditionalInfo.PreRelease(
            List(
              Dsv(
                List[Anh](Anh.alphabet("a"), Anh.hyphen, Anh.alphabet("b"))
              ),
              Dsv(
                List[Anh](Anh.alphabet("xyz"))
              )
            )
          )
        ),
        None
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre4: Result = {
    val input    = "1.0-0"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        Option(
          AdditionalInfo.PreRelease(
            List(
              Dsv(
                List[Anh](Anh.num(0))
              )
            )
          )
        ),
        None
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExamplePre5: Result = {
    val input    = "1.0-000a"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        Option(
          AdditionalInfo.PreRelease(
            List(
              Dsv(
                List[Anh](Anh.numFromStringUnsafe("000"), Anh.alphabet("a"))
              )
            )
          )
        ),
        None
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExamplePreInvalid1: Result = {
    val input    = "1.0-001"
    val expected = Left(
      DecVer
        .ParseError
        .preReleaseParseError(
          DecVer.ParseError.leadingZeroNumError("001")
        )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta1: Result = {
    val input    = "1.0+1234"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        None,
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(Dsv(List(Anh.num(1234))))
          )
        )
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta2: Result = {
    val input    = "1.0+a.3.7.xyz"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        None,
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](Anh.alphabet("a"))
              ),
              Dsv(
                List[Anh](Anh.num(3))
              ),
              Dsv(
                List[Anh](Anh.num(7))
              ),
              Dsv(
                List[Anh](Anh.alphabet("xyz"))
              )
            )
          )
        )
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta3: Result = {
    val input    = "1.0+a-b.xyz"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        None,
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](Anh.alphabet("a"), Anh.hyphen, Anh.alphabet("b"))
              ),
              Dsv(
                List[Anh](Anh.alphabet("xyz"))
              )
            )
          )
        )
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta4: Result = {
    val input    = "1.0+0"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        None,
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](Anh.num(0))
              )
            )
          )
        )
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta5: Result = {
    val input    = "1.0+000a"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        None,
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](Anh.numFromStringUnsafe("000"), Anh.alphabet("a"))
              )
            )
          )
        )
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExampleMeta6: Result = {
    val input    = "1.0+001"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        None,
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](Anh.numFromStringUnsafe("001"))
              )
            )
          )
        )
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta1: Result = {
    val input    = "1.0-beta+1234"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        Option(
          AdditionalInfo.PreRelease(
            List(Dsv(List(Anh.alphabet("beta"))))
          )
        ),
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(Dsv(List(Anh.num(1234))))
          )
        )
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta2: Result = {
    val input    = "1.0-a.3.7.xyz+a.3.7.xyz"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        Option(
          AdditionalInfo.PreRelease(
            List(
              Dsv(
                List[Anh](Anh.alphabet("a"))
              ),
              Dsv(
                List[Anh](Anh.num(3))
              ),
              Dsv(
                List[Anh](Anh.num(7))
              ),
              Dsv(
                List[Anh](Anh.alphabet("xyz"))
              )
            )
          )
        ),
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](Anh.alphabet("a"))
              ),
              Dsv(
                List[Anh](Anh.num(3))
              ),
              Dsv(
                List[Anh](Anh.num(7))
              ),
              Dsv(
                List[Anh](Anh.alphabet("xyz"))
              )
            )
          )
        )
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta3: Result = {
    val input    = "1.0-a-b.xyz+a-b.xyz"
    val expected = Right(
      DecVer(
        Major(1),
        Minor(0),
        Option(
          AdditionalInfo.PreRelease(
            List(
              Dsv(
                List[Anh](Anh.alphabet("a"), Anh.hyphen, Anh.alphabet("b"))
              ),
              Dsv(
                List[Anh](Anh.alphabet("xyz"))
              )
            )
          )
        ),
        Option(
          AdditionalInfo.BuildMetaInfo(
            List(
              Dsv(
                List[Anh](Anh.alphabet("a"), Anh.hyphen, Anh.alphabet("b"))
              ),
              Dsv(
                List[Anh](Anh.alphabet("xyz"))
              )
            )
          )
        )
      )
    )

    val actual = DecVer.parse(input)
    actual ==== expected
  }

  def testDecVerDecVer: Property = for {
    major <- DecVerGens.genMajor.log("major")
    minor <- DecVerGens.genMinor.log("major")
  } yield {
    DecVer.withMajorMinor(major, minor).asRight[DecVer.ParseError] ====
      DecVer.parse(s"${major.value.toString}.${minor.value.toString}")
  }

  def testDecVerIncreaseMajor: Property = for {
    v <- DecVerGens.genDecVer.log("v")
  } yield {
    val expected = v.copy(major = Major(v.major.value + 1))
    val actual   = DecVer.increaseMajor(v)
    actual ==== expected
  }

  def testDecVerIncreaseMinor: Property = for {
    v <- DecVerGens.genDecVer.log("v")
  } yield {
    val expected = v.copy(minor = Minor(v.minor.value + 1))
    val actual   = DecVer.increaseMinor(v)
    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDecVerEqual: Property = for {
    v <- DecVerGens.genDecVer.log("v")
  } yield {
    Result.diffNamed("Failed: v == v is not true", v, v)(_ == _)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDecVerEqualDiffCase: Property = for {
    v1AndV2 <- DecVerGens.genMinMaxDecVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("Failed: v1(diff) == v2(dff) is not false", v1, v2)((x, y) => !(x == y))
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDecVerNotEqualSameCase: Property = for {
    v <- DecVerGens.genDecVer.log("v")
  } yield {
    Result.diffNamed("Failed: v != v is not false", v, v)((x, y) => !(x != y))
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDecVerNotEqualDiffCase: Property = for {
    v1AndV2 <- DecVerGens.genMinMaxDecVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("Failed: v1(diff) != v2(dff) is not true", v1, v2)(_ != _)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDecVerCompareEqualCase: Property = for {
    v <- DecVerGens.genDecVer.log("v")
  } yield {
    Result.diffNamed("Failed: v == v is not true", v, v)(_.compare(_) == 0)
  }

  def testDecVerCompareLess: Property = for {
    v1AndV2 <- DecVerGens.genMinMaxDecVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.assert(v1.compare(v2) < 0)
  }

  def testDecVerCompareGreater: Property = for {
    v1AndV2 <- DecVerGens.genMinMaxDecVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.assert(v2.compare(v1) > 0)
  }

  def testDecVerLessTrue: Property = for {
    v1AndV2 <- DecVerGens.genMinMaxDecVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v1(less) < v2(greater) is not true ===", v1, v2)(_ < _)
  }

  def testDecVerLessFalseForSame: Property = for {
    v <- DecVerGens.genDecVer.log("v")
  } yield {
    Result.diffNamed("=== Failed: v(same) < v(same) is not false ===", v, v)((x, y) => !(x < y))
  }

  def testDecVerLessFalse: Property = for {
    v1AndV2 <- DecVerGens.genMinMaxDecVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v2(greater) < v1(less) is not false ===", v2, v1)((x, y) => !(x < y))
  }

  def testDecVerLessOrEqualTrue: Property = for {
    v1AndV2 <- DecVerGens.genMinMaxDecVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v1(less) <= v2(greater) is not true ===", v1, v2)(_ <= _)
  }

  def testDecVerLessOrEqualTrueForSame: Property = for {
    v <- DecVerGens.genDecVer.log("v")
  } yield {
    Result.diffNamed("=== Failed: v(same) <= v(same) is not true ===", v, v)(_ <= _)
  }

  def testDecVerLessOrEqualFalse: Property = for {
    v1AndV2 <- DecVerGens.genMinMaxDecVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v2(greater) <= v1(less) is not false ===", v2, v1)((x, y) => !(x <= y))
  }

  def testDecVerGreaterTrue: Property = for {
    v1AndV2 <- DecVerGens.genMinMaxDecVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v2(greater) > v1(less) is not true ===", v2, v1)(_ > _)
  }

  def testDecVerGreaterFalseForSame: Property = for {
    v <- DecVerGens.genDecVer.log("v")
  } yield {
    Result.diffNamed("=== Failed: v(same) > v(same) is not false ===", v, v)((x, y) => !(x > y))
  }

  def testDecVerGreaterFalse: Property = for {
    v1AndV2 <- DecVerGens.genMinMaxDecVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v1(less) > v2(greater) is not false ===", v1, v2)((x, y) => !(x > y))
  }

  def testDecVerGreaterOrEqualTrue: Property = for {
    v1AndV2 <- DecVerGens.genMinMaxDecVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v2(greater) >= v1(less) is not true ===", v2, v1)(_ >= _)
  }

  def testDecVerGreaterOrEqualTrueForSame: Property = for {
    v <- DecVerGens.genDecVer.log("v")
  } yield {
    Result.diffNamed("=== Failed: v(same) >= v(same) is not true ===", v, v)(_ >= _)
  }

  def testDecVerGreaterOrEqualFalse: Property = for {
    v1AndV2 <- DecVerGens.genMinMaxDecVers.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v1(less) >= v2(greater) is not false ===", v1, v2)((x, y) => !(x >= y))
  }

  def roundTripDecVerRenderDecVer: Property = for {
    decVer <- DecVerGens.genDecVer.log("decVer")
  } yield {
    val rendered = DecVer.render(decVer)
    val actual   = DecVer.parse(rendered)
    actual ==== Right(decVer)
  }

  def roundTripDecVerRender: Property = for {
    decVer <- DecVerGens.genDecVer.log("decVer")
  } yield {
    val rendered = decVer.render
    val actual   = DecVer.parse(rendered)
    actual ==== Right(decVer)
  }

  def testIsomorphismParseRender: Property = for {
    decVer <- DecVerGens.genDecVer.log("decVer")
  } yield {
    val input = decVer.render
    DecVer.parse(input) match {
      case Right(actual) =>
        val expected = decVer
        actual ==== expected

      case Left(err) =>
        Result.failure.log(s"DecVer.parse failed with error: ${err.render}")
    }
  }

  def testIsomorphismParseUnsafeRender: Property = for {
    decVer <- DecVerGens.genDecVer.log("decVer")
  } yield {
    val input    = decVer.render
    val actual   = DecVer.unsafeParse(input)
    val expected = decVer
    actual ==== expected
  }

  object MatchesSpec {

    def testExample1: Result = {
      Result
        .assert(DecVer.unsafeParse("1.0").matches(DecVerMatchers.unsafeParse("1.0 - 2.0")))
        .log("DecVerMatchers(1.0 - 2.0).matches(1.0) failed")
    }

    def testExample2: Result = {
      Result
        .assert(DecVer.unsafeParse("2.0").matches(DecVerMatchers.unsafeParse("1.0 - 2.0")))
        .log("DecVerMatchers(1.0 - 2.0).matches(2.0) failed")
    }

    def testExample3: Result = {
      Result
        .assert(DecVer.unsafeParse("1.1").matches(DecVerMatchers.unsafeParse("1.0 - 2.0")))
        .log("DecVerMatchers(1.0 - 2.0).matches(1.1) failed")
    }

    def testExample4: Result = {
      Result
        .assert(DecVer.unsafeParse("1.999").matches(DecVerMatchers.unsafeParse("1.0 - 2.0")))
        .log("DecVerMatchers(1.0 - 2.0).matches(1.999) failed")
    }

    def testExample5: Result = {
      Result
        .diff(DecVer.unsafeParse("1.0").matches(DecVerMatchers.unsafeParse(">1.0 <2.0")), false)(_ === _)
        .log("DecVerMatchers(>1.0 <2.0).matches(1.0) failed")
    }

    def testExample6: Result = {
      Result
        .diff(DecVer.unsafeParse("2.0").matches(DecVerMatchers.unsafeParse(">1.0 <2.0")), false)(_ === _)
        .log("DecVerMatchers(>1.0 <2.0).matches(2.0) failed")
    }

    def testExample7: Result = {
      Result
        .assert(DecVer.unsafeParse("1.1").matches(DecVerMatchers.unsafeParse(">1.0 <2.0")))
        .log("DecVerMatchers(>1.0 <2.0).matches(1.1) failed")
    }

    def testExample8: Result = {
      Result
        .assert(DecVer.unsafeParse("1.999").matches(DecVerMatchers.unsafeParse(">1.0 <2.0")))
        .log("DecVerMatchers(>1.0 <2.0).matches(1.999) failed")
    }

    def testExample9: Result = {
      Result
        .assert(DecVer.unsafeParse("1.0").matches(DecVerMatchers.unsafeParse(">=1.0 <=2.0")))
        .log("DecVerMatchers(>=1.0 <=2.0).matches(1.0) failed")
    }

    def testExample10: Result = {
      Result
        .assert(DecVer.unsafeParse("2.0").matches(DecVerMatchers.unsafeParse(">=1.0 <=2.0")))
        .log("DecVerMatchers(>=1.0 <=2.0).matches(2.0) failed")
    }

    def testExample11: Result = {
      Result
        .assert(DecVer.unsafeParse("1.1").matches(DecVerMatchers.unsafeParse(">=1.0 <=2.0")))
        .log("DecVerMatchers(>=1.0 <=2.0).matches(1.1) failed")
    }

    def testExample12: Result = {
      Result
        .assert(DecVer.unsafeParse("1.999").matches(DecVerMatchers.unsafeParse(">=1.0 <=2.0")))
        .log("DecVerMatchers(>=1.0 <=2.0).matches(1.999) failed")
    }

    def testDecVerValidMatchesDecVerMatchersRangeOrComparison: Property = for {
      major  <- Gen.int(Range.linear(5, 10)).log("major")
      minor  <- Gen.int(Range.linear(1, 10)).log("minor")
      major2 <- Gen.int(Range.linear(5, 10)).map(_ + major).log("major2")
      minor2 <- Gen.int(Range.linear(1, 10)).map(_ + minor).log("minor2")
      op     <- MatcherGens.genComparisonOperator.log("op")
    } yield {
      val decVer = DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor))

      val v1       = DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor - 1))
      val v2       = DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor + 1))
      val matcher1 = DecVerMatcher.range(v1, v2)

      val decVer2  = DecVer.withMajorMinor(DecVer.Major(major2), DecVer.Minor(minor2))
      val matcher2 = DecVerMatcher.comparison(DecVerComparison(op, decVer2))

        // format: off
        val versions = op match {
          case ComparisonOperator.Lt =>
            List(
              DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2)  ),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2 - 1)),
              DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2 - 1))
            )
          case ComparisonOperator.Le =>
            List(
              DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2 - 1)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2 - 1)),
              decVer2.copy()
            )
          case ComparisonOperator.Eql =>
            List(
              decVer2.copy()
            )
          case ComparisonOperator.Ne =>
            List(
              DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2 - 1)),
              DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2 - 1)),
              DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2 + 1)),
              DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2 + 1))
            )
          case ComparisonOperator.Gt =>
            List(
              DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2 + 1)),
              DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2 + 1))
            )
          case ComparisonOperator.Ge =>
            List(
              DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2 + 1)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2 + 1)),
              decVer2.copy()
            )
        }
        // format: on

      val decVerMatchers = DecVerMatchers.unsafeParse(s"${matcher1.render} || ${matcher2.render}")

      println(
        s"""# Range || Comparison:
             |-   matchers: ${decVerMatchers.render}
             |-  decVer: ${decVer.render}
             |- decVers: ${versions.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
             |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(decVer.matches(decVerMatchers))
            .log(
              s""" Range || Comparison - range test failed
                   |>  matchers: ${decVerMatchers.render}
                   |> decVer: ${decVer.render}
                   |""".stripMargin
            ),
          Result
            .assert(versions.forall(v => v.matches(decVerMatchers)))
            .log(
              s""" Range || Comparison - comparison test failed
                   |>   matchers: ${decVerMatchers.render}
                   |> decVers: ${versions.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
                   |""".stripMargin
            )
        )
      )
    }

    def testDecVerValidMatchesDecVerMatchersComparisonAndComparison: Property = for {
      v1V2DecVer <- MatcherGens
                      .genRangedDecVerComparison(
                        Range.linear(11, 30),
                        Range.linear(11, 50),
                      )
                      .log("(v1, v2, decVer)")
      (v1, v2, decVer) = v1V2DecVer
    } yield {
      val decVerMatchers = DecVerMatchers.unsafeParse(s"${v1.render} ${v2.render}")

      println(
        s"""# Comparison and Comparison
             |- matchers: ${decVerMatchers.render}
             |-   decVer: ${decVer.render}
             |""".stripMargin
      )

      Result
        .assert(decVer.matches(decVerMatchers))
        .log(
          s""" Comparison and Comparison - failed
               |> matchers: ${decVerMatchers.render}
               |>   decVer: ${decVer.render}
               |""".stripMargin
        )
    }

    def testDecVerValidMatchesDecVerMatchersRangeOrComparisonAndComparison: Property = for {
      rangeMatcherDecVerInRange <- MatcherGens
                                     .genDecVerMatcherRangeAndDecVerInRange(
                                       majorRange = Range.linear(11, 30),
                                       minorRange = Range.linear(11, 50),
                                     )
                                     .log("(rangeMatcher, decVerInRange)")
      (rangeMatcher, decVerInRange) = rangeMatcherDecVerInRange
      v1V2DecVer <- MatcherGens
                      .genRangedDecVerComparison(
                        majorRange = Range.linear(31, 100),
                        minorRange = Range.linear(51, 100),
                      )
                      .log("(v1, v2, decVer)")
      (v1, v2, decVer) = v1V2DecVer
    } yield {
      val decVerMatchers = DecVerMatchers.unsafeParse(s"${rangeMatcher.render} || ${v1.render} ${v2.render}")

      println(
        s"""# Range || Comparison and Comparison
             |-      matchers: ${decVerMatchers.render}
             |- decVerInRange: ${decVerInRange.render}
             |-  decVerInComp: ${decVer.render}
             |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(decVerInRange.matches(decVerMatchers))
            .log(
              s""" Range || Comparison and Comparison - testing range failed
                   |>      matchers: ${decVerMatchers.render}
                   |> decVerInRange: ${decVerInRange.render}
                   |""".stripMargin
            ),
          Result
            .assert(decVer.matches(decVerMatchers))
            .log(
              s""" Range || Comparison and Comparison - testing (comparison and comparison) failed
                   |>     matchers: ${decVerMatchers.render}
                   |> decVerInComp: ${decVer.render}
                   |""".stripMargin
            )
        )
      )
    }

  }

  object UnsafeMatchesSpec {

    def testExample1: Result = {
      Result
        .assert(DecVer.unsafeParse("1.0").unsafeMatches("1.0 - 2.0"))
        .log("DecVerMatchers(1.0 - 2.0).matches(1.0) failed")
    }

    def testExample2: Result = {
      Result
        .assert(DecVer.unsafeParse("2.0").unsafeMatches("1.0 - 2.0"))
        .log("DecVerMatchers(1.0 - 2.0).matches(2.0) failed")
    }

    def testExample3: Result = {
      Result
        .assert(DecVer.unsafeParse("1.1").unsafeMatches("1.0 - 2.0"))
        .log("DecVerMatchers(1.0 - 2.0).matches(1.1) failed")
    }

    def testExample4: Result = {
      Result
        .assert(DecVer.unsafeParse("1.999").unsafeMatches("1.0 - 2.0"))
        .log("DecVerMatchers(1.0 - 2.0).matches(1.999) failed")
    }

    def testExample5: Result = {
      Result
        .diff(DecVer.unsafeParse("1.0").unsafeMatches(">1.0 <2.0"), false)(_ === _)
        .log("DecVerMatchers(>1.0 <2.0).matches(1.0) failed")
    }

    def testExample6: Result = {
      Result
        .diff(DecVer.unsafeParse("2.0").unsafeMatches(">1.0 <2.0"), false)(_ === _)
        .log("DecVerMatchers(>1.0 <2.0).matches(2.0) failed")
    }

    def testExample7: Result = {
      Result
        .assert(DecVer.unsafeParse("1.1").unsafeMatches(">1.0 <2.0"))
        .log("DecVerMatchers(>1.0 <2.0).matches(1.1) failed")
    }

    def testExample8: Result = {
      Result
        .assert(DecVer.unsafeParse("1.999").unsafeMatches(">1.0 <2.0"))
        .log("DecVerMatchers(>1.0 <2.0).matches(1.999) failed")
    }

    def testExample9: Result = {
      Result
        .assert(DecVer.unsafeParse("1.0").unsafeMatches(">=1.0 <=2.0"))
        .log("DecVerMatchers(>=1.0 <=2.0).matches(1.0) failed")
    }

    def testExample10: Result = {
      Result
        .assert(DecVer.unsafeParse("2.0").unsafeMatches(">=1.0 <=2.0"))
        .log("DecVerMatchers(>=1.0 <=2.0).matches(2.0) failed")
    }

    def testExample11: Result = {
      Result
        .assert(DecVer.unsafeParse("1.1").unsafeMatches(">=1.0 <=2.0"))
        .log("DecVerMatchers(>=1.0 <=2.0).matches(1.1) failed")
    }

    def testExample12: Result = {
      Result
        .assert(DecVer.unsafeParse("1.999").unsafeMatches(">=1.0 <=2.0"))
        .log("DecVerMatchers(>=1.0 <=2.0).matches(1.999) failed")
    }

    def testDecVerValidMatchesDecVerMatchersRangeOrComparison: Property = for {
      major  <- Gen.int(Range.linear(5, 10)).log("major")
      minor  <- Gen.int(Range.linear(1, 10)).log("minor")
      major2 <- Gen.int(Range.linear(5, 10)).map(_ + major).log("major2")
      minor2 <- Gen.int(Range.linear(1, 10)).map(_ + minor).log("minor2")
      op     <- MatcherGens.genComparisonOperator.log("op")
    } yield {
      val decVer = DecVer.withMajorMinor(DecVer.Major(major), DecVer.Minor(minor))

      val v1       = DecVer.withMajorMinor(DecVer.Major(major - 1), DecVer.Minor(minor - 1))
      val v2       = DecVer.withMajorMinor(DecVer.Major(major + 1), DecVer.Minor(minor + 1))
      val matcher1 = DecVerMatcher.range(v1, v2)

      val decVer2  = DecVer.withMajorMinor(DecVer.Major(major2), DecVer.Minor(minor2))
      val matcher2 = DecVerMatcher.comparison(DecVerComparison(op, decVer2))

        // format: off
        val versions = op match {
          case ComparisonOperator.Lt =>
            List(
              DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2 - 1)),
              DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2 - 1))
            )
          case ComparisonOperator.Le =>
            List(
              DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2 - 1)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2 - 1)),
              decVer2.copy()
            )
          case ComparisonOperator.Eql =>
            List(
              decVer2.copy()
            )
          case ComparisonOperator.Ne =>
            List(
              DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2 - 1)),
              DecVer.withMajorMinor(DecVer.Major(major2 - 1), DecVer.Minor(minor2 - 1)),
              DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2 + 1)),
              DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2 + 1))
            )
          case ComparisonOperator.Gt =>
            List(
              DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2 + 1)),
              DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2 + 1))
            )
          case ComparisonOperator.Ge =>
            List(
              DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2 + 1)),
              DecVer.withMajorMinor(DecVer.Major(major2),     DecVer.Minor(minor2)),
              DecVer.withMajorMinor(DecVer.Major(major2 + 1), DecVer.Minor(minor2 + 1)),
              decVer2.copy()
            )
        }
        // format: on

      val decVerMatchers = s"${matcher1.render} || ${matcher2.render}"

      println(
        s"""# Range || Comparison:
             |-   matchers: ${decVerMatchers}
             |-  decVer: ${decVer.render}
             |- decVers: ${versions.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
             |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(decVer.unsafeMatches(decVerMatchers))
            .log(
              s""" Range || Comparison - range test failed
                   |>  matchers: $decVerMatchers
                   |> decVer: ${decVer.render}
                   |""".stripMargin
            ),
          Result
            .assert(versions.forall(v => v.unsafeMatches(decVerMatchers)))
            .log(
              s""" Range || Comparison - comparison test failed
                   |>   matchers: $decVerMatchers
                   |> decVers: ${versions.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
                   |""".stripMargin
            )
        )
      )
    }

    def testDecVerValidMatchesDecVerMatchersComparisonAndComparison: Property = for {
      v1V2DecVer <- MatcherGens
                      .genRangedDecVerComparison(
                        Range.linear(11, 30),
                        Range.linear(11, 50)
                      )
                      .log("(v1, v2, decVer)")
      (v1, v2, decVer) = v1V2DecVer
    } yield {
      val decVerMatchers = s"${v1.render} ${v2.render}"

      println(
        s"""# Comparison and Comparison
             |-  matchers: $decVerMatchers
             |- decVer: ${decVer.render}
             |""".stripMargin
      )

      Result
        .assert(decVer.unsafeMatches(decVerMatchers))
        .log(
          s""" Comparison and Comparison - failed
               |>  matchers: ${decVerMatchers}
               |> decVer: ${decVer.render}
               |""".stripMargin
        )
    }

    def testDecVerValidMatchesDecVerMatchersRangeOrComparisonAndComparison: Property = for {
      rangeMatcherDecVerInRange <- MatcherGens
                                     .genDecVerMatcherRangeAndDecVerInRange(
                                       majorRange = Range.linear(11, 30),
                                       minorRange = Range.linear(11, 50),
                                     )
                                     .log("(rangeMatcher, decVerInRange)")
      (rangeMatcher, decVerInRange) = rangeMatcherDecVerInRange
      v1V2DecVer <- MatcherGens
                      .genRangedDecVerComparison(
                        majorRange = Range.linear(31, 100),
                        minorRange = Range.linear(51, 100),
                      )
                      .log("(v1, v2, decVer)")
      (v1, v2, decVer) = v1V2DecVer
    } yield {
      val decVerMatchers = s"${rangeMatcher.render} || ${v1.render} ${v2.render}"

      println(
        s"""# Range || Comparison and Comparison
             |-         matchers: $decVerMatchers
             |- decVerInRange: ${decVerInRange.render}
             |-  decVerInComp: ${decVer.render}
             |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(decVerInRange.unsafeMatches(decVerMatchers))
            .log(
              s""" Range || Comparison and Comparison - testing range failed
                   |>         matchers: $decVerMatchers
                   |> decVerInRange: ${decVerInRange.render}
                   |""".stripMargin
            ),
          Result
            .assert(decVer.unsafeMatches(decVerMatchers))
            .log(
              s""" Range || Comparison and Comparison - testing (comparison and comparison) failed
                   |>        matchers: $decVerMatchers
                   |> decVerInComp: ${decVer.render}
                   |""".stripMargin
            )
        )
      )
    }

  }

  def testDecVerToSemVer: Property = for {
    decVer <- DecVerGens.genDecVer.log("decVer")
  } yield {
    val expected = SemVer(
      SemVer.Major(decVer.major.value),
      SemVer.Minor(decVer.minor.value),
      SemVer.patch0,
      decVer.pre,
      decVer.buildMetadata
    )
    val actual   = decVer.toSemVer
    println(actual)
    actual ==== expected
  }

  def testDecVerFromSemVer: Property = for {
    semVer <- SemVerGens.genSemVer.log("semVer")
  } yield {
    val expected = DecVer(
      DecVer.Major(semVer.major.value),
      DecVer.Minor(semVer.minor.value),
      pre = semVer.pre,
      buildMetadata = semVer.buildMetadata
    )
    val actual   = DecVer.fromSemVer(semVer)
    actual ==== expected
  }

}
