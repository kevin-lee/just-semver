package just

import scala.annotation.tailrec

/** @author
  *   Kevin Lee
  * @since
  *   2018-12-15
  */
object Common {
  // $COVERAGE-OFF$

  extension [A](a1: A)(using CanEqual[A, A]) {
    @SuppressWarnings(Array("org.wartremover.warts.Equals"))
    inline def ===(a2: A): Boolean = a1 == a2
    @SuppressWarnings(Array("org.wartremover.warts.Equals"))
    inline def !==(a2: A): Boolean = a1 != a2
  }

  inline def none[A]: Option[A] = None

  extension [A](a: A) {
    inline def some: Option[A] = Some(a)

    inline def asRight[B]: Either[B, A] = Right[B, A](a)
    inline def asLeft[B]: Either[A, B]  = Left[A, B](a)
  }

  extension [A](x: Seq[A]) {
    def compareElems(y: Seq[A])(using ordering: Ordering[A]): Int = {
      @tailrec
      def compareElems0(x: Seq[A], y: Seq[A]): Int =
        (x, y) match {
          case (head1 +: tail1, head2 +: tail2) =>
            val result = ordering.compare(head1, head2)
            if (result == 0) {
              compareElems0(tail1, tail2)
            } else {
              result
            }

          case (Seq(), _ +: _) =>
            -1
          case (_ +: _, Seq()) =>
            1
          case (Seq(), Seq()) =>
            0
        }
      compareElems0(x, y)
    }
  }
  // $COVERAGE-ON$

}
