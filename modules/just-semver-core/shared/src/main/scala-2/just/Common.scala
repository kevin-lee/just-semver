package just

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
    def compareElems(y: Seq[A])(implicit ordering: Ordering[A]): Int = {
      /* The name is seqDerivedOrdering in 2.12 and seqOrdering in 2.13, so find it by type. */
      import scala.math.Ordering.Implicits._
      Ordering[Seq[A]].compare(x, y)
    }
  }
  // $COVERAGE-ON$
}
