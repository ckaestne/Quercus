package com.caucho.quercus.env;

import de.fosd.typechef.featureexpr.FeatureExpr;

/**
 * An array entry
 *
 * has a condition, a key and a conditional value/var
 */
public interface VEntry {

  EnvVar getValue();

  EnvVar setValue(EnvVar value);

  Value getKey();

  FeatureExpr getCondition();


}
