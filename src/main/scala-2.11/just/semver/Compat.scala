package just.semver

import scala.util.{Try, Success, Failure}

trait Compat {

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
  implicit def eitherCompat[A, B](either: Either[A, B]): Compat.EitherCompat[A, B] = new Compat.EitherCompat(either)
  @SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion"))
  implicit def tryCompat[A](t: Try[A]): Compat.TryCompat[A]                        = new Compat.TryCompat(t)
}

object Compat {
  final class EitherCompat[A, B](private val either: Either[A, B]) extends AnyVal {

    @inline def map[C](f: B => C): Either[A, C] =
      either.right.map(f)

    @inline def flatMap[C >: A, D](f: B => Either[C, D]): Either[C, D] =
      either.right.flatMap(f)

    def filterOrElse[A1 >: A](p: B => Boolean, zero: => A1): Either[A1, B] = either match {
      case Right(b) if !p(b) => Left(zero)
      case _ => either
    }
  }

  final class TryCompat[A](private val t: Try[A]) extends AnyVal {
    def toEither: Either[Throwable, A] = t match {
      case Success(value) => Right(value)
      case Failure(err) => Left(err)
    }
  }
}
