package just.semver

import hedgehog.{Gen, Range}

/**
 * @author Kevin Lee
 * @since 2024-07-27
 */
object CommonGens {

  def genVersionNumberWithRange(range: Range[Int]): Gen[Int] =
    Gen.int(range)

}
