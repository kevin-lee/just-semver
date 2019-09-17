package just

object EitherCompat {

  @inline final def map[A, B, C](either: Either[A, B])(f: B => C): Either[A, C] =
    either.right.map(f)

  @inline final def flatMap[A, B, C](either: Either[A, B])(f: B => Either[A, C]): Either[A, C] =
    either.right.flatMap(f)

}
