package just.semver.expr

import just.Common._

/** @author Kevin Lee
  * @since 2022-04-03
  */
sealed trait ComparisonOperator
object ComparisonOperator {
  case object Lt extends ComparisonOperator
  case object Le extends ComparisonOperator
  case object Eql extends ComparisonOperator
  case object Ne extends ComparisonOperator
  case object Gt extends ComparisonOperator
  case object Ge extends ComparisonOperator

  def lt: ComparisonOperator  = Lt
  def le: ComparisonOperator  = Le
  def eql: ComparisonOperator = Eql
  def ne: ComparisonOperator  = Ne
  def gt: ComparisonOperator  = Gt
  def ge: ComparisonOperator  = Ge

  def parse(s: String): Either[String, ComparisonOperator] = s match {
    case "<=" => Right(le)
    case "<" => Right(lt)
    case ">=" => Right(ge)
    case ">" => Right(gt)
    case "!=" => Right(ne)
    case "=" => Right(eql)
    case _ => Left("Unknown or invalid operator")
  }

  implicit class ComparisonOperatorOps(private val operator: ComparisonOperator) extends AnyVal {

    def eval[A: Ordering](a1: A, a2: A): Boolean = {
      val v1 = Ordered.orderingToOrdered(a1)
      val v2 = a2
      operator match {
        case Lt => v1 < v2
        case Le => v1 <= v2
        case Eql => v1.compare(v2) === 0
        case Ne => v1.compare(v2) !== 0
        case Gt => v1 > v2
        case Ge => v1 >= v2
      }
    }

    def render: String = operator match {
      case Lt => "<"
      case Le => "<="
      case Eql => "="
      case Ne => "!="
      case Gt => ">"
      case Ge => ">="
    }
  }
}
