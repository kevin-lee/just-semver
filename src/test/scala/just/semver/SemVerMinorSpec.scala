package just.semver

import hedgehog._
import hedgehog.runner._

/**
 * @author Kevin Lee
 * @since 2018-11-04
 */
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
    minor <- Gens.genMinor.log("minor")
  } yield {
    val v1 = SemVer.withMinor(minor)
    val v2 = SemVer.withMinor(minor)
    Result.assert(v1 == v2).log("minor == minor")
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDifferentMinors: Property = for {
    minor1AndMinor2 <- Gens.genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemVer.withMinor(minor1)
    val v2 = SemVer.withMinor(minor2)
    Result.assert(v1 != v2).log("minor1 != minor2")
  }

  def testMinorLessCase: Property = for {
    minor1AndMinor2 <- Gens.genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemVer.withMinor(minor1)
    val v2 = SemVer.withMinor(minor2)
    Result.assert(v1 < v2).log("minor1 < minor2")
  }

  def testMinorMoreCase: Property = for {
    minor1AndMinor2 <- Gens.genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemVer.withMinor(minor1)
    val v2 = SemVer.withMinor(minor2)
    Result.assert(v2 > v1).log("minor2 > minor1")
  }

  def testLeeThanEqualWithSameMinors: Property = for {
    minor <- Gens.genMinor.log("minor")
  } yield {
    val v1 = SemVer.withMinor(minor)
    val v2 = SemVer.withMinor(minor)
    Result.assert(v1 <= v2).log("minor1 <= minor2")
  }

  def testLeeThanEqualWithLess: Property = for {
    minor1AndMinor2 <- Gens.genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemVer.withMinor(minor1)
    val v2 = SemVer.withMinor(minor2)
    Result.assert(v1 <= v2).log("minor1 <= minor2")
  }

  def testMoreThanEqualWithSameMinors: Property = for {
    minor <- Gens.genMinor.log("minor")
  } yield {
    val v1 = SemVer.withMinor(minor)
    val v2 = SemVer.withMinor(minor)
    Result.assert(v1 >= v2)
  }

  def testMoreThanEqualWithGreater: Property = for {
    minor1AndMinor2 <- Gens.genMinMaxMinors.log("(minor1, minor2)")
    (minor1, minor2) = minor1AndMinor2
  } yield {
    val v1 = SemVer.withMinor(minor1)
    val v2 = SemVer.withMinor(minor2)
    Result.assert(v2 >= v1).log("minor2 >= minor1")
  }

}