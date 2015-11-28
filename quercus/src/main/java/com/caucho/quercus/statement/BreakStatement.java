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

package com.caucho.quercus.statement;

import com.caucho.quercus.Location;
import com.caucho.quercus.env.BreakValue;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.Expr;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

/**
 * Represents a break expression statement in a PHP program.
 */
public class BreakStatement extends Statement {
  protected final Expr _target;
  protected final ArrayList<String> _loopLabelList;
  
  //public static final BreakStatement BREAK = new BreakStatement();
  
  public BreakStatement(Location location,
                        Expr target,
                        ArrayList<String> loopLabelList)
  {
    super(location);
    
    _target = target;
    _loopLabelList = loopLabelList;
  }

  /**
   * Executes the statement, returning the expression value.
   */
  public @NonNull V<? extends Value> execute(Env env, FeatureExpr ctx)
  {
    if (_target == null)
      return VHelper.toV(BreakValue.BREAK);
    else
      return _target.eval(env, VHelper.noCtx()).map((a)->new BreakValue(a.toInt()));
  }
}

