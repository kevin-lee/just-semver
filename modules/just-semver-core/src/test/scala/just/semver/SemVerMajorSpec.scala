package just.semver

import hedgehog._
import hedgehog.runner._

/** @author
  *   Kevin Lee
  * @since
  *   2018-11-04
  */
object SemVerMajorSpec extends Properties {

  override def tests: List[Test] = List(
    property("Two SemVers with the same Major and the rest are equal then it should be equal", testSameMajors),
    property(
      "Two SemVers with the different Majors and the rest are equal then it should be not equal",
      testDifferentMajors
    ),
    property("Test SemVer(Major(less)) < SemVer(Major(greater)) is true", testMajorLessCase),
    property("Test SemVer(Major(greater)) > SemVer(Major(less)) is true", testMajorMoreCase),
    property("Test SemVer(same Major) <= SemVer(same Major) is true", testLeeThanEqualWithSameMajors),
    property("Test SemVer(Major(less)) <= SemVer(Major(greater)) is true", testLeeThanEqualWithLess),
    property("Test SemVer(same Major) >= SemVer(same Major) is true", testMoreThanEqualWithSameMajors),
    property("Test SemVer(Major(greater)) >= SemVer(Major(less)) is true", testMoreThanEqualWithGreater)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSameMajors: Property = for {
    major <- Gens.genMajor.log("major")
  } yield {
    val v1 = SemVer.withMajor(major)
    val v2 = SemVer.withMajor(major)
    Result.assert(v1 == v2).log("major == major")
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDifferentMajors: Property = for {
    major1AndMajor2 <- Gens.genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemVer.withMajor(major1)
    val v2 = SemVer.withMajor(major2)
    Result.assert(v1 != v2).log("major1 != major2")
  }

  def testMajorLessCase: Property = for {
    major1AndMajor2 <- Gens.genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemVer.withMajor(major1)
    val v2 = SemVer.withMajor(major2)
    Result.assert(v1 < v2).log("major1 < major2")
  }

  def testMajorMoreCase: Property = for {
    major1AndMajor2 <- Gens.genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemVer.withMajor(major1)
    val v2 = SemVer.withMajor(major2)
    Result.assert(v2 > v1).log("major2 > major1")
  }

  def testLeeThanEqualWithSameMajors: Property = for {
    major <- Gens.genMajor.log("major")
  } yield {
    val v1 = SemVer.withMajor(major)
    val v2 = SemVer.withMajor(major)
    Result.assert(v1 <= v2).log("major1 <= major2")
  }

  def testLeeThanEqualWithLess: Property = for {
    major1AndMajor2 <- Gens.genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemVer.withMajor(major1)
    val v2 = SemVer.withMajor(major2)
    Result.assert(v1 <= v2).log("major1 <= major2")
  }

  def testMoreThanEqualWithSameMajors: Property = for {
    major <- Gens.genMajor.log("major")
  } yield {
    val v1 = SemVer.withMajor(major)
    val v2 = SemVer.withMajor(major)
    Result.assert(v1 >= v2)
  }

  def testMoreThanEqualWithGreater: Property = for {
    major1AndMajor2 <- Gens.genMinMaxMajors.log("(major1, major2)")
    (major1, major2) = major1AndMajor2
  } yield {
    val v1 = SemVer.withMajor(major1)
    val v2 = SemVer.withMajor(major2)
    Result.assert(v2 >= v1).log("major2 >= major1")
  }

}
