package kevinlee

import hedgehog._

/**
  * @author Kevin Lee
  * @since 2018-11-04
  */
object GenPlus {

  def range[A](r: Range[A])(f: Range[A] => Gen[A]): Gen[A] = {
    val (min, max) = r.bounds(Size(Size.max))
    Gen.frequency1(
      (1, Gen.constant(min))
      , (1, Gen.constant(max))
      , (1, Gen.constant(r.origin))
      , (97, f(r))
    )
  }

}
