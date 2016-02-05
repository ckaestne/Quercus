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
import com.caucho.quercus.env.*;
import com.caucho.quercus.expr.AbstractVarExpr;
import com.caucho.quercus.expr.Expr;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.Opt;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import edu.cmu.cs.varex.VList;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a foreach statement.
 */
public class ForeachStatement
  extends Statement
{
  protected final Expr _objExpr;

  protected final AbstractVarExpr _key;

  protected final AbstractVarExpr _value;
  protected final boolean _isRef;

  protected final Statement _block;

  protected final String _label;

  public ForeachStatement(Location location,
                          Expr objExpr,
                          AbstractVarExpr key,
                          AbstractVarExpr value,
                          boolean isRef,
                          Statement block,
                          String label)
  {
    super(location);

    _objExpr = objExpr;

    _key = key;
    _value = value;
    _isRef = isRef;

    _block = block;
    _label = label;

    block.setParent(this);
  }

  @Override
  public boolean isLoop()
  {
    return true;
  }

  private class MergedIterator implements Iterator<VEntry> {
    private final Iterator<Opt<Iterator<VEntry>>> ititerator;
    private Opt<Iterator<VEntry>> thisIterator;

    public MergedIterator(List<Opt<Iterator<VEntry>>> iterators) {
      this.ititerator = iterators.iterator();
      if (ititerator.hasNext())
        thisIterator = ititerator.next();
      else thisIterator = null;
    }

    @Override
    public boolean hasNext() {
      if (thisIterator == null) return false;
      if (thisIterator.getValue().hasNext()) return true;
      if (ititerator.hasNext())
        thisIterator = ititerator.next();
      else thisIterator = null;
      return hasNext();
    }

    @Override
    public VEntry next() {
      assert hasNext();
      final VEntry next = thisIterator.getValue().next();
      final FeatureExpr iteratorCondition = thisIterator.getCondition();
      return new VEntry() {
        @Override
        public EnvVar getEnvVar() {
          return next.getEnvVar();
        }

        @Override
        public EnvVar setEnvVar(EnvVar value) {
          return next.setEnvVar(value);
        }

        @Override
        public Value getKey() {
          return next.getKey();
        }

        @Override
        public FeatureExpr getCondition() {
          return next.getCondition().and(iteratorCondition);
        }
      };
    }
  }

  private Iterator<VEntry> getMergedIterator(Env env, V<? extends Value> obj) {
    final List<Opt<Iterator<VEntry>>> iterators = VList.flatten(obj.map((a) -> a.getIterator(env)));
    return new MergedIterator(iterators);
  }

  @Override
  public
  @Nonnull
  V<? extends ValueOrVar> execute(Env env, FeatureExpr ctx) {
    V<? extends Value> origObj = _objExpr.eval(env, ctx);
    V<? extends Value> obj = origObj.map((a) -> a.copy()); // php/0669
    Iterator<VEntry> iter = getMergedIterator(env, obj);


    V<? extends ValueOrVar> forEachResult = V.one(null);

    while (iter.hasNext() && ctx.isSatisfiable()) {
      VEntry entry = iter.next();
      Value key = entry.getKey();
      FeatureExpr innerCtx = ctx.and(entry.getCondition());
      EnvVar value;
      if (!_isRef)
        value = entry.getEnvVar();
      else value = origObj.getOne(innerCtx).getVar(key);

      if (_key != null)
        _key.evalAssignValue(env, innerCtx, VHelper.toV(key));

      if (!_isRef)
        _value.evalAssignValue(env, innerCtx, value.copy().getValue());
      else
        _value.evalAssignRef(env, innerCtx, value.getVar());

      V<? extends ValueOrVar> result = _block.execute(env, innerCtx);

      forEachResult = forEachResult.<ValueOrVar>vflatMap(innerCtx, (oc, fer) -> fer != null ? V.one(fer) :
              result.<ValueOrVar>vflatMap(oc, (c, r) -> V.choice(c, evalReturn(r), fer)));
    }


    return forEachResult;
  }

  private ValueOrVar evalReturn(ValueOrVar r) {
    if (r == null) return r;
    else if (r instanceof ContinueValue) {
      ContinueValue conValue = (ContinueValue) r;

      int target = conValue.getTarget();

      if (target > 1)
        return new ContinueValue(target - 1);
      else
        return null;
    } else if (r instanceof BreakValue) {
      BreakValue breakValue = (BreakValue) r;

      int target = breakValue.getTarget();

      if (target > 1)
        return new BreakValue(target - 1);
      else
        return null;
    } else
      return r;
  }
}

