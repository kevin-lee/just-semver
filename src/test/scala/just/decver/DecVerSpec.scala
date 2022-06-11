package just.decver

import hedgehog._
import hedgehog.runner._
import just.semver.{Compat, SemVer, Gens => SemVerGens}

import scala.util.Try

/** @author Kevin Lee
  * @since 2022-06-10
  */
object DecVerSpec extends Properties with Compat {
  override def tests: List[Test] = List(
    property("""DecVer(major, minor).render should return "major.minor"""", testDecVerRender),
    property("""DecVer(less).compare(DecVer(greater)) should return -1""", testDecVerCompareLess),
    property("""DecVer(same).compare(DecVer(same)) should return 0""", testDecVerCompareSame),
    property("""DecVer(greater).compare(DecVer(less)) should return 1""", testDecVerCompareGreater),
    property("DecVer.parse(valid) should return Right(DecVer)", testDecVerParseValid),
    example("DecVer.parse(null) should return Left(DecVer.ParseError.NullValue)", testDecVerParseNull),
    example("""DecVer.parse("") should return Left(DecVer.ParseError.Empty)""", testDecVerParseEmptyString),
    property("""DecVer.parse(invalid) should return Left(DecVer.ParseError.Invalid)""", testDecVerParseInvalid),
    property("DecVer.unsafeParse(valid) should return DecVer", testDecVerUnsafeParseValid),
    example(
      "DecVer.unsafeParse(null) should throw RuntimeException(rendered DecVer.ParseError.NullValue)",
      testDecVerUnsafeParseNull
    ),
    example(
      """DecVer.unsafeParse("") should throw RuntimeException(rendered DecVer.ParseError.Empty)""",
      testDecVerUnsafeParseEmptyString
    ),
    property(
      """DecVer.unsafeParse(invalid) should throw RuntimeException(rendered DecVer.ParseError.Invalid)""",
      testDecVerUnsafeParseInvalid
    ),
    property("""DecVer(major, minor).toSemVer should return SemVer(major, minor, 0)""", testDecVerToSemVer),
    property(
      """DecVer.fromSemVer(SemVer(major, minor, patch)) should return DecVer(major, minor)""",
      testDecVerFromSemVer
    ),
    property("""DecVer(major, minor).increaseMajor should return DecVer(major + 1, minor)""", testDecVerIncreaseMajor),
    property("""DecVer(major, minor).increaseMinor should return DecVer(major, minor + 1)""", testDecVerIncreaseMinor)
  )

  def testDecVerRender: Property = for {
    major <- SemVerGens.genNonNegativeInt.log("major")
    minor <- SemVerGens.genNonNegativeInt.log("minor")
  } yield {
    val decVer   = DecVer(DecVer.Major(major), DecVer.Minor(minor))
    val expected = s"${major.toString}.${minor.toString}"
    val actual   = decVer.render

    actual ==== expected
  }

  def testDecVerCompareLess: Property = for {
    v1AndV2 <- Gens.genLessAndGreaterDecVerPair.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    val expected = -1
    val actual   = v1.compare(v2)
    (actual ==== expected).log("v1.compare(v2) failed")
  }

  def testDecVerCompareSame: Property = for {
    decVer <- Gens.genDecVer.log("decVer")
  } yield {
    val expected = 0
    val actual   = decVer.compare(decVer)
    (actual ==== expected).log("decVer.compare(decVer) failed")
  }

  def testDecVerCompareGreater: Property = for {
    v1AndV2 <- Gens.genLessAndGreaterDecVerPair.log("(v1, v2)")
    (v1, v2) = v1AndV2
  } yield {
    val expected = 1
    val actual   = v2.compare(v1)
    (actual ==== expected).log("v2.compare(v1) failed")
  }

  def testDecVerParseValid: Property = for {
    decVer <- Gens.genDecVer.log("decVer")
  } yield {
    val version = decVer.render

    val expected = decVer
    val actual   = DecVer.parse(version)

    actual ==== Right(expected)
  }

  def testDecVerParseNull: Result = {
    @SuppressWarnings(Array("org.wartremover.warts.Null"))
    val version: String = null // scalafix:ok DisableSyntax.null
    val expected        = DecVer.ParseError.nullValue
    val actual          = DecVer.parse(version)

    actual ==== Left(expected)
  }

  def testDecVerParseEmptyString: Result = {
    val version: String = ""
    val expected        = DecVer.ParseError.empty
    val actual          = DecVer.parse(version)

    actual ==== Left(expected)
  }

  def testDecVerParseInvalid: Property = for {
    input <- Gen.string(Gen.unicode, Range.linear(1, 100)).log("input")
  } yield {

    val expected = DecVer.ParseError.invalid(input)
    val actual   = DecVer.parse(input)

    actual ==== Left(expected)
  }

  def testDecVerUnsafeParseValid: Property = for {
    decVer <- Gens.genDecVer.log("decVer")
  } yield {
    val version = decVer.render

    val expected = decVer
    val actual   = DecVer.unsafeParse(version)

    actual ==== expected
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testDecVerUnsafeParseNull: Result = {
    @SuppressWarnings(Array("org.wartremover.warts.Null"))
    val version: String = null // scalafix:ok DisableSyntax.null
    val expected        = DecVer.ParseError.nullValue
    val actual          = Try(DecVer.unsafeParse(version)).toEither

    (actual matchPattern {
      case Left(err: DecVer.ParseError.NullValue.type) =>
        err.getMessage ==== expected.render
    }).log(s"Expected DecVer.ParseError.NullValue thrown but got ${actual.toString}")
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testDecVerUnsafeParseEmptyString: Result = {
    val version: String = ""
    val expected        = DecVer.ParseError.empty
    val actual          = Try(DecVer.unsafeParse(version)).toEither

    (actual matchPattern {
      case Left(err: DecVer.ParseError.Empty.type) =>
        err.getMessage ==== expected.render
    }).log(s"Expected DecVer.ParseError.Empty thrown but got ${actual.toString}")
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  def testDecVerUnsafeParseInvalid: Property = for {
    input <- Gen.string(Gen.unicode, Range.linear(1, 100)).log("input")
  } yield {

    val expected = DecVer.ParseError.invalid(input)
    val actual   = Try(DecVer.unsafeParse(input)).toEither

    (actual matchPattern {
      case Left(err @ DecVer.ParseError.Invalid(version)) =>
        err.getMessage ==== expected.render and version ==== input
    }).log(s"Expected DecVer.ParseError.Invalid thrown but got ${actual.toString}")
  }

  def testDecVerToSemVer: Property = for {
    semVer <- SemVerGens
                .genSemVerWithOnlyMajorMinorPatch(
                  Range.linear(0, Int.MaxValue),
                  Range.linear(0, Int.MaxValue),
                  Range.linear(0, Int.MaxValue)
                )
                .log("semVer")
  } yield {
    val decVer   = DecVer(DecVer.Major(semVer.major.value), DecVer.Minor(semVer.minor.value))
    val expected = semVer.copy(patch = SemVer.patch0)
    val actual   = decVer.toSemVer
    actual ==== expected
  }

  def testDecVerFromSemVer: Property = for {
    semVer <- SemVerGens
                .genSemVerWithOnlyMajorMinorPatch(
                  Range.linear(0, Int.MaxValue),
                  Range.linear(0, Int.MaxValue),
                  Range.linear(0, Int.MaxValue)
                )
                .log("semVer")
  } yield {
    val expected = DecVer(DecVer.Major(semVer.major.value), DecVer.Minor(semVer.minor.value))
    val actual   = DecVer.fromSemVer(semVer)
    actual ==== expected
  }

  def testDecVerIncreaseMajor: Property = for {
    major <- Gens.genMajorWithMax(Int.MaxValue >> 1).log("major")
    minor <- Gens.genMinorWithMax(Int.MaxValue >> 1).log("minor")
  } yield {
    val decVer   = DecVer(major, minor)
    val expected = DecVer(DecVer.Major(major.value + 1), DecVer.Minor(minor.value))
    val actual   = decVer.increaseMajor

    actual ==== expected
  }

  def testDecVerIncreaseMinor: Property = for {
    major <- Gens.genMajorWithMax(Int.MaxValue >> 1).log("major")
    minor <- Gens.genMinorWithMax(Int.MaxValue >> 1).log("minor")
  } yield {
    val decVer   = DecVer(major, minor)
    val expected = DecVer(DecVer.Major(major.value), DecVer.Minor(minor.value + 1))
    val actual   = decVer.increaseMinor

    actual ==== expected
  }

}
