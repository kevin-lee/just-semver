package just.semver

import just.semver.SemVer.{Major, Minor}

/**
 * @author Kevin Lee
 * @since 2018-10-21
 */
trait SequenceBasedVersion[T] extends Ordered[T] {
  def major: Major
  def minor: Minor
  def render: String
}
