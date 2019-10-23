# just-semver

[![Build Status](https://github.com/Kevin-Lee/just-semver/workflows/Build%20All/badge.svg)](https://github.com/Kevin-Lee/just-semver/actions?workflow=Build+All)
[![Release Status](https://github.com/Kevin-Lee/just-semver/workflows/Release/badge.svg)](https://github.com/Kevin-Lee/just-semver/actions?workflow=Release)
[![Download](https://api.bintray.com/packages/kevinlee/maven/just-semver/images/download.svg)](https://bintray.com/kevinlee/maven/just-semver/_latestVersion)

[![Coverage Status](https://coveralls.io/repos/github/Kevin-Lee/just-semver/badge.svg?branch=master)](https://coveralls.io/github/Kevin-Lee/just-semver?branch=master)

Semantic Versioning (SemVer) for Scala

# Get just-semver
```scala
resolvers += "Just Repo" at "https://dl.bintray.com/kevinlee/maven"

libraryDependencies += "io.kevinlee" %% "just-semver" % "0.1.0"
```

# How to Use
```scala
import just.semver.SemVer

val v = SemVer.parse("1.0.0")
// v: Either[ParseError, SemVer] = Right(SemVer(Major(1), Minor(0), Patch(0), None, None))

// To render it to `String`,
v.map(SemVer.render)
// Either[ParseError, String] = Right("1.0.0")

// Invalid version
SemVer.parse("a1.0.0")
// v: Either[ParseError, SemVer] = Left(InvalidVersionStringError("a1.0.0"))

```

```scala
import just.semver.SemVer

// parse unsafe - NOT RECOMMENDED!!!
val v = SemVer.parseUnsafe("1.0.0")
// v: SemVer = SemVer(Major(1), Minor(0), Patch(0), None, None)

// to String
SemVer.render(v)
// String = "1.0.0"


// Invalid version
SemVer.parseUnsafe("a1.0.0")
// java.lang.RuntimeException: Invalid SemVer String. value: a1.0.0
```

* SemVer with pre-release info
```scala
import just.semver.SemVer

SemVer.parse("1.0.0-beta1")
// Either[ParseError, SemVer] = Right(
//   SemVer(
//       Major(1), Minor(0), Patch(0)
//     , Some(PreRelease(List(Dsv(List(Alphabet("beta"), Num("1"))))))
//     , None
//     )
//   )

val v = SemVer.parse("1.0.0-3.123.9a")
// Either[ParseError, SemVer] = 
// Right(
//   SemVer(
//       Major(1), Minor(0), Patch(0)
//     , Some(
//         PreRelease(List(
//             Dsv(List(Num("3")))
//           , Dsv(List(Num("123")))
//           , Dsv(List(Num("9"), Alphabet("a")))
//         ))
//       )
//     , None
//   )
// )

v.map(SemVer.render)
// v: [ParseError, String] = Right("1.0.0-3.123.9a")
```

* SemVer with build meta-info
```scala
import just.semver.SemVer

val v = SemVer.parse("1.0.0+100.0.12abc")
// v: Either[ParseError, SemVer] = 
// Right(
//   SemVer(
//      Major(1),Minor(0),Patch(0)
//    , None
//    , Some(
//        BuildMetaInfo(
//          List(Dsv(List(Num(100))), Dsv(List(Num(0))), Dsv(List(Num(12), Alphabet(abc))))
//        )
//      )
//   )
// )

v.map(SemVer.render)
// Either[ParseError, String] = Right("1.0.0+100.0.12abc")
```

* SemVer with pre-release info and build meta-info
```scala
import just.semver.SemVer

SemVer.parse("1.0.0-beta1")
// Either[ParseError, SemVer] = Right(
//   SemVer(
//       Major(1), Minor(0), Patch(0)
//     , Some(PreRelease(List(Dsv(List(Alphabet("beta"), Num("1"))))))
//     , None
//     )
//   )

val v = SemVer.parse("1.0.0-3.123.9a+100.0.12abc")
// Either[ParseError, SemVer] = 
// Right(
//   SemVer(
//       Major(1), Minor(0), Patch(0)
//     , Some(
//          PreRelease(List(
//              Dsv(List(Num("3")))
//            , Dsv(List(Num("123")))
//            , Dsv(List(Num("9"), Alphabet("a")))
//          ))
//       )
//     , Some(
//         BuildMetaInfo(
//           List(
//               Dsv(List(Num(100)))
//             , Dsv(List(Num(0)))
//             , Dsv(List(Num(12), Alphabet(abc)))
//           )
//         )
//       )
//   )
// )

v.map(SemVer.render)
// v: [ParseError, String] = Right("1.0.0-3.123.9a+100.0.12abc")
```

## Compare `SamVer`
```scala
import just.semver.SemVer

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.1")
} yield a < b
// Either[ParseError, Boolean] = Right(true)

for {
 a <- SemVer.parse("1.0.1")
 b <- SemVer.parse("1.0.0")
} yield a < b
// Either[ParseError, Boolean] = Right(false)

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.1")
} yield a <= b
// Either[ParseError, Boolean] = Right(true)

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.0")
} yield a <= b
// Either[ParseError, Boolean] = Right(true)

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.0")
} yield a == b
// Either[ParseError, Boolean] = Right(true)

for {
 a <- SemVer.parse("1.0.1")
 b <- SemVer.parse("1.0.0")
} yield a > b
// Either[ParseError, Boolean] = Right(true)

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.1")
} yield a > b
// Either[ParseError, Boolean] = Right(false)

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.1")
} yield a >= b
// Either[ParseError, Boolean] = Right(false)

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.0")
} yield a >= b
// Either[ParseError, Boolean] = Right(true)

for {
 a <- SemVer.parse("1.0.1")
 b <- SemVer.parse("1.0.0")
} yield a >= b
// Either[ParseError, Boolean] = Right(true)
```
