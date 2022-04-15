import just.semver.SemVer
import sbt._
//import wartremover.WartRemover.autoImport.{Wart, Warts}

/** @author Kevin Lee
  * @since 2018-05-21
  */
object ProjectInfo {

  object Wart {
    val Any                     = "org.wartremover.warts.Any"
    val AnyVal                  = "org.wartremover.warts.AnyVal"
    val ArrayEquals             = "org.wartremover.warts.ArrayEquals"
    val AsInstanceOf            = "org.wartremover.warts.AsInstanceOf"
    val DefaultArguments        = "org.wartremover.warts.DefaultArguments"
    val EitherProjectionPartial = "org.wartremover.warts.EitherProjectionPartial"
    val Enumeration             = "org.wartremover.warts.Enumeration"
    val Equals                  = "org.wartremover.warts.Equals"
    val ExplicitImplicitTypes   = "org.wartremover.warts.ExplicitImplicitTypes"
    val FinalCaseClass          = "org.wartremover.warts.FinalCaseClass"
    val FinalVal                = "org.wartremover.warts.FinalVal"
    val GlobalExecutionContext  = "org.wartremover.warts.GlobalExecutionContext"
    val ImplicitConversion      = "org.wartremover.warts.ImplicitConversion"
    val ImplicitParameter       = "org.wartremover.warts.ImplicitParameter"
    val IsInstanceOf            = "org.wartremover.warts.IsInstanceOf"
    val IterableOps             = "org.wartremover.warts.IterableOps"
    val JavaConversions         = "org.wartremover.warts.JavaConversions"
    val JavaSerializable        = "org.wartremover.warts.JavaSerializable"
    val LeakingSealed           = "org.wartremover.warts.LeakingSealed"
    val ListAppend              = "org.wartremover.warts.ListAppend"
    val ListUnapply             = "org.wartremover.warts.ListUnapply"
    val MutableDataStructures   = "org.wartremover.warts.MutableDataStructures"
    val NoNeedImport            = "org.wartremover.warts.NoNeedImport"
    val NonUnitStatements       = "org.wartremover.warts.NonUnitStatements"
    val Nothing                 = "org.wartremover.warts.Nothing"
    val Null                    = "org.wartremover.warts.Null"
    val Option2Iterable         = "org.wartremover.warts.Option2Iterable"
    val OptionPartial           = "org.wartremover.warts.OptionPartial"
    val Overloading             = "org.wartremover.warts.Overloading"
    val PlatformDefault         = "org.wartremover.warts.PlatformDefault"
    val Product                 = "org.wartremover.warts.Product"
    val PublicInference         = "org.wartremover.warts.PublicInference"
    val Recursion               = "org.wartremover.warts.Recursion"
    val RedundantConversions    = "org.wartremover.warts.RedundantConversions"
    val Return                  = "org.wartremover.warts.Return"
    val ScalaApp                = "org.wartremover.warts.ScalaApp"
    val Serializable            = "org.wartremover.warts.Serializable"
    val SizeIs                  = "org.wartremover.warts.SizeIs"
    val StringPlusAny           = "org.wartremover.warts.StringPlusAny"
    val ThreadSleep             = "org.wartremover.warts.ThreadSleep"
    val Throw                   = "org.wartremover.warts.Throw"
    val ToString                = "org.wartremover.warts.ToString"
    val TryPartial              = "org.wartremover.warts.TryPartial"
    val Var                     = "org.wartremover.warts.Var"
    val While                   = "org.wartremover.warts.While"
  }

  val all: List[String] = List(
    Wart.Any,
    Wart.AnyVal,
    Wart.ArrayEquals,
    Wart.AsInstanceOf,
    Wart.DefaultArguments,
    Wart.EitherProjectionPartial,
    Wart.Enumeration,
    Wart.Equals,
    Wart.ExplicitImplicitTypes,
    Wart.FinalCaseClass,
    Wart.FinalVal,
    Wart.GlobalExecutionContext,
    Wart.ImplicitConversion,
    Wart.ImplicitParameter,
    Wart.IsInstanceOf,
    Wart.IterableOps,
    Wart.JavaConversions,
    Wart.JavaSerializable,
    Wart.LeakingSealed,
    Wart.ListAppend,
    Wart.ListUnapply,
    Wart.MutableDataStructures,
    Wart.NoNeedImport,
    Wart.NonUnitStatements,
    Wart.Nothing,
    Wart.Null,
    Wart.Option2Iterable,
    Wart.OptionPartial,
    Wart.Overloading,
    Wart.PlatformDefault,
    Wart.Product,
    Wart.PublicInference,
    Wart.Recursion,
    Wart.RedundantConversions,
    Wart.Return,
    Wart.ScalaApp,
    Wart.Serializable,
    Wart.SizeIs,
    Wart.StringPlusAny,
    Wart.ThreadSleep,
    Wart.Throw,
    Wart.ToString,
    Wart.TryPartial,
    Wart.Var,
    Wart.While
  )

  def allBut(wart: String, warts: String*): List[String] =
    all.diff(wart +: warts)

  def commonWarts(scalaBinaryVersion: String): List[String] =
    SemVer.majorMinorPatch(SemVer.parseUnsafe(scalaBinaryVersion)) match {
      case (SemVer.Major(2), SemVer.Minor(10), _) | (SemVer.Major(3), SemVer.Minor(0), _) =>
        List.empty[String]

      case (SemVer.Major(2), SemVer.Minor(11), _) =>
        allBut(
          Wart.DefaultArguments,
          Wart.Overloading,
          Wart.Any,
          Wart.Nothing,
          Wart.NonUnitStatements,
          Wart.IterableOps,
          Wart.NoNeedImport,
          Wart.Product
        ).map("-P:wartremover:traverser:" + _)

      case (SemVer.Major(2), SemVer.Minor(12) | SemVer.Minor(13), _) | (SemVer.Major(3), _, _) =>
        allBut(Wart.DefaultArguments, Wart.Overloading, Wart.Any, Wart.Nothing, Wart.NonUnitStatements).map(
          "-P:wartremover:traverser:" + _
        )

      case _ =>
        throw new MessageOnlyException(s"Unsupported Scala version: $scalaBinaryVersion")
    }

//  def commonWarts(scalaBinaryVersion: String): Seq[wartremover.Wart] = scalaBinaryVersion match {
//    case "2.10" =>
//      Seq.empty
//    case "2.11" =>
//      Seq.empty
//    case "2.12" | "2.13" | "3" =>
//      Warts.allBut(Wart.DefaultArguments, Wart.Overloading, Wart.Any, Wart.Nothing, Wart.NonUnitStatements)
////    case "3" =>
////      Seq.empty[wartremover.Wart]
//    case _ =>
//      Seq.empty[wartremover.Wart]
//  }

}
