
# How to Use

:::caution NOTE
For now, please do not use any types and methods from the package other than `just.semver`.
* `just.semver`: Fine
* `just.semver.matcher` or any other `just.semver.xxx` packages: You can use it but not recommended as it's currently experimental.
:::

## `SemVer.parse`

```scala mdoc:reset-object
import just.semver.SemVer

val v = SemVer.parse("1.0.0")

// To render it to `String`,
v.map(_.render)

// Invalid version
SemVer.parse("a1.0.0")

```

## `SemVer.unsafeParse`

```scala mdoc:reset-object
import just.semver.SemVer

// parse unsafe - NOT RECOMMENDED!!!
val v = SemVer.unsafeParse("1.0.0")

// to String
v.render
```

```scala mdoc:crash

// Invalid version
SemVer.unsafeParse("a1.0.0")
```

## SemVer with `pre-release` info
```scala mdoc:reset-object
import just.semver.SemVer

SemVer.parse("1.0.0-beta1")

val v = SemVer.parse("1.0.0-3.123.9a")

v.map(_.render)
```

## SemVer with build `meta-info`
```scala mdoc:reset-object
import just.semver.SemVer

val v = SemVer.parse("1.0.0+100.0.12abc")

v.map(_.render)
```

## SemVer with `pre-release` info and build `meta-info`
```scala mdoc:reset-object
import just.semver.SemVer

SemVer.parse("1.0.0-beta1")

val v = SemVer.parse("1.0.0-3.123.9a+100.0.12abc")

v.map(_.render)
```

## Compare `SamVer`
```scala mdoc:reset-object
import just.semver.SemVer

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.1")
} yield a < b

for {
 a <- SemVer.parse("1.0.1")
 b <- SemVer.parse("1.0.0")
} yield a < b

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.1")
} yield a <= b

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.0")
} yield a <= b

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.0")
} yield a == b

for {
 a <- SemVer.parse("1.0.1")
 b <- SemVer.parse("1.0.0")
} yield a > b

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.1")
} yield a > b

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.1")
} yield a >= b

for {
 a <- SemVer.parse("1.0.0")
 b <- SemVer.parse("1.0.0")
} yield a >= b

for {
 a <- SemVer.parse("1.0.1")
 b <- SemVer.parse("1.0.0")
} yield a >= b
```

## Matchers
```scala mdoc
SemVer.unsafeParse("1.0.0").unsafeMatches("1.0.0 - 2.0.0")
SemVer.unsafeParse("1.5.0").unsafeMatches("1.0.0 - 2.0.0")
SemVer.unsafeParse("2.0.0").unsafeMatches("1.0.0 - 2.0.0")
SemVer.unsafeParse("0.9.9").unsafeMatches("1.0.0 - 2.0.0")
SemVer.unsafeParse("2.0.1").unsafeMatches("1.0.0 - 2.0.0")

SemVer.unsafeParse("1.0.0").unsafeMatches(">1.0.0 <2.0.0")
SemVer.unsafeParse("1.0.0").unsafeMatches(">=1.0.0 <=2.0.0")
SemVer.unsafeParse("1.5.0").unsafeMatches(">1.0.0 <2.0.0")
SemVer.unsafeParse("2.0.0").unsafeMatches(">1.0.0 <2.0.0")
SemVer.unsafeParse("2.0.0").unsafeMatches(">=1.0.0 <=2.0.0")
SemVer.unsafeParse("0.9.9").unsafeMatches(">=1.0.0 <=2.0.0")
SemVer.unsafeParse("2.0.1").unsafeMatches(">=1.0.0 <=2.0.0")

SemVer.unsafeParse("1.0.0").unsafeMatches("1.0.0 - 2.0.0 || >3.0.0 <4.0.0")
SemVer.unsafeParse("2.0.0").unsafeMatches("1.0.0 - 2.0.0 || >3.0.0 <4.0.0")
SemVer.unsafeParse("3.0.0").unsafeMatches("1.0.0 - 2.0.0 || >3.0.0 <=4.0.0")
SemVer.unsafeParse("3.0.1").unsafeMatches("1.0.0 - 2.0.0 || >3.0.0 <=4.0.0")
SemVer.unsafeParse("4.0.0").unsafeMatches("1.0.0 - 2.0.0 || >3.0.0 <=4.0.0")
```
