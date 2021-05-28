package just.semver

import Anh.{alphabet, hyphen, num, numFromStringUnsafe}

import hedgehog._
import hedgehog.runner._

import just.Common._
import just.semver.SemVer.{Major, Minor, Patch}

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
      SemVer.parse(s"${major.major.toString}.${minor.minor.toString}.${patch.patch.toString}")
  }

  def testSemVerIncreaseMajor: Property = for {
    v <- Gens.genSemVer.log("v")
  } yield {
    val expected = v.copy(major = Major(v.major.major + 1))
    val actual   = SemVer.increaseMajor(v)
    actual ==== expected
  }

  def testSemVerIncreaseMinor: Property = for {
    v <- Gens.genSemVer.log("v")
  } yield {
    val expected = v.copy(minor = Minor(v.minor.minor + 1))
    val actual   = SemVer.increaseMinor(v)
    actual ==== expected
  }

  def testSemVerIncreasePatch: Property = for {
    v <- Gens.genSemVer.log("v")
  } yield {
    val expected = v.copy(patch = Patch(v.patch.patch + 1))
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

}
