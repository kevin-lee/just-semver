package io.kevinlee

import scala.annotation.tailrec
import CommonPredef._

/**
  * @author Kevin Lee
  * @since 2018-12-15
  */
object Common {
  @tailrec
  def compareElems[A <: Ordered[A]](x: Seq[A], y: Seq[A]): Int =
    (x, y) match {
      case (head1 +: tail1, head2 +: tail2) =>
        val result = head1.compare(head2)
        if (result === 0) {
          compareElems(tail1, tail2)
        } else {
          result
        }
      case (Seq(), _ +: _) =>
        -1
      case (_ +: _, Seq()) =>
        1
    }
}
