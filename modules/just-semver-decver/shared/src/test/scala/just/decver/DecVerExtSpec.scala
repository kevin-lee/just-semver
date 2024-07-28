package just.decver

import hedgehog._
import hedgehog.runner._
import just.Common._
import just.decver.DecVerExt.{Major, Minor}
import just.decver.matcher.{DecVerExtComparison, DecVerExtMatcher, DecVerExtMatchers, Gens => MatcherGens}
import just.decver.{Gens => DecVerGens}
import just.semver.expr.ComparisonOperator
import just.semver.{AdditionalInfo, Anh, Dsv}

/** @author
  *   Kevin Lee
  * @since
  *   2018-11-04
  */
object DecVerExtSpec extends Properties {
  override def tests: List[Test] = List(
    example(
      """DecVerExt.parse("1.0") should return SementicVersion(Major(1), Minor(0), None, None)""",
      parseExample1
    ),
    example(
      """DecVerExt.parse("1.0-beta") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePre1
    ),
    example(
      """DecVerExt.parse("1.0-a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePre2
    ),
    example(
      """DecVerExt.parse("1.0-a-b.xyz") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePre3
    ),
    example(
      """DecVerExt.parse("1.0-0") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePre4
    ),
    example(
      """DecVerExt.parse("1.0-000a") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePre5
    ),
    example("""DecVerExt.parse("1.0-001") should return Left(Invalid)""", parseExamplePreInvalid1),
    example(
      """DecVerExt.parse("1.0+1234") should return SementicVersion(Major(1), Minor(0), None, Some(build meta info)""",
      parseExampleMeta1
    ),
    example(
      """DecVerExt.parse("1.0+a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExampleMeta2
    ),
    example(
      """DecVerExt.parse("1.0+a-b.xyz") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExampleMeta3
    ),
    example(
      """DecVerExt.parse("1.0+0") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExampleMeta4
    ),
    example(
      """DecVerExt.parse("1.0+000a") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExampleMeta5
    ),
    example(
      """DecVerExt.parse("1.0+001") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExampleMeta6
    ),
    example(
      """DecVerExt.parse("1.0-beta+1234") should return SementicVersion(Major(1), Minor(0), None, Some(build meta info)""",
      parseExamplePreMeta1
    ),
    example(
      """DecVerExt.parse("1.0-a.3.7.xyz+a.3.7.xyz") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePreMeta2
    ),
    example(
      """DecVerExt.parse("1.0-a-b.xyz+a-b.xyz") should return SementicVersion(Major(1), Minor(0), Some(with pre-release info), None)""",
      parseExamplePreMeta3
    ),
    property(
      s"""test DecVerExt.withMajorMinor(major, minor) should be equal to DecVerExt.parse(s"$${major.major}.$${minor.minor}")""",
      testDecVerExtDecVerExt
    ),
    property("DecVerExt.increaseMajor", testDecVerExtIncreaseMajor),
    property("DecVerExt.increaseMinor", testDecVerExtIncreaseMinor),
    property("DecVerExt(same) == DecVerExt(same) should be true", testDecVerExtEqual),
    property("DecVerExt(different) == DecVerExt(different) should be false", testDecVerExtEqualDiffCase),
    property("DecVerExt(same) != DecVerExt(same) should be false", testDecVerExtNotEqualSameCase),
    property("DecVerExt(different) != DecVerExt(different) should be true", testDecVerExtNotEqualDiffCase),
    property("DecVerExt(same).compare(DecVerExt(same)) should be 0", testDecVerExtCompareEqualCase),
    property("DecVerExt(less).compare(DecVerExt(greater)) should the value less than 0", testDecVerExtCompareLess),
    property("DecVerExt(greater).compare(DecVerExt(less)) should the value more than 0", testDecVerExtCompareGreater),
    property("DecVerExt(less) < DecVerExt(greater) should be true", testDecVerExtLessTrue),
    property("DecVerExt(same) < DecVerExt(same) should be false", testDecVerExtLessFalseForSame),
    property("DecVerExt(greater) < DecVerExt(less) should be false", testDecVerExtLessFalse),
    property("DecVerExt(less) <= DecVerExt(greater) should be true", testDecVerExtLessOrEqualTrue),
    property("DecVerExt(same) <= DecVerExt(same) should be true", testDecVerExtLessOrEqualTrueForSame),
    property("DecVerExt(greater) <= DecVerExt(less) should be false", testDecVerExtLessOrEqualFalse),
    property("DecVerExt(greater) > DecVerExt(less) should be true", testDecVerExtGreaterTrue),
    property("DecVerExt(same) > DecVerExt(same) should be false", testDecVerExtGreaterFalseForSame),
    property("DecVerExt(less) > DecVerExt(greater) should be false", testDecVerExtGreaterFalse),
    property("DecVerExt(greater) >= DecVerExt(less) should be true", testDecVerExtGreaterOrEqualTrue),
    property("DecVerExt(same) >= DecVerExt(same) should be true", testDecVerExtGreaterOrEqualTrueForSame),
    property("DecVerExt(less) >= DecVerExt(greater) should be false", testDecVerExtGreaterOrEqualFalse),
    property("DecVerExt round trip DecVerExt.render(decVerExt)", roundTripDecVerExtRenderDecVerExt),
    property("DecVerExt round trip decVerExt.render", roundTripDecVerExtRender),
    property("test Isomorphism: parse <=> render", testIsomorphismParseRender),
    property("test Isomorphism: parseUnsafe <=> render", testIsomorphismParseUnsafeRender)
  ) ++ List(
    example(
      "test Example-1 DecVerExt(1.0).matches(DecVerExtMatchers(1.0 - 2.0)) should return true",
      MatchesSpec.testExample1
    ),
    example(
      "test Example-2 DecVerExt(2.0).matches(DecVerExtMatchers(1.0 - 2.0)) should return true",
      MatchesSpec.testExample2
    ),
    example(
      "test Example-3 DecVerExt(1.1).matches(DecVerExtMatchers(1.0 - 2.0)) should return true",
      MatchesSpec.testExample3
    ),
    example(
      "test Example-4 DecVerExt(1.999).matches(DecVerExtMatchers(1.0 - 2.0)) should return true",
      MatchesSpec.testExample4
    ),
    example(
      "test Example-5 DecVerExt(1.0).matches(DecVerExtMatchers(>1.0 <2.0)) should return false",
      MatchesSpec.testExample5
    ),
    example(
      "test Example-6 DecVerExt(2.0).matches(DecVerExtMatchers(>1.0 <2.0)) should return false",
      MatchesSpec.testExample6
    ),
    example(
      "test Example-7 DecVerExt(1.1).matches(DecVerExtMatchers(>1.0 <2.0)) should return true",
      MatchesSpec.testExample7
    ),
    example(
      "test Example-8 DecVerExt(1.999).matches(DecVerExtMatchers(>1.0 <2.0)) should return true",
      MatchesSpec.testExample8
    ),
    example(
      "test Example-9 DecVerExt(1.0).matches(DecVerExtMatchers(>=1.0 <=2.0)) should return true",
      MatchesSpec.testExample5
    ),
    example(
      "test Example-10 DecVerExt(2.0).matches(DecVerExtMatchers(>=1.0 <=2.0)) should return true",
      MatchesSpec.testExample6
    ),
    example(
      "test Example-11 DecVerExt(1.1).matches(DecVerExtMatchers(>=1.0 <=2.0)) should return true",
      MatchesSpec.testExample7
    ),
    example(
      "test Example-12 DecVerExt(1.999).matches(DecVerExtMatchers(>=1.0 <=2.0)) should return true",
      MatchesSpec.testExample8
    ),
    property(
      "test DecVerExt(Valid).matches(DecVerExtMatchers(Range || Comparison))",
      MatchesSpec.testDecVerExtValidMatchesDecVerExtMatchersRangeOrComparison
    ),
    property(
      "test DecVerExt(Valid).matches(DecVerExtMatchers(Comparison and Comparison))",
      MatchesSpec.testDecVerExtValidMatchesDecVerExtMatchersComparisonAndComparison
    ),
    property(
      "test DecVerExt.matches(DecVerExtMatchers(Range || Comparison and Comparison))",
      MatchesSpec.testDecVerExtValidMatchesDecVerExtMatchersRangeOrComparisonAndComparison
    )
  ) ++ List(
    example(
      "test Example-1 DecVerExt(1.0).unsafeMatches(1.0 - 2.0) should return true",
      UnsafeMatchesSpec.testExample1
    ),
    example(
      "test Example-2 DecVerExt(2.0).unsafeMatches(1.0 - 2.0) should return true",
      UnsafeMatchesSpec.testExample2
    ),
    example(
      "test Example-3 DecVerExt(1.1).unsafeMatches(1.0 - 2.0) should return true",
      UnsafeMatchesSpec.testExample3
    ),
    example(
      "test Example-4 DecVerExt(1.999).unsafeMatches(1.0 - 2.0) should return true",
      UnsafeMatchesSpec.testExample4
    ),
    example(
      "test Example-5 DecVerExt(1.0).unsafeMatches(>1.0 <2.0) should return false",
      UnsafeMatchesSpec.testExample5
    ),
    example(
      "test Example-6 DecVerExt(2.0).unsafeMatches(>1.0 <2.0) should return false",
      UnsafeMatchesSpec.testExample6
    ),
    example(
      "test Example-7 DecVerExt(1.1).unsafeMatches(>1.0 <2.0) should return true",
      UnsafeMatchesSpec.testExample7
    ),
    example(
      "test Example-8 DecVerExt(1.999).unsafeMatches(>1.0 <2.0) should return true",
      UnsafeMatchesSpec.testExample8
    ),
    example(
      "test Example-9 DecVerExt(1.0).unsafeMatches(>=1.0 <=2.0) should return true",
      UnsafeMatchesSpec.testExample5
    ),
    example(
      "test Example-10 DecVerExt(2.0).unsafeMatches(>=1.0 <=2.0) should return true",
      UnsafeMatchesSpec.testExample6
    ),
    example(
      "test Example-11 DecVerExt(1.1).unsafeMatches(>=1.0 <=2.0) should return true",
      UnsafeMatchesSpec.testExample7
    ),
    example(
      "test Example-12 DecVerExt(1.999).unsafeMatches(>=1.0 <=2.0) should return true",
      UnsafeMatchesSpec.testExample8
    ),
    property(
      "test DecVerExt(Valid).unsafeMatches(Range || Comparison)",
      UnsafeMatchesSpec.testDecVerExtValidMatchesDecVerExtMatchersRangeOrComparison
    ),
    property(
      "test DecVerExt(Valid).unsafeMatches(Comparison and Comparison)",
      UnsafeMatchesSpec.testDecVerExtValidMatchesDecVerExtMatchersComparisonAndComparison
    ),
    property(
      "test DecVerExt.unsafeMatches(Range || Comparison and Comparison)",
      UnsafeMatchesSpec.testDecVerExtValidMatchesDecVerExtMatchersRangeOrComparisonAndComparison
    ),
    property(
      "DecVerExt(major, minor, patch).toDecVer should return DecVer(major, minor)",
      testDecVerExtToDecVer
    ),
    property(
      "DecVerExt.fromDecVer(DecVer(major, minor)) should return DecVerExt(major, minor)",
      testDecVerExtFromDecVer
    )
  )

  def parseExample1: Result = {
    val input    = "1.0"
    val expected = Right(DecVerExt(Major(1), Minor(0), None, None))
    val actual   = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExamplePre1: Result = {
    val input    = "1.0-beta"
    val expected = Right(
      DecVerExt(
        Major(1),
        Minor(0),
        Option(
          AdditionalInfo.PreRelease(List(Dsv(List(Anh.alphabet("beta")))))
        ),
        None
      )
    )

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExamplePre2: Result = {
    val input    = "1.0-a.3.7.xyz"
    val expected = Right(
      DecVerExt(
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

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExamplePre3: Result = {
    val input    = "1.0-a-b.xyz"
    val expected = Right(
      DecVerExt(
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

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExamplePre4: Result = {
    val input    = "1.0-0"
    val expected = Right(
      DecVerExt(
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

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExamplePre5: Result = {
    val input    = "1.0-000a"
    val expected = Right(
      DecVerExt(
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

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExamplePreInvalid1: Result = {
    val input    = "1.0-001"
    val expected = Left(
      DecVerExt
        .ParseError
        .preReleaseParseError(
          DecVerExt.ParseError.leadingZeroNumError("001")
        )
    )

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExampleMeta1: Result = {
    val input    = "1.0+1234"
    val expected = Right(
      DecVerExt(
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

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExampleMeta2: Result = {
    val input    = "1.0+a.3.7.xyz"
    val expected = Right(
      DecVerExt(
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

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExampleMeta3: Result = {
    val input    = "1.0+a-b.xyz"
    val expected = Right(
      DecVerExt(
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

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExampleMeta4: Result = {
    val input    = "1.0+0"
    val expected = Right(
      DecVerExt(
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

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExampleMeta5: Result = {
    val input    = "1.0+000a"
    val expected = Right(
      DecVerExt(
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

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExampleMeta6: Result = {
    val input    = "1.0+001"
    val expected = Right(
      DecVerExt(
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

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta1: Result = {
    val input    = "1.0-beta+1234"
    val expected = Right(
      DecVerExt(
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

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta2: Result = {
    val input    = "1.0-a.3.7.xyz+a.3.7.xyz"
    val expected = Right(
      DecVerExt(
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

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def parseExamplePreMeta3: Result = {
    val input    = "1.0-a-b.xyz+a-b.xyz"
    val expected = Right(
      DecVerExt(
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

    val actual = DecVerExt.parse(input)
    actual ==== expected
  }

  def testDecVerExtDecVerExt: Property = for {
    major <- DecVerExtGens.genMajor.log("major")
    minor <- DecVerExtGens.genMinor.log("major")
  } yield {
    DecVerExt.withMajorMinor(major, minor).asRight[DecVerExt.ParseError] ====
      DecVerExt.parse(s"${major.value.toString}.${minor.value.toString}")
  }

  def testDecVerExtIncreaseMajor: Property = for {
    v <- DecVerExtGens.genDecVerExt.log("v")
  } yield {
    val expected = v.copy(major = Major(v.major.value + 1))
    val actual   = DecVerExt.increaseMajor(v)
    actual ==== expected
  }

  def testDecVerExtIncreaseMinor: Property = for {
    v <- DecVerExtGens.genDecVerExt.log("v")
  } yield {
    val expected = v.copy(minor = Minor(v.minor.value + 1))
    val actual   = DecVerExt.increaseMinor(v)
    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDecVerExtEqual: Property = for {
    v <- DecVerExtGens.genDecVerExt.log("v")
  } yield {
    Result.diffNamed("Failed: v == v is not true", v, v)(_ == _)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDecVerExtEqualDiffCase: Property = for {
    v1AndV2 <- DecVerExtGens.genMinMaxDecVerExts.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("Failed: v1(diff) == v2(dff) is not false", v1, v2)((x, y) => !(x == y))
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDecVerExtNotEqualSameCase: Property = for {
    v <- DecVerExtGens.genDecVerExt.log("v")
  } yield {
    Result.diffNamed("Failed: v != v is not false", v, v)((x, y) => !(x != y))
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDecVerExtNotEqualDiffCase: Property = for {
    v1AndV2 <- DecVerExtGens.genMinMaxDecVerExts.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("Failed: v1(diff) != v2(dff) is not true", v1, v2)(_ != _)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDecVerExtCompareEqualCase: Property = for {
    v <- DecVerExtGens.genDecVerExt.log("v")
  } yield {
    Result.diffNamed("Failed: v == v is not true", v, v)(_.compare(_) == 0)
  }

  def testDecVerExtCompareLess: Property = for {
    v1AndV2 <- DecVerExtGens.genMinMaxDecVerExts.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.assert(v1.compare(v2) < 0)
  }

  def testDecVerExtCompareGreater: Property = for {
    v1AndV2 <- DecVerExtGens.genMinMaxDecVerExts.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.assert(v2.compare(v1) > 0)
  }

  def testDecVerExtLessTrue: Property = for {
    v1AndV2 <- DecVerExtGens.genMinMaxDecVerExts.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v1(less) < v2(greater) is not true ===", v1, v2)(_ < _)
  }

  def testDecVerExtLessFalseForSame: Property = for {
    v <- DecVerExtGens.genDecVerExt.log("v")
  } yield {
    Result.diffNamed("=== Failed: v(same) < v(same) is not false ===", v, v)((x, y) => !(x < y))
  }

  def testDecVerExtLessFalse: Property = for {
    v1AndV2 <- DecVerExtGens.genMinMaxDecVerExts.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v2(greater) < v1(less) is not false ===", v2, v1)((x, y) => !(x < y))
  }

  def testDecVerExtLessOrEqualTrue: Property = for {
    v1AndV2 <- DecVerExtGens.genMinMaxDecVerExts.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v1(less) <= v2(greater) is not true ===", v1, v2)(_ <= _)
  }

  def testDecVerExtLessOrEqualTrueForSame: Property = for {
    v <- DecVerExtGens.genDecVerExt.log("v")
  } yield {
    Result.diffNamed("=== Failed: v(same) <= v(same) is not true ===", v, v)(_ <= _)
  }

  def testDecVerExtLessOrEqualFalse: Property = for {
    v1AndV2 <- DecVerExtGens.genMinMaxDecVerExts.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v2(greater) <= v1(less) is not false ===", v2, v1)((x, y) => !(x <= y))
  }

  def testDecVerExtGreaterTrue: Property = for {
    v1AndV2 <- DecVerExtGens.genMinMaxDecVerExts.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v2(greater) > v1(less) is not true ===", v2, v1)(_ > _)
  }

  def testDecVerExtGreaterFalseForSame: Property = for {
    v <- DecVerExtGens.genDecVerExt.log("v")
  } yield {
    Result.diffNamed("=== Failed: v(same) > v(same) is not false ===", v, v)((x, y) => !(x > y))
  }

  def testDecVerExtGreaterFalse: Property = for {
    v1AndV2 <- DecVerExtGens.genMinMaxDecVerExts.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v1(less) > v2(greater) is not false ===", v1, v2)((x, y) => !(x > y))
  }

  def testDecVerExtGreaterOrEqualTrue: Property = for {
    v1AndV2 <- DecVerExtGens.genMinMaxDecVerExts.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v2(greater) >= v1(less) is not true ===", v2, v1)(_ >= _)
  }

  def testDecVerExtGreaterOrEqualTrueForSame: Property = for {
    v <- DecVerExtGens.genDecVerExt.log("v")
  } yield {
    Result.diffNamed("=== Failed: v(same) >= v(same) is not true ===", v, v)(_ >= _)
  }

  def testDecVerExtGreaterOrEqualFalse: Property = for {
    v1AndV2 <- DecVerExtGens.genMinMaxDecVerExts.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    Result.diffNamed("=== Failed: v1(less) >= v2(greater) is not false ===", v1, v2)((x, y) => !(x >= y))
  }

  def roundTripDecVerExtRenderDecVerExt: Property = for {
    decVerExt <- DecVerExtGens.genDecVerExt.log("decVerExt")
  } yield {
    val rendered = DecVerExt.render(decVerExt)
    val actual   = DecVerExt.parse(rendered)
    actual ==== Right(decVerExt)
  }

  def roundTripDecVerExtRender: Property = for {
    decVerExt <- DecVerExtGens.genDecVerExt.log("decVerExt")
  } yield {
    val rendered = decVerExt.render
    val actual   = DecVerExt.parse(rendered)
    actual ==== Right(decVerExt)
  }

  def testIsomorphismParseRender: Property = for {
    decVerExt <- DecVerExtGens.genDecVerExt.log("decVerExt")
  } yield {
    val input = decVerExt.render
    DecVerExt.parse(input) match {
      case Right(actual) =>
        val expected = decVerExt
        actual ==== expected

      case Left(err) =>
        Result.failure.log(s"DecVerExt.parse failed with error: ${err.render}")
    }
  }

  def testIsomorphismParseUnsafeRender: Property = for {
    decVerExt <- DecVerExtGens.genDecVerExt.log("decVerExt")
  } yield {
    val input    = decVerExt.render
    val actual   = DecVerExt.unsafeParse(input)
    val expected = decVerExt
    actual ==== expected
  }

  object MatchesSpec {

    def testExample1: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.0").matches(DecVerExtMatchers.unsafeParse("1.0 - 2.0")))
        .log("DecVerExtMatchers(1.0 - 2.0).matches(1.0) failed")
    }

    def testExample2: Result = {
      Result
        .assert(DecVerExt.unsafeParse("2.0").matches(DecVerExtMatchers.unsafeParse("1.0 - 2.0")))
        .log("DecVerExtMatchers(1.0 - 2.0).matches(2.0) failed")
    }

    def testExample3: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.1").matches(DecVerExtMatchers.unsafeParse("1.0 - 2.0")))
        .log("DecVerExtMatchers(1.0 - 2.0).matches(1.1) failed")
    }

    def testExample4: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.999").matches(DecVerExtMatchers.unsafeParse("1.0 - 2.0")))
        .log("DecVerExtMatchers(1.0 - 2.0).matches(1.999) failed")
    }

    def testExample5: Result = {
      Result
        .diff(DecVerExt.unsafeParse("1.0").matches(DecVerExtMatchers.unsafeParse(">1.0 <2.0")), false)(_ === _)
        .log("DecVerExtMatchers(>1.0 <2.0).matches(1.0) failed")
    }

    def testExample6: Result = {
      Result
        .diff(DecVerExt.unsafeParse("2.0").matches(DecVerExtMatchers.unsafeParse(">1.0 <2.0")), false)(_ === _)
        .log("DecVerExtMatchers(>1.0 <2.0).matches(2.0) failed")
    }

    def testExample7: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.1").matches(DecVerExtMatchers.unsafeParse(">1.0 <2.0")))
        .log("DecVerExtMatchers(>1.0 <2.0).matches(1.1) failed")
    }

    def testExample8: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.999").matches(DecVerExtMatchers.unsafeParse(">1.0 <2.0")))
        .log("DecVerExtMatchers(>1.0 <2.0).matches(1.999) failed")
    }

    def testExample9: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.0").matches(DecVerExtMatchers.unsafeParse(">=1.0 <=2.0")))
        .log("DecVerExtMatchers(>=1.0 <=2.0).matches(1.0) failed")
    }

    def testExample10: Result = {
      Result
        .assert(DecVerExt.unsafeParse("2.0").matches(DecVerExtMatchers.unsafeParse(">=1.0 <=2.0")))
        .log("DecVerExtMatchers(>=1.0 <=2.0).matches(2.0) failed")
    }

    def testExample11: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.1").matches(DecVerExtMatchers.unsafeParse(">=1.0 <=2.0")))
        .log("DecVerExtMatchers(>=1.0 <=2.0).matches(1.1) failed")
    }

    def testExample12: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.999").matches(DecVerExtMatchers.unsafeParse(">=1.0 <=2.0")))
        .log("DecVerExtMatchers(>=1.0 <=2.0).matches(1.999) failed")
    }

    def testDecVerExtValidMatchesDecVerExtMatchersRangeOrComparison: Property = for {
      major  <- Gen.int(Range.linear(5, 10)).log("major")
      minor  <- Gen.int(Range.linear(1, 10)).log("minor")
      major2 <- Gen.int(Range.linear(5, 10)).map(_ + major).log("major2")
      minor2 <- Gen.int(Range.linear(1, 10)).map(_ + minor).log("minor2")
      op     <- MatcherGens.genComparisonOperator.log("op")
    } yield {
      val decVerExt = DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor))

      val v1       = DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor - 1))
      val v2       = DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor + 1))
      val matcher1 = DecVerExtMatcher.range(v1, v2)

      val decVerExt2 = DecVerExt.withMajorMinor(DecVerExt.Major(major2), DecVerExt.Minor(minor2))
      val matcher2   = DecVerExtMatcher.comparison(DecVerExtComparison(op, decVerExt2))

        // format: off
        val versions = op match {
          case ComparisonOperator.Lt =>
            List(
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2)  ),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2 - 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2 - 1))
            )
          case ComparisonOperator.Le =>
            List(
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2 - 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2 - 1)),
              decVerExt2.copy()
            )
          case ComparisonOperator.Eql =>
            List(
              decVerExt2.copy()
            )
          case ComparisonOperator.Ne =>
            List(
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2 - 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2 - 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2 + 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2 + 1))
            )
          case ComparisonOperator.Gt =>
            List(
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2 + 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2 + 1))
            )
          case ComparisonOperator.Ge =>
            List(
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2 + 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2 + 1)),
              decVerExt2.copy()
            )
        }
        // format: on

      val decVerExtMatchers = DecVerExtMatchers.unsafeParse(s"${matcher1.render} || ${matcher2.render}")

      println(
        s"""# Range || Comparison:
             |-   matchers: ${decVerExtMatchers.render}
             |-  decVerExt: ${decVerExt.render}
             |- decVerExts: ${versions.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
             |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(decVerExt.matches(decVerExtMatchers))
            .log(
              s""" Range || Comparison - range test failed
                   |>  matchers: ${decVerExtMatchers.render}
                   |> decVerExt: ${decVerExt.render}
                   |""".stripMargin
            ),
          Result
            .assert(versions.forall(v => v.matches(decVerExtMatchers)))
            .log(
              s""" Range || Comparison - comparison test failed
                   |>   matchers: ${decVerExtMatchers.render}
                   |> decVerExts: ${versions.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
                   |""".stripMargin
            )
        )
      )
    }

    def testDecVerExtValidMatchesDecVerExtMatchersComparisonAndComparison: Property = for {
      v1V2DecVerExt <- MatcherGens
                         .genRangedDecVerExtComparison(
                           Range.linear(11, 30),
                           Range.linear(11, 50),
                         )
                         .log("(v1, v2, decVerExt)")
      (v1, v2, decVerExt) = v1V2DecVerExt
    } yield {
      val decVerExtMatchers = DecVerExtMatchers.unsafeParse(s"${v1.render} ${v2.render}")

      println(
        s"""# Comparison and Comparison
             |- matchers: ${decVerExtMatchers.render}
             |-   decVerExt: ${decVerExt.render}
             |""".stripMargin
      )

      Result
        .assert(decVerExt.matches(decVerExtMatchers))
        .log(
          s""" Comparison and Comparison - failed
               |> matchers: ${decVerExtMatchers.render}
               |>   decVerExt: ${decVerExt.render}
               |""".stripMargin
        )
    }

    def testDecVerExtValidMatchesDecVerExtMatchersRangeOrComparisonAndComparison: Property = for {
      rangeMatcherDecVerExtInRange <- MatcherGens
                                        .genDecVerExtMatcherRangeAndDecVerInRange(
                                          majorRange = Range.linear(11, 30),
                                          minorRange = Range.linear(11, 50),
                                        )
                                        .log("(rangeMatcher, decVerExtInRange)")
      (rangeMatcher, decVerExtInRange) = rangeMatcherDecVerExtInRange
      v1V2DecVerExt <- MatcherGens
                         .genRangedDecVerExtComparison(
                           majorRange = Range.linear(31, 100),
                           minorRange = Range.linear(51, 100),
                         )
                         .log("(v1, v2, decVerExt)")
      (v1, v2, decVerExt) = v1V2DecVerExt
    } yield {
      val decVerExtMatchers = DecVerExtMatchers.unsafeParse(s"${rangeMatcher.render} || ${v1.render} ${v2.render}")

      println(
        s"""# Range || Comparison and Comparison
             |-      matchers: ${decVerExtMatchers.render}
             |- decVerExtInRange: ${decVerExtInRange.render}
             |-  decVerExtInComp: ${decVerExt.render}
             |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(decVerExtInRange.matches(decVerExtMatchers))
            .log(
              s""" Range || Comparison and Comparison - testing range failed
                   |>      matchers: ${decVerExtMatchers.render}
                   |> decVerExtInRange: ${decVerExtInRange.render}
                   |""".stripMargin
            ),
          Result
            .assert(decVerExt.matches(decVerExtMatchers))
            .log(
              s""" Range || Comparison and Comparison - testing (comparison and comparison) failed
                   |>     matchers: ${decVerExtMatchers.render}
                   |> decVerExtInComp: ${decVerExt.render}
                   |""".stripMargin
            )
        )
      )
    }

  }

  object UnsafeMatchesSpec {

    def testExample1: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.0").unsafeMatches("1.0 - 2.0"))
        .log("DecVerExtMatchers(1.0 - 2.0).matches(1.0) failed")
    }

    def testExample2: Result = {
      Result
        .assert(DecVerExt.unsafeParse("2.0").unsafeMatches("1.0 - 2.0"))
        .log("DecVerExtMatchers(1.0 - 2.0).matches(2.0) failed")
    }

    def testExample3: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.1").unsafeMatches("1.0 - 2.0"))
        .log("DecVerExtMatchers(1.0 - 2.0).matches(1.1) failed")
    }

    def testExample4: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.999").unsafeMatches("1.0 - 2.0"))
        .log("DecVerExtMatchers(1.0 - 2.0).matches(1.999) failed")
    }

    def testExample5: Result = {
      Result
        .diff(DecVerExt.unsafeParse("1.0").unsafeMatches(">1.0 <2.0"), false)(_ === _)
        .log("DecVerExtMatchers(>1.0 <2.0).matches(1.0) failed")
    }

    def testExample6: Result = {
      Result
        .diff(DecVerExt.unsafeParse("2.0").unsafeMatches(">1.0 <2.0"), false)(_ === _)
        .log("DecVerExtMatchers(>1.0 <2.0).matches(2.0) failed")
    }

    def testExample7: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.1").unsafeMatches(">1.0 <2.0"))
        .log("DecVerExtMatchers(>1.0 <2.0).matches(1.1) failed")
    }

    def testExample8: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.999").unsafeMatches(">1.0 <2.0"))
        .log("DecVerExtMatchers(>1.0 <2.0).matches(1.999) failed")
    }

    def testExample9: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.0").unsafeMatches(">=1.0 <=2.0"))
        .log("DecVerExtMatchers(>=1.0 <=2.0).matches(1.0) failed")
    }

    def testExample10: Result = {
      Result
        .assert(DecVerExt.unsafeParse("2.0").unsafeMatches(">=1.0 <=2.0"))
        .log("DecVerExtMatchers(>=1.0 <=2.0).matches(2.0) failed")
    }

    def testExample11: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.1").unsafeMatches(">=1.0 <=2.0"))
        .log("DecVerExtMatchers(>=1.0 <=2.0).matches(1.1) failed")
    }

    def testExample12: Result = {
      Result
        .assert(DecVerExt.unsafeParse("1.999").unsafeMatches(">=1.0 <=2.0"))
        .log("DecVerExtMatchers(>=1.0 <=2.0).matches(1.999) failed")
    }

    def testDecVerExtValidMatchesDecVerExtMatchersRangeOrComparison: Property = for {
      major  <- Gen.int(Range.linear(5, 10)).log("major")
      minor  <- Gen.int(Range.linear(1, 10)).log("minor")
      major2 <- Gen.int(Range.linear(5, 10)).map(_ + major).log("major2")
      minor2 <- Gen.int(Range.linear(1, 10)).map(_ + minor).log("minor2")
      op     <- MatcherGens.genComparisonOperator.log("op")
    } yield {
      val decVerExt = DecVerExt.withMajorMinor(DecVerExt.Major(major), DecVerExt.Minor(minor))

      val v1       = DecVerExt.withMajorMinor(DecVerExt.Major(major - 1), DecVerExt.Minor(minor - 1))
      val v2       = DecVerExt.withMajorMinor(DecVerExt.Major(major + 1), DecVerExt.Minor(minor + 1))
      val matcher1 = DecVerExtMatcher.range(v1, v2)

      val decVerExt2 = DecVerExt.withMajorMinor(DecVerExt.Major(major2), DecVerExt.Minor(minor2))
      val matcher2   = DecVerExtMatcher.comparison(DecVerExtComparison(op, decVerExt2))

        // format: off
        val versions = op match {
          case ComparisonOperator.Lt =>
            List(
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2 - 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2 - 1))
            )
          case ComparisonOperator.Le =>
            List(
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2 - 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2 - 1)),
              decVerExt2.copy()
            )
          case ComparisonOperator.Eql =>
            List(
              decVerExt2.copy()
            )
          case ComparisonOperator.Ne =>
            List(
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2 - 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 - 1), DecVerExt.Minor(minor2 - 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2 + 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2 + 1))
            )
          case ComparisonOperator.Gt =>
            List(
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2 + 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2 + 1))
            )
          case ComparisonOperator.Ge =>
            List(
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2 + 1)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2),     DecVerExt.Minor(minor2)),
              DecVerExt.withMajorMinor(DecVerExt.Major(major2 + 1), DecVerExt.Minor(minor2 + 1)),
              decVerExt2.copy()
            )
        }
        // format: on

      val decVerExtMatchers = s"${matcher1.render} || ${matcher2.render}"

      println(
        s"""# Range || Comparison:
             |-   matchers: ${decVerExtMatchers}
             |-  decVerExt: ${decVerExt.render}
             |- decVerExts: ${versions.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
             |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(decVerExt.unsafeMatches(decVerExtMatchers))
            .log(
              s""" Range || Comparison - range test failed
                   |>  matchers: $decVerExtMatchers
                   |> decVerExt: ${decVerExt.render}
                   |""".stripMargin
            ),
          Result
            .assert(versions.forall(v => v.unsafeMatches(decVerExtMatchers)))
            .log(
              s""" Range || Comparison - comparison test failed
                   |>   matchers: $decVerExtMatchers
                   |> decVerExts: ${versions.map(_.render).mkString("[\n>   - ", "\n>   - ", "\n> ]")}
                   |""".stripMargin
            )
        )
      )
    }

    def testDecVerExtValidMatchesDecVerExtMatchersComparisonAndComparison: Property = for {
      v1V2DecVerExt <- MatcherGens
                         .genRangedDecVerExtComparison(
                           Range.linear(11, 30),
                           Range.linear(11, 50)
                         )
                         .log("(v1, v2, decVerExt)")
      (v1, v2, decVerExt) = v1V2DecVerExt
    } yield {
      val decVerExtMatchers = s"${v1.render} ${v2.render}"

      println(
        s"""# Comparison and Comparison
             |-  matchers: $decVerExtMatchers
             |- decVerExt: ${decVerExt.render}
             |""".stripMargin
      )

      Result
        .assert(decVerExt.unsafeMatches(decVerExtMatchers))
        .log(
          s""" Comparison and Comparison - failed
               |>  matchers: ${decVerExtMatchers}
               |> decVerExt: ${decVerExt.render}
               |""".stripMargin
        )
    }

    def testDecVerExtValidMatchesDecVerExtMatchersRangeOrComparisonAndComparison: Property = for {
      rangeMatcherDecVerExtInRange <- MatcherGens
                                        .genDecVerExtMatcherRangeAndDecVerInRange(
                                          majorRange = Range.linear(11, 30),
                                          minorRange = Range.linear(11, 50),
                                        )
                                        .log("(rangeMatcher, decVerExtInRange)")
      (rangeMatcher, decVerExtInRange) = rangeMatcherDecVerExtInRange
      v1V2DecVerExt <- MatcherGens
                         .genRangedDecVerExtComparison(
                           majorRange = Range.linear(31, 100),
                           minorRange = Range.linear(51, 100),
                         )
                         .log("(v1, v2, decVerExt)")
      (v1, v2, decVerExt) = v1V2DecVerExt
    } yield {
      val decVerExtMatchers = s"${rangeMatcher.render} || ${v1.render} ${v2.render}"

      println(
        s"""# Range || Comparison and Comparison
             |-         matchers: $decVerExtMatchers
             |- decVerExtInRange: ${decVerExtInRange.render}
             |-  decVerExtInComp: ${decVerExt.render}
             |""".stripMargin
      )

      Result.all(
        List(
          Result
            .assert(decVerExtInRange.unsafeMatches(decVerExtMatchers))
            .log(
              s""" Range || Comparison and Comparison - testing range failed
                   |>         matchers: $decVerExtMatchers
                   |> decVerExtInRange: ${decVerExtInRange.render}
                   |""".stripMargin
            ),
          Result
            .assert(decVerExt.unsafeMatches(decVerExtMatchers))
            .log(
              s""" Range || Comparison and Comparison - testing (comparison and comparison) failed
                   |>        matchers: $decVerExtMatchers
                   |> decVerExtInComp: ${decVerExt.render}
                   |""".stripMargin
            )
        )
      )
    }

  }

  def testDecVerExtToDecVer: Property = for {
    decVerExt <- DecVerExtGens.genDecVerExt.log("decVerExt")
  } yield {
    val expected = DecVer(DecVer.Major(decVerExt.major.value), DecVer.Minor(decVerExt.minor.value))
    val actual   = decVerExt.toDecVer
    actual ==== expected
  }

  def testDecVerExtFromDecVer: Property = for {
    decVer <- DecVerGens.genDecVer.log("decVer")
  } yield {
    val expected = DecVerExt(
      DecVerExt.Major(decVer.major.value),
      DecVerExt.Minor(decVer.minor.value),
      pre = none,
      buildMetadata = none
    )
    val actual   = DecVerExt.fromDecVer(decVer)
    actual ==== expected
  }

}
