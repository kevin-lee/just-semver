package just.semver.expr

import just.Common._

/** @author Kevin Lee
  * @since 2022-04-03
  */
enum ComparisonOperator derives CanEqual {
  case Lt
  case Le
  case Eql
  case Ne
  case Gt
  case Ge
}
object ComparisonOperator {
  def lt: ComparisonOperator  = ComparisonOperator.Lt
  def le: ComparisonOperator  = ComparisonOperator.Le
  def eql: ComparisonOperator = ComparisonOperator.Eql
  def ne: ComparisonOperator  = ComparisonOperator.Ne
  def gt: ComparisonOperator  = ComparisonOperator.Gt
  def ge: ComparisonOperator  = ComparisonOperator.Ge

  def parse(s: String): Either[String, ComparisonOperator] = s match {
    case "<=" => Right(le)
    case "<" => Right(lt)
    case ">=" => Right(ge)
    case ">" => Right(gt)
    case "!=" => Right(ne)
    case "=" => Right(eql)
    case _ => Left("Unknown or invalid operator")
  }

  extension (operator: ComparisonOperator) {

    def eval[A: Ordering](a1: A, a2: A): Boolean = {
      val v1 = Ordered.orderingToOrdered(a1)
      val v2 = a2
      operator match {
        case ComparisonOperator.Lt => v1 < v2
        case ComparisonOperator.Le => v1 <= v2
        case ComparisonOperator.Eql => v1.compare(v2) === 0
        case ComparisonOperator.Ne => v1.compare(v2) !== 0
        case ComparisonOperator.Gt => v1 > v2
        case ComparisonOperator.Ge => v1 >= v2
      }
    }

    def render: String = operator match {
      case ComparisonOperator.Lt => "<"
      case ComparisonOperator.Le => "<="
      case ComparisonOperator.Eql => "="
      case ComparisonOperator.Ne => "!="
      case ComparisonOperator.Gt => ">"
      case ComparisonOperator.Ge => ">="
    }
  }
}
