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

package com.caucho.quercus.env;

import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;

/**
 * Represents a copy of an object value for serialization/apc
 */
public class CopyObjectExtValue extends ObjectExtValue
{
  private CopyRoot _root;

  public CopyObjectExtValue(Env env, ObjectExtValue copy, CopyRoot root)
  {
    super(env, copy, root);

    _root = root;
  }

  /**
   * Returns the array ref.
   */
  @Override
  public V<? extends Var> getFieldVar(Env env, StringValue name)
  {
    _root.setModified();

    return super.getFieldVar(env, name);
  }

  /**
   * Returns the array ref.
   */
  @Override
  public V<? extends Var> getThisFieldVar(Env env, StringValue name)
  {
    _root.setModified();

    return super.getThisFieldVar(env, name);
  }

  /**
   * Returns the value as an argument which may be a reference.
   */
  @Override
    public V<? extends Var> getFieldArg(Env env, StringValue name, boolean isTop)
  {
    _root.setModified();

    return super.getFieldArg(env, name, isTop);
  }

  /**
   * Returns the value as an argument which may be a reference.
   */
  @Override
  public V<? extends Var> getThisFieldArg(Env env, StringValue name)
  {
    _root.setModified();

    return super.getThisFieldArg(env, name);
  }

  /**
   * Returns the value as an argument which may be a reference.
   */
  @Override
  public V<? extends Var> getFieldArgRef(Env env, StringValue name)
  {
    _root.setModified();

    return super.getFieldArgRef(env, name);
  }

  /**
   * Returns the value as an argument which may be a reference.
   */
  @Override
  public V<? extends Var> getThisFieldArgRef(Env env, StringValue name)
  {
    _root.setModified();

    return super.getThisFieldArgRef(env, name);
  }

  /**
   * Adds a new value.
   */
  @Override
  public V<? extends Value> putField(Env env, FeatureExpr ctx, StringValue name, V<? extends ValueOrVar> value)
  {
    _root.setModified();

    return super.putField(env, ctx, name, value);
  }

  /**
   * Sets/adds field to this object.
   */
  @Override
  public V<? extends Value> putThisField(Env env, FeatureExpr ctx, StringValue name, V<? extends ValueOrVar> value)
  {
    _root.setModified();

    return super.putThisField(env, ctx, name, value);
  }

  protected
  @javax.annotation.Nullable
  V<? extends Value> putFieldExt(Env env, FeatureExpr ctx, StringValue name, V<? extends ValueOrVar> value)
  {
    return null;
  }

  /**
   * Adds a new value to the object.
   */
  @Override
  public void initField(Env env, FeatureExpr ctx, StringValue name,
                        StringValue canonicalName, V<? extends Value> value)
  {
    _root.setModified();

    super.initField(env, ctx, canonicalName, value);
  }

  /**
   * Removes a value.
   */
  @Override
  public void unsetField(FeatureExpr ctx, StringValue name)
  {
    _root.setModified();

    super.unsetField(ctx, name);
  }

  /**
   * Removes the field ref.
   */
  @Override
  public void unsetArray(Env env, FeatureExpr ctx, StringValue name, Value index)
  {
    _root.setModified();

    super.unsetArray(env, ctx, name, index);
  }

  /**
   * Removes the field ref.
   */
  public void unsetThisArray(Env env, FeatureExpr ctx, StringValue name, Value index)
  {
    _root.setModified();

    super.unsetThisArray(env, ctx, name, index);
  }

  /**
   * Sets the array value with the given key.
   */
  @Override
  public Value put(Value key, Value value)
  {
    _root.setModified();

    return super.put(key, value);
  }

  /**
   * Appends a new array value
   */
  @Override
  public V<? extends ValueOrVar> put(FeatureExpr ctx, V<? extends ValueOrVar> value)
  {
    _root.setModified();

    return super.put(ctx, value);
  }

  /**
   * Unsets the array value
   */
  @Override
  public V<? extends Value> remove(FeatureExpr ctx, Value key)
  {
    _root.setModified();

    return super.remove(ctx, key);
  }
}

