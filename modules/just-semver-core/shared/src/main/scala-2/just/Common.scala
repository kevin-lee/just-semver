package just

import scala.annotation.tailrec

/** @author
  *   Kevin Lee
  * @since
  *   2018-12-15
  */
object Common {
  // $COVERAGE-OFF$

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  implicit final class EqualA[A](private val a1: A) extends AnyVal {
    @inline def ===(a2: A): Boolean = a1 == a2
    @inline def !==(a2: A): Boolean = a1 != a2
  }

  @inline def none[A]: Option[A] = None

  implicit final class Ops[A](private val a: A) extends AnyVal {
    @inline def some: Option[A] = Some(a)

    @inline def asRight[B]: Either[B, A] = Right[B, A](a)
    @inline def asLeft[B]: Either[A, B]  = Left[A, B](a)
  }

  implicit final class SeqPlus[A](private val x: Seq[A]) extends AnyVal {
    def compareElems(y: Seq[A])(implicit ordering: Ordering[A]): Int =
      Common.compareElems(x, y)
  }

  @tailrec
  private def compareElems[A: Ordering](x: Seq[A], y: Seq[A]): Int = {
    val ordering = implicitly[Ordering[A]]
    (x, y) match {
      case (head1 +: tail1, head2 +: tail2) =>
        val result = ordering.compare(head1, head2)
        if (result === 0) {
          compareElems(tail1, tail2)
        } else {
          result
        }

      case (Seq(), _ +: _) =>
        -1
      case (_ +: _, Seq()) =>
        1
      case (Seq(), Seq()) | (_, _) =>
        0
    }
  }
  // $COVERAGE-ON$
}
