package test;

import fj.F2;
import static fj.test.Arbitrary.arbIntegerBoundaries;
import static fj.test.Bool.bool;
import static fj.test.CheckResult.summary;
import fj.test.Property;
import static fj.test.Property.property;
import static fj.test.Shrink.shrinkInteger;

/*
Adding two positive integers, a and b, results in a positive integer. This is not true, since the
integer value may overflow during the addition resulting in a negative value. This property is
tested with shrinking; note the reasonably small counter-example.
*/
public final class IntegerOverflow {
  public static void main(final String[] args) {
    final Property p = property(arbIntegerBoundaries, arbIntegerBoundaries, shrinkInteger, shrinkInteger,
        new F2<Integer, Integer, Property>() {
      public Property f(final Integer a, final Integer b) {
        return bool(a > 0 && b > 0).implies(a + b > 0);
      }
    });
    summary.println(p.check()); // Falsified after 4 passed tests with arguments: [8,2147483647]
  }
}
