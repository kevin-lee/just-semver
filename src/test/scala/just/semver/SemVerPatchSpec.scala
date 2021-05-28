package just.semver

import hedgehog._
import hedgehog.runner._

/** @author
  *   Kevin Lee
  * @since
  *   2018-11-04
  */
object SemVerPatchSpec extends Properties {

  override def tests: List[Test] = List(
    property("Two SemVers with the same Patch and the rest are equal then it should be equal", testSamePatchs),
    property(
      "Two SemVers with the different Patchs and the rest are equal then it should be not equal",
      testDifferentPatchs
    ),
    property("Test SemVer(Patch(less)) < SemVer(Patch(greater)) is true", testPatchLessCase),
    property("Test SemVer(Patch(greater)) > SemVer(Patch(less)) is true", testPatchMoreCase),
    property("Test SemVer(same Patch) <= SemVer(same Patch) is true", testLeeThanEqualWithSamePatchs),
    property("Test SemVer(Patch(less)) <= SemVer(Patch(greater)) is true", testLeeThanEqualWithLess),
    property("Test SemVer(same Patch) >= SemVer(same Patch) is true", testMoreThanEqualWithSamePatchs),
    property("Test SemVer(Patch(greater)) >= SemVer(Patch(less)) is true", testMoreThanEqualWithGreater)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testSamePatchs: Property = for {
    patch <- Gens.genPatch.log("patch")
  } yield {
    val v1 = SemVer.withPatch(patch)
    val v2 = SemVer.withPatch(patch)
    Result.assert(v1 == v2).log("patch == patch")
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testDifferentPatchs: Property = for {
    patch1AndPatch2 <- Gens.genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemVer.withPatch(patch1)
    val v2 = SemVer.withPatch(patch2)
    Result.assert(v1 != v2).log("patch1 != patch2")
  }

  def testPatchLessCase: Property = for {
    patch1AndPatch2 <- Gens.genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemVer.withPatch(patch1)
    val v2 = SemVer.withPatch(patch2)
    Result.assert(v1 < v2).log("patch1 < patch2")
  }

  def testPatchMoreCase: Property = for {
    patch1AndPatch2 <- Gens.genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemVer.withPatch(patch1)
    val v2 = SemVer.withPatch(patch2)
    Result.assert(v2 > v1).log("patch2 > patch1")
  }

  def testLeeThanEqualWithSamePatchs: Property = for {
    patch <- Gens.genPatch.log("patch")
  } yield {
    val v1 = SemVer.withPatch(patch)
    val v2 = SemVer.withPatch(patch)
    Result.assert(v1 <= v2).log("patch1 <= patch2")
  }

  def testLeeThanEqualWithLess: Property = for {
    patch1AndPatch2 <- Gens.genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemVer.withPatch(patch1)
    val v2 = SemVer.withPatch(patch2)
    Result.assert(v1 <= v2).log("patch1 <= patch2")
  }

  def testMoreThanEqualWithSamePatchs: Property = for {
    patch <- Gens.genPatch.log("patch")
  } yield {
    val v1 = SemVer.withPatch(patch)
    val v2 = SemVer.withPatch(patch)
    Result.assert(v1 >= v2)
  }

  def testMoreThanEqualWithGreater: Property = for {
    patch1AndPatch2 <- Gens.genMinMaxPatches.log("(patch1, patch2)")
    (patch1, patch2) = patch1AndPatch2
  } yield {
    val v1 = SemVer.withPatch(patch1)
    val v2 = SemVer.withPatch(patch2)
    Result.assert(v2 >= v1).log("patch2 >= patch1")
  }

}
