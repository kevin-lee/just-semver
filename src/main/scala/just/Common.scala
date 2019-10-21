package just

import scala.annotation.tailrec

import just.fp.syntax._

/**
  * @author Kevin Lee
  * @since 2018-12-15
  */
object Common {
  // $COVERAGE-OFF$
  @tailrec
  def compareElems[A : Ordering](x: Seq[A], y: Seq[A]): Int = {
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
      case (Seq(), Seq()) =>
        0
    }
  }
  // $COVERAGE-ON$
}
