---
sidebar_position: 1
id: 'intro'
title: 'Just SemVer'
slug: '/'
---
# just-semver

[![Build Status](https://github.com/Kevin-Lee/just-semver/workflows/Build%20All/badge.svg)](https://github.com/Kevin-Lee/just-semver/actions?workflow=Build+All)
[![Release Status](https://github.com/Kevin-Lee/just-semver/workflows/Release/badge.svg)](https://github.com/Kevin-Lee/just-semver/actions?workflow=Release)
[![codecov](https://codecov.io/gh/kevin-lee/just-semver/graph/badge.svg?token=SO5LB2BWOL)](https://codecov.io/gh/kevin-lee/just-semver)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kevinlee/just-semver_2.13/badge.svg)](https://search.maven.org/artifact/io.kevinlee/just-semver_2.13)
[![Latest version](https://index.scala-lang.org/kevin-lee/just-semver/just-semver/latest.svg)](https://index.scala-lang.org/kevin-lee/just-semver/just-semver)

Semantic Versioning (`SemVer`) for Scala

:::info
Supported Scala Versions: @SUPPORTED_SCALA_VERSIONS@.<br/>
It also supports Scala.js and Scala Native.

Show [**all `just-semver` versions**](https://index.scala-lang.org/kevin-lee/just-semver/artifacts)
:::


## Get just-semver

### `@VERSION@`

[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-1.11.0.svg)](https://www.scala-js.org)

Since `0.6.0` `just-semver` supports Scala.js.

#### Core
```scala
"io.kevinlee" %% "just-semver-core" % "@VERSION@"
```

```scala
"io.kevinlee" %%% "just-semver-core" % "@VERSION@"
```


e.g.)
```scala
libraryDependencies += "io.kevinlee" %% "just-semver-core" % "@VERSION@"
```
```scala
libraryDependencies += "io.kevinlee" %%% "just-semver-core" % "@VERSION@"
```


#### DecVer: Decimal version module
```scala
"io.kevinlee" %% "just-semver-decver" % "@VERSION@"
```

```scala
"io.kevinlee" %%% "just-semver-decver" % "@VERSION@"
```


e.g.)
```scala
libraryDependencies += "io.kevinlee" %% "just-semver-decver" % "@VERSION@"
```
```scala
libraryDependencies += "io.kevinlee" %%% "just-semver-decver" % "@VERSION@"
```

#### All modules

```scala
"io.kevinlee" %% "just-semver-core" % "@VERSION@",
"io.kevinlee" %% "just-semver-decver" % "@VERSION@",
```

```scala
"io.kevinlee" %%% "just-semver-core" % "@VERSION@",
"io.kevinlee" %%% "just-semver-decver" % "@VERSION@",
```

***

```scala
libraryDependencies ++= Seq(
  "io.kevinlee" %% "just-semver-core" % "@VERSION@",
  "io.kevinlee" %% "just-semver-decver" % "@VERSION@",
)
```
```scala
libraryDependencies ++= Seq(
  "io.kevinlee" %%% "just-semver-core" % "@VERSION@",
  "io.kevinlee" %%% "just-semver-decver" % "@VERSION@",
)
```



## Older Versions

### `1.1.0`
Please use a higher version.

### `1.0.0`
Please use a higher version.

### `0.13.0`
Please use a higher version.

### `0.12.0`
Please use a higher version.

### `0.11.0`

:::info
Supported Scala Versions: `2.12`, `2.13` and `3.3+`.

Show [**all `just-semver` versions**](https://index.scala-lang.org/kevin-lee/just-semver/artifacts)
:::


#### Get just-semver

```scala
"io.kevinlee" %% "just-semver-core" % "0.11.0"
```

[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-1.18.0.svg)](https://www.scala-js.org)

Since `0.6.0` `just-semver` supports Scala.js.

```scala
"io.kevinlee" %%% "just-semver-core" % "0.11.0"
```


e.g.)
```scala
libraryDependencies += "io.kevinlee" %% "just-semver-core" % "0.11.0"
```
```scala
libraryDependencies += "io.kevinlee" %%% "just-semver-core" % "0.11.0"
```


### `0.10.0`

:::info
Supported Scala Versions: `2.12`, `2.13` and `3.2+`.

Show [**all `just-semver` versions**](https://index.scala-lang.org/kevin-lee/just-semver/artifacts)
:::


#### Get just-semver

```scala
"io.kevinlee" %% "just-semver-core" % "0.10.0"
```

[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-1.11.0.svg)](https://www.scala-js.org)

Since `0.6.0` `just-semver` supports Scala.js.

```scala
"io.kevinlee" %%% "just-semver-core" % "0.10.0"
```


e.g.)
```scala
libraryDependencies += "io.kevinlee" %% "just-semver-core" % "0.10.0"
```
```scala
libraryDependencies += "io.kevinlee" %%% "just-semver-core" % "0.10.0"
```


### `0.9.0`

:::info
Supported Scala Versions: `2.12`, `2.13` and `3.1+`.

Show [**all `just-semver` versions**](https://index.scala-lang.org/kevin-lee/just-semver/artifacts)
:::


#### Get just-semver

```scala
"io.kevinlee" %% "just-semver-core" % "0.9.0"
```

[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-1.11.0.svg)](https://www.scala-js.org)

Since `0.6.0` `just-semver` supports Scala.js.

```scala
"io.kevinlee" %%% "just-semver-core" % "0.9.0"
```


e.g.)
```scala
libraryDependencies += "io.kevinlee" %% "just-semver-core" % "0.9.0"
```
```scala
libraryDependencies += "io.kevinlee" %%% "just-semver-core" % "0.9.0"
```


### `0.6.0`

:::info
Supported Scala Versions: `2.11`, `2.12`, `2.13` and `3`.

Show [**all `just-semver` versions**](https://index.scala-lang.org/kevin-lee/just-semver/artifacts)
:::


#### Get just-semver

```scala
"io.kevinlee" %% "just-semver-core" % "0.6.0"
```

[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-1.11.0.svg)](https://www.scala-js.org)

Since `0.6.0` `just-semver` supports Scala.js.

```scala
"io.kevinlee" %%% "just-semver-core" % "0.6.0"
```


e.g.)
```scala
libraryDependencies += "io.kevinlee" %% "just-semver-core" % "0.6.0"
```
```scala
libraryDependencies += "io.kevinlee" %%% "just-semver-core" % "0.6.0"
```
