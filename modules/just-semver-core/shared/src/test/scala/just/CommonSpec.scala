package just

import hedgehog._
import hedgehog.runner._
import just.Common._

import scala.collection.compat.immutable._

/** @author Kevin Lee
  * @since 2026-10-10
  */
object CommonSpec extends Properties {

  override def tests: List[Test] = List(
    property("a === a should return true", testEqualSame),
    property("a === b should return the same result as a == b", testEqualSameAsEquals),
    property("a !== a should return false", testNotEqualSame),
    property("a !== b should return the same result as a != b", testNotEqualSameAsNotEquals),
    property("a !== b should return the negation of a === b", testNotEqualIsNegationOfEqual),
    example("none[A] should return None", testNone),
    property("a.some should return Some(a)", testSome),
    property("a.asRight[B] should return Right(a)", testAsRight),
    property("a.asLeft[B] should return Left(a)", testAsLeft),
    example("Seq().compareElems(Seq()) should return 0", testCompareElemsBothEmpty),
    property("Seq().compareElems(non-empty Seq) should return -1", testCompareElemsEmptyAndNonEmpty),
    property("(non-empty Seq).compareElems(Seq()) should return 1", testCompareElemsNonEmptyAndEmpty),
    property("x.compareElems(x) should return 0", testCompareElemsReflexive),
    property(
      "x.compareElems(the same elements in a different Seq type) should return 0",
      testCompareElemsSameElemsDifferentSeqType
    ),
    property("x.compareElems(x ++ non-empty Seq) should return -1", testCompareElemsProperPrefix),
    property("(x ++ non-empty Seq).compareElems(x) should return 1", testCompareElemsProperPrefixReversed),
    property(
      "compareElems should return the result of the first different elements (less) and ignore the rest",
      testCompareElemsFirstDifferentElemLess
    ),
    property(
      "compareElems should return the result of the first different elements (greater) and ignore the rest",
      testCompareElemsFirstDifferentElemGreater
    ),
    property("compareElems should be antisymmetric", testCompareElemsAntisymmetric),
    property("compareElems should be transitive", testCompareElemsTransitive).withTests(1000),
    property(
      "compareElems should return the same result as the model with Ordering[Int]",
      testCompareElemsModelInt
    ).withTests(1000),
    property(
      "compareElems should return the same result as the model with Ordering[Int].reverse",
      testCompareElemsModelIntReverse
    ).withTests(1000),
    property(
      "compareElems should return the same result as the model with Ordering[String]",
      testCompareElemsModelString
    ).withTests(1000),
    property(
      "compareElems should return the same result for List, Vector, ArraySeq and LazyList",
      testCompareElemsSeqTypes
    ),
    example("compareElems should be stack-safe for long equal Seqs", testCompareElemsLongEqual),
    example("compareElems should be stack-safe for long Seqs with the difference at the end", testCompareElemsLongDiff),
  )

  /* Generators { */

  private val genSmallInt: Gen[Int] = Gen.int(Range.linear(-5, 5))

  /* A small element range and short length make equal elements and common prefixes common. */
  private val genSmallInts: Gen[List[Int]] = Gen.list(genSmallInt, Range.linear(0, 10))

  private val genNonEmptySmallInts: Gen[List[Int]] = Gen.list(genSmallInt, Range.linear(1, 10))

  private val genTinyInts: Gen[List[Int]] = Gen.list(Gen.int(Range.linear(0, 1)), Range.linear(0, 3))

  private val genSmallStrings: Gen[List[String]] =
    Gen.list(Gen.string(Gen.alpha, Range.linear(0, 3)), Range.linear(0, 5))

  /* } Generators */

  /* Model: an independent implementation of the lexicographic order. */
  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  private def modelCompare[A](x: Seq[A], y: Seq[A])(ordering: Ordering[A]): Int =
    x.zip(y)
      .map { case (a, b) => ordering.compare(a, b) }
      .find(_ != 0)
      .getOrElse(Ordering.Int.compare(x.length, y.length))

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testEqualSame: Property = for {
    a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
  } yield {
    Result.assert(a === a)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testEqualSameAsEquals: Property = for {
    a <- genSmallInt.log("a")
    b <- genSmallInt.log("b")
  } yield {
    (a === b) ==== (a == b)
  }

  def testNotEqualSame: Property = for {
    a <- Gen.string(Gen.unicode, Range.linear(0, 10)).log("a")
  } yield {
    Result.assert(!(a !== a))
  }

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  def testNotEqualSameAsNotEquals: Property = for {
    a <- genSmallInt.log("a")
    b <- genSmallInt.log("b")
  } yield {
    (a !== b) ==== (a != b)
  }

  def testNotEqualIsNegationOfEqual: Property = for {
    a <- Gen.string(Gen.alpha, Range.linear(0, 1)).log("a")
    b <- Gen.string(Gen.alpha, Range.linear(0, 1)).log("b")
  } yield {
    (a !== b) ==== !(a === b)
  }

  def testNone: Result =
    none[Int] ==== None

  def testSome: Property = for {
    a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
  } yield {
    a.some ==== Some(a)
  }

  def testAsRight: Property = for {
    a <- Gen.int(Range.linear(Int.MinValue, Int.MaxValue)).log("a")
  } yield {
    a.asRight[String] ==== Right[String, Int](a)
  }

  def testAsLeft: Property = for {
    a <- Gen.string(Gen.unicode, Range.linear(0, 10)).log("a")
  } yield {
    a.asLeft[Int] ==== Left[String, Int](a)
  }

  def testCompareElemsBothEmpty: Result =
    Result.all(
      List(
        List.empty[Int].compareElems(List.empty[Int]) ==== 0,
        Vector.empty[Int].compareElems(List.empty[Int]) ==== 0,
        Seq.empty[Int].compareElems(Seq.empty[Int]) ==== 0,
      )
    )

  def testCompareElemsEmptyAndNonEmpty: Property = for {
    y <- genNonEmptySmallInts.log("y")
  } yield {
    List.empty[Int].compareElems(y) ==== -1
  }

  def testCompareElemsNonEmptyAndEmpty: Property = for {
    x <- genNonEmptySmallInts.log("x")
  } yield {
    x.compareElems(List.empty[Int]) ==== 1
  }

  def testCompareElemsReflexive: Property = for {
    x <- genSmallInts.log("x")
  } yield {
    x.compareElems(x) ==== 0
  }

  def testCompareElemsSameElemsDifferentSeqType: Property = for {
    x <- genSmallInts.log("x")
  } yield {
    Result.all(
      List(
        x.compareElems(x.toVector) ==== 0,
        x.toVector.compareElems(x) ==== 0,
        x.compareElems(ArraySeq(x: _*)) ==== 0,
        LazyList(x: _*).compareElems(x) ==== 0,
      )
    )
  }

  def testCompareElemsProperPrefix: Property = for {
    x    <- genSmallInts.log("x")
    more <- genNonEmptySmallInts.log("more")
  } yield {
    x.compareElems(x ++ more) ==== -1
  }

  def testCompareElemsProperPrefixReversed: Property = for {
    x    <- genSmallInts.log("x")
    more <- genNonEmptySmallInts.log("more")
  } yield {
    (x ++ more).compareElems(x) ==== 1
  }

  def testCompareElemsFirstDifferentElemLess: Property = for {
    prefix <- genSmallInts.log("prefix")
    a      <- genSmallInt.log("a")
    delta  <- Gen.int(Range.linear(1, 5)).log("delta")
    restX  <- genSmallInts.log("restX")
    restY  <- genSmallInts.log("restY")
  } yield {
    val x = prefix ++ (a :: restX)
    val y = prefix ++ ((a + delta) :: restY)
    x.compareElems(y) ==== -1
  }

  def testCompareElemsFirstDifferentElemGreater: Property = for {
    prefix <- genSmallInts.log("prefix")
    a      <- genSmallInt.log("a")
    delta  <- Gen.int(Range.linear(1, 5)).log("delta")
    restX  <- genSmallInts.log("restX")
    restY  <- genSmallInts.log("restY")
  } yield {
    val x = prefix ++ ((a + delta) :: restX)
    val y = prefix ++ (a :: restY)
    x.compareElems(y) ==== 1
  }

  def testCompareElemsAntisymmetric: Property = for {
    x <- genSmallInts.log("x")
    y <- genSmallInts.log("y")
  } yield {
    math.signum(x.compareElems(y)) ==== -math.signum(y.compareElems(x))
  }

  def testCompareElemsTransitive: Property = for {
    x <- genTinyInts.log("x")
    y <- genTinyInts.log("y")
    z <- genTinyInts.log("z")
  } yield {
    val xy = x.compareElems(y)
    val yz = y.compareElems(z)
    val xz = x.compareElems(z)
    Result
      .assert(!(xy <= 0 && yz <= 0) || xz <= 0)
      .log("x <= y and y <= z, but x > z")
  }

  def testCompareElemsModelInt: Property = for {
    x <- genSmallInts.log("x")
    y <- genSmallInts.log("y")
  } yield {
    x.compareElems(y) ==== modelCompare(x, y)(Ordering.Int)
  }

  def testCompareElemsModelIntReverse: Property = for {
    x <- genSmallInts.log("x")
    y <- genSmallInts.log("y")
  } yield {
    /* A local implicit takes priority over Ordering.Int, and it works for both Scala 2 and Scala 3. */
    implicit val ordering: Ordering[Int] = Ordering.Int.reverse
    x.compareElems(y) ==== modelCompare(x, y)(ordering)
  }

  /* Ordering[String].compare can return any Int (not only -1, 0 and 1),
   * so this also checks that compareElems returns the element result as it is.
   */
  def testCompareElemsModelString: Property = for {
    x <- genSmallStrings.log("x")
    y <- genSmallStrings.log("y")
  } yield {
    x.compareElems(y) ==== modelCompare(x, y)(Ordering.String)
  }

  def testCompareElemsSeqTypes: Property = for {
    x <- genSmallInts.log("x")
    y <- genSmallInts.log("y")
  } yield {
    val expected = modelCompare(x, y)(Ordering.Int)
    Result.all(
      List(
        x.compareElems(y) ==== expected,
        x.toVector.compareElems(y.toVector) ==== expected,
        ArraySeq(x: _*).compareElems(ArraySeq(y: _*)) ==== expected,
        LazyList(x: _*).compareElems(LazyList(y: _*)) ==== expected,
        x.compareElems(y.toVector) ==== expected,
        x.toVector.compareElems(ArraySeq(y: _*)) ==== expected,
        ArraySeq(x: _*).compareElems(LazyList(y: _*)) ==== expected,
      )
    )
  }

  private val LongLength = 100000

  def testCompareElemsLongEqual: Result = {
    val x = List.fill(LongLength)(1)
    val y = List.fill(LongLength)(1)
    x.compareElems(y) ==== 0
  }

  def testCompareElemsLongDiff: Result = {
    val x = List.tabulate(LongLength + 1)(i => if (i < LongLength) 1 else 2)
    val y = List.fill(LongLength + 1)(1)
    Result.all(
      List(
        x.compareElems(y) ==== 1,
        y.compareElems(x) ==== -1,
      )
    )
  }

}
