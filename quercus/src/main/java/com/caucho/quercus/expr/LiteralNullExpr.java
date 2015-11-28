/*
 * Copyright (c) 1998-2012 Caucho Technology -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Resin Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resin Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.quercus.expr;

import com.caucho.quercus.Location;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.Value;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;

/**
 * Represents a PHP null expression.
 */
public class LiteralNullExpr extends Expr {
  public static final LiteralNullExpr NULL
    = new LiteralNullExpr(Location.UNKNOWN);

  private LiteralNullExpr(Location location)
  {
    super(location);
  }

  protected LiteralNullExpr()
  {
  }

  /**
   * Evaluates the expression as a constant.
   *
   * @return the expression value.
   */
  public Value evalConstant()
  {
    return NullValue.NULL;
  }

  /**
   * Returns true for a literal expression.
   */
  public boolean isLiteral()
  {
    return true;
  }

  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public V<? extends Value> eval(Env env, FeatureExpr ctx)
  {
    return VHelper.toV(NullValue.NULL);
  }

  //
  // Java code generation
  //

  public String toString()
  {
    return "null";
  }
}

