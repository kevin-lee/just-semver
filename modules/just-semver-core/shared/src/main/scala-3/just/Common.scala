package just

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
    def compareElems(y: Seq[A])(using ordering: Ordering[A]): Int =
      Ordering.Implicits.seqOrdering[Seq, A].compare(x, y)
  }
  // $COVERAGE-ON$

}
