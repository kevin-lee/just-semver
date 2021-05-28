package just.semver

trait Compat {

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
  implicit def eitherCompat[A, B](either: Either[A, B]): Compat.EitherCompat[A, B] = new Compat.EitherCompat(either)
}

object Compat {
  final class EitherCompat[A, B](val either: Either[A, B]) extends AnyVal {

    @inline def map[C](f: B => C): Either[A, C] =
      either.right.map(f)

    @inline def flatMap[C >: A, D](f: B => Either[C, D]): Either[C, D] =
      either.right.flatMap(f)
  }
}
