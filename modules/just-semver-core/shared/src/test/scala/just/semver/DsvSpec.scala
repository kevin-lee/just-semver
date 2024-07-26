package just.semver

import hedgehog._
import hedgehog.runner._

import scala.collection.compat.immutable._

/** @author Kevin Lee
  * @since 2024-04-22
  */
object DsvSpec extends Properties {
  override def tests: List[Test] = List(
    property("test Dsv.parse(valid String)", testParse),
    property("test Dsv.parse(invalid String)", testParseInvalid),
    example("test Dsv.parse(an empty String)", testParseInvalid2),
  )

  def testParse: Property = for {
    anh1  <- Gen
               .frequency1(
                 45 -> Gen.string(Gen.alpha, Range.linear(1, 3)).map(Anh.alphabet),
                 45 -> Gen.int(Range.linear(0, 999)).map(Anh.num),
               )
               .log("anh1")
    anh2  <- (anh1 match {
               case Anh.Alphabet(_) =>
                 Gen
                   .frequency1(
                     90 -> Gen.int(Range.linear(0, 999)).map(Anh.num),
                     10 -> Gen.constant(Anh.hyphen)
                   )
               case Anh.Num(_) =>
                 Gen
                   .frequency1(
                     90 -> Gen.string(Gen.alpha, Range.linear(1, 3)).map(Anh.alphabet),
                     10 -> Gen.constant(Anh.hyphen)
                   )
               case Anh.Hyphen =>
                 Gen
                   .frequency1(
                     50 -> Gen.string(Gen.alpha, Range.linear(1, 3)).map(Anh.alphabet),
                     50 -> Gen.int(Range.linear(0, 999)).map(Anh.num),
                   )
             }).log("anh2")
    anh3  <- (anh2 match {
               case Anh.Alphabet(_) =>
                 Gen
                   .frequency1(
                     90 -> Gen.int(Range.linear(0, 999)).map(Anh.num),
                     10 -> Gen.constant(Anh.hyphen)
                   )
               case Anh.Num(_) =>
                 Gen
                   .frequency1(
                     90 -> Gen.string(Gen.alpha, Range.linear(1, 3)).map(Anh.alphabet),
                     10 -> Gen.constant(Anh.hyphen)
                   )
               case Anh.Hyphen =>
                 Gen
                   .frequency1(
                     50 -> Gen.string(Gen.alpha, Range.linear(1, 3)).map(Anh.alphabet),
                     50 -> Gen.int(Range.linear(0, 999)).map(Anh.num),
                   )
             }).log("anh3")
    anh4  <- (anh3 match {
               case Anh.Alphabet(_) =>
                 Gen
                   .frequency1(
                     90 -> Gen.int(Range.linear(0, 999)).map(Anh.num),
                     10 -> Gen.constant(Anh.hyphen)
                   )
               case Anh.Num(_) =>
                 Gen
                   .frequency1(
                     90 -> Gen.string(Gen.alpha, Range.linear(1, 3)).map(Anh.alphabet),
                     10 -> Gen.constant(Anh.hyphen)
                   )
               case Anh.Hyphen =>
                 Gen
                   .frequency1(
                     50 -> Gen.string(Gen.alpha, Range.linear(1, 3)).map(Anh.alphabet),
                     50 -> Gen.int(Range.linear(0, 999)).map(Anh.num),
                   )
             }).log("anh4")
    anh5  <- (anh4 match {
               case Anh.Alphabet(_) =>
                 Gen
                   .frequency1(
                     90 -> Gen.int(Range.linear(0, 999)).map(Anh.num),
                     10 -> Gen.constant(Anh.hyphen)
                   )
               case Anh.Num(_) =>
                 Gen
                   .frequency1(
                     90 -> Gen.string(Gen.alpha, Range.linear(1, 3)).map(Anh.alphabet),
                     10 -> Gen.constant(Anh.hyphen)
                   )
               case Anh.Hyphen =>
                 Gen
                   .frequency1(
                     50 -> Gen.string(Gen.alpha, Range.linear(1, 3)).map(Anh.alphabet),
                     50 -> Gen.int(Range.linear(0, 999)).map(Anh.num),
                   )
             }).log("anh5")
    input <- Gen.constant(List(anh1, anh2, anh3, anh4, anh5).map(_.render).mkString).log("input")
  } yield {
    val expected = Dsv(List(anh1, anh2, anh3, anh4, anh5))
    val actual   = Dsv.parse(input)
    actual ==== Right(expected)
  }

  @SuppressWarnings(
    Array("org.wartremover.warts.Equals", "org.wartremover.warts.ToString", "org.wartremover.warts.IterableOps")
  )
  private val someNonAnhCharInts = LazyList
//    .range(1, 100_000)
    .range(1, 100000)
    .filterNot { n =>
      val c = n.toChar
      c.isUpper || c.isLower || c.isDigit || c == '-'
    }
    .foldLeft(Vector.empty[(Int, Int)]) {
      case (Vector(), n) => Vector(n -> n)
      case ((head +: Vector()), n) => if (head._2 + 1 == n) Vector(head._1 -> n) else Vector(head._1 -> head._2, n -> n)
      case (xs, n) => if (xs.last._2 + 1 == n) xs.init :+ (xs.last._1 -> n) else xs :+ (n -> n)
    }
    .toList

  private val someNonAnhChars = someNonAnhCharInts
    .map { case (start, end) => start.toChar -> end.toChar }

  @SuppressWarnings(Array("org.wartremover.warts.IterableOps", "org.wartremover.warts.ToString"))
  def testParseInvalid: Property = for {
    s <- Gen
           .string(
             Gen.choice(
               (Gen.char _).tupled(someNonAnhChars.head),
               someNonAnhChars.tail.map { case (start, end) => Gen.char(start, end) }
             ),
             Range.linear(1, 3)
           )
           .map(_.mkString)
           .log("s")
  } yield {
    val actual = Dsv.parse(s)
    (actual ==== Left(Dsv.DsvParseError.invalidAlphaNumHyphenError(s.head, s.tail.toList)))
      .log(
        s"s=${s.map(_.toInt).mkString("[", ", ", "]")}"
      )
  }

  def testParseInvalid2: Result = {
    val actual = Dsv.parse("")
    actual ==== Left(Dsv.DsvParseError.emptyAlphaNumHyphenError)
  }

}
