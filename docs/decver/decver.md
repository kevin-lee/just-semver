
# DecVer (Decimal Version)

## `DecVer.parse`

```scala mdoc:reset-object
import just.decver.DecVer

val v = DecVer.parse("1.0")

// To render it to `String`,
v.map(_.render)

// Invalid version
DecVer.parse("a1.0")

// Invalid version
DecVer.parse("a1.0.0")

```

## `DecVer.unsafeParse`

```scala mdoc:reset-object
import just.decver.DecVer

// parse unsafe - NOT RECOMMENDED!!!
val v = DecVer.unsafeParse("1.0")

// to String
v.render
```

```scala mdoc:crash

// Invalid version
DecVer.unsafeParse("a1.0")
```

## DecVer with `pre-release` info
```scala mdoc:reset-object
import just.decver.DecVer

DecVer.parse("1.0-beta1")

val v = DecVer.parse("1.0-3.123.9a")

v.map(_.render)
```

## DecVer with build `meta-info`
```scala mdoc:reset-object
import just.decver.DecVer

val v = DecVer.parse("1.0+100.0.12abc")

v.map(_.render)
```

## DecVer with `pre-release` info and build `meta-info`
```scala mdoc:reset-object
import just.decver.DecVer

DecVer.parse("1.0-beta1")

val v = DecVer.parse("1.0-3.123.9a+100.0.12abc")

v.map(_.render)
```

## Compare `DecVer`
```scala mdoc:reset-object
import just.decver.DecVer

for {
 a <- DecVer.parse("1.0")
 b <- DecVer.parse("1.1")
} yield a < b

for {
 a <- DecVer.parse("1.1")
 b <- DecVer.parse("1.0")
} yield a < b

for {
 a <- DecVer.parse("1.0")
 b <- DecVer.parse("1.1")
} yield a <= b

for {
 a <- DecVer.parse("1.0")
 b <- DecVer.parse("1.0")
} yield a <= b

for {
 a <- DecVer.parse("1.0")
 b <- DecVer.parse("1.0")
} yield a == b

for {
 a <- DecVer.parse("1.1")
 b <- DecVer.parse("1.0")
} yield a > b

for {
 a <- DecVer.parse("1.0")
 b <- DecVer.parse("1.1")
} yield a > b

for {
 a <- DecVer.parse("1.0")
 b <- DecVer.parse("1.1")
} yield a >= b

for {
 a <- DecVer.parse("1.0")
 b <- DecVer.parse("1.0")
} yield a >= b

for {
 a <- DecVer.parse("1.1")
 b <- DecVer.parse("1.0")
} yield a >= b
```

## Matchers
```scala mdoc
DecVer.unsafeParse("1.0").unsafeMatches("1.0 - 2.0")
DecVer.unsafeParse("1.5").unsafeMatches("1.0 - 2.0")
DecVer.unsafeParse("2.0").unsafeMatches("1.0 - 2.0")
DecVer.unsafeParse("0.9").unsafeMatches("1.0 - 2.0")
DecVer.unsafeParse("2.1").unsafeMatches("1.0 - 2.0")

DecVer.unsafeParse("1.0").unsafeMatches(">1.0 <2.0")
DecVer.unsafeParse("1.0").unsafeMatches(">=1.0 <=2.0")
DecVer.unsafeParse("1.5").unsafeMatches(">1.0 <2.0")
DecVer.unsafeParse("2.0").unsafeMatches(">1.0 <2.0")
DecVer.unsafeParse("2.0").unsafeMatches(">=1.0 <=2.0")
DecVer.unsafeParse("0.9").unsafeMatches(">=1.0 <=2.0")
DecVer.unsafeParse("2.1").unsafeMatches(">=1.0 <=2.0")

DecVer.unsafeParse("1.0").unsafeMatches("1.0 - 2.0 || >3.0 <4.0")
DecVer.unsafeParse("2.0").unsafeMatches("1.0 - 2.0 || >3.0 <4.0")
DecVer.unsafeParse("3.0").unsafeMatches("1.0 - 2.0 || >3.0 <=4.0")
DecVer.unsafeParse("3.1").unsafeMatches("1.0 - 2.0 || >3.0 <=4.0")
DecVer.unsafeParse("4.0").unsafeMatches("1.0 - 2.0 || >3.0 <=4.0")
```
