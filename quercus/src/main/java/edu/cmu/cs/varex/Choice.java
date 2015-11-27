package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;

/**
 * Created by ckaestne on 11/27/2015.
 */
public class Choice<T> implements V<T> {
    private final FeatureExpr condition;
    private final T a;
    private final T b;

    public Choice(FeatureExpr condition, T a, T b) {
        this.condition = condition;
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return "CHOICE(" + condition.toTextExpr() + " ? " + a + " : " + b + ")";
    }
}
