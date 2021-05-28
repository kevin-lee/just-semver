package just.semver

import hedgehog._
import hedgehog.runner._

/** @author
  *   Kevin Lee
  * @since
  *   2018-11-04
  */
object AlphaNumHyphenSpec extends Properties {

  override def tests: List[Test] = List(
    property("Num(same).compare(Num(same)) should return 0", testNumEqual),
    property("Num(less).compare(Num(greater)) should return -1", testNumLess),
    property("Num(greater).compare(Num(less)) should return 1", testNumMore),
    property("AlphaHyphen(same).compare(AlphaHyphen(same)) should return 0", testAlphaHyphenEqual),
    property("AlphaHyphen(less).compare(AlphaHyphen(greater)) should return the Int < 0", testAlphaHyphenLess),
    property("AlphaHyphen(greater).compare(AlphaHyphen(less)) should return the Int > 0", testAlphaHyphenMore),
    property("test Anh.render(anh)", testAnhRenderAnh),
    property("test anh.render", testAnhRenderAnh)
  )

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testNumEqual: Property = for {
    num <- Gens.genNum.log("num")
  } yield {
    num.compare(num) ==== 0 and Result.assert(num == num)
  }

  def testNumLess: Property = for {
    minMax      <- Gens.genMinMaxNum.log("(num1, num2)")
    (num1, num2) = minMax
  } yield {
    num1.compare(num2) ==== -1
  }

  def testNumMore: Property = for {
    minMax      <- Gens.genMinMaxNum.log("(num1, num2)")
    (num1, num2) = minMax
  } yield {
    num2.compare(num1) ==== 1
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testAlphaHyphenEqual: Property = for {
    alphaHyphen <- Gens.genAlphabet(10).log("alphaHyphen")
  } yield {
    alphaHyphen.compare(alphaHyphen) ==== 0 and Result.assert(alphaHyphen == alphaHyphen)
  }

  def testAlphaHyphenLess: Property = for {
    alphaHyphenPair             <- Gens.genMinMaxAlphabet(10).log("(alphaHyphen1, alphaHyphen2)")
    (alphaHyphen1, alphaHyphen2) = alphaHyphenPair
  } yield {
    Result.assert(alphaHyphen1.compare(alphaHyphen2) < 0)
  }

  def testAlphaHyphenMore: Property = for {
    alphaHyphenPair             <- Gens.genMinMaxAlphabet(10).log("(alphaHyphen1, alphaHyphen2)")
    (alphaHyphen1, alphaHyphen2) = alphaHyphenPair
  } yield {
    Result.assert(alphaHyphen2.compare(alphaHyphen1) > 0)
  }

  def testAnhRenderAnh: Property = for {
    anh <- Gen.frequency1(3 -> Gens.genNum, 6 -> Gens.genAlphabet(10), 1 -> Gens.genHyphen).log("anh")
  } yield {
    val expected: String =
      anh match {
        case Anh.Alphabet(value) =>
          value
        case Anh.Num(value)      =>
          value
        case Anh.Hyphen          =>
          "-"
      }
    val actual           = Anh.render(anh)
    actual ==== expected
  }

  def testAnhRender: Property = for {
    anh <- Gen.frequency1(3 -> Gens.genNum, 6 -> Gens.genAlphabet(10), 1 -> Gens.genHyphen).log("anh")
  } yield {
    val expected: String = Anh.render(anh)
    val actual           = anh.render
    actual ==== expected
  }

}
