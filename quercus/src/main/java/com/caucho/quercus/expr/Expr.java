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
import com.caucho.quercus.env.*;
import com.caucho.quercus.parser.QuercusParser;
import com.caucho.quercus.statement.Statement;
import com.caucho.util.L10N;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import javax.annotation.Nonnull;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a PHP expression.
 */
abstract public class Expr {
  private static final L10N L = new L10N(Expr.class);

  public static final int COMPILE_ARG_MAX = 5;
  public static final Expr[] NULL_ARGS = new Expr[0];

  private final Location _location;

  public Expr(Location location)
  {
    _location = location;
  }

  public Expr()
  {
    _location = Location.UNKNOWN;
  }

  /**
   * Returns the location.
   */
  final public Location getLocation()
  {
    return _location;
  }

  /**
   * Returns the filename.
   */
  public String getFileName()
  {
    if (_location != Location.UNKNOWN)
      return _location.getFileName();
    else
      return null;
  }

  /**
   * Returns the line number in the file.
   */
  public int getLine()
  {
    return _location.getLineNumber();
  }

  /**
   * Returns the function name.
   */
  public String getFunctionLocation()
  {
    return "";
  }

  /**
   * Returns the file name and line number, if location is known.
   */
  public String getLocationLine()
  {
    if (_location != Location.UNKNOWN)
      return _location.getFileName() + ":" + getLine() + ": ";
    else
      return "";
  }

  /**
   * Returns true for a reference.
   */
  public boolean isRef()
  {
    return false;
  }

  /**
   * Returns true for a constant expression.
   */
  public boolean isConstant()
  {
    return isLiteral();
  }

  /**
   * Returns true for a literal expression.
   */
  public boolean isLiteral()
  {
    return false;
  }

  /**
   * Returns true if a static true value.
   */
  public boolean isTrue()
  {
    return false;
  }

  /**
   * Returns true if a static false value.
   */
  public boolean isFalse()
  {
    return false;
  }

  /*
   * Returns true if this is an assign expr.
   */
  public boolean isAssign()
  {
    return false;
  }

  /**
   * Returns true for an expression that can be read (only $a[] uses this)
   */
  public boolean canRead()
  {
    return true;
  }

  /**
   * Returns true if the expression evaluates to a boolean.
   */
  public boolean isBoolean()
  {
    return false;
  }

  /**
   * Returns true if the expression evaluates to a long.
   */
  public boolean isLong()
  {
    return false;
  }

  /**
   * Returns true if the expression evaluates to a double.
   */
  public boolean isDouble()
  {
    return false;
  }

  /**
   * Returns true if the expression evaluates to a number.
   */
  public boolean isNumber()
  {
    return isLong() || isDouble();
  }

  /**
   * Returns true if the expression evaluates to a string.
   */
  public boolean isString()
  {
    return false;
  }

  /**
   * Returns true if the expression evaluates to an array.
   */
  public boolean isArray()
  {
    return false;
  }

  /**
   * Returns true if the expression is a var/left-hand-side.
   */
  public boolean isVar()
  {
    return false;
  }

  //
  // expression creation functions
  //

  public Expr createAssign(QuercusParser parser, Expr value)
    throws IOException
  {
    String msg = (L.l("{0} is an invalid left-hand side of an assignment.",
                      this));

    if (parser != null)
      throw parser.error(msg);
    else
      throw new IOException(msg);
  }

  /**
   * Creates an assignment using this value as the right hand side.
   */
  public Expr createAssignFrom(QuercusParser parser,
                               AbstractVarExpr leftHandSide)
  {
    ExprFactory factory = parser.getExprFactory();

    return factory.createAssign(leftHandSide, this);
  }

  /**
   * Mark as an assignment for a list()
   */
  public void assign(QuercusParser parser)
    throws IOException
  {
    String msg = L.l("{0} is an invalid left-hand side of an assignment.",
                     this);

    if (parser != null)
      throw parser.error(msg);
    else
      throw new IOException(msg);
  }

  public Expr createAssignRef(QuercusParser parser, Expr value)
    throws IOException
  {
    // XXX: need real exception
    String msg = L.l("{0} is an invalid left-hand side of an assignment.",
                     this);

    if (parser != null)
      throw parser.error(msg);
    else
      throw new IOException(msg);
  }

  /**
   * Creates a reference.
   * @param location
   */
  public Expr createRef(QuercusParser parser)
    throws IOException
  {
    return this;
  }

  public Expr createDeref(ExprFactory factory)
    throws IOException
  {
    return this;
  }

  /**
   * Creates a assignment
   * @param location
   */
  public Expr createCopy(ExprFactory factory)
  {
    return this;
  }

  /**
   * Copy for things like $a .= "test";
   * @param location
   */
  /*
  public Expr copy()
  {
    return this;
  }
  */

  /**
   * Creates a field ref
   */
  public Expr createFieldGet(ExprFactory factory,
                             Location location,
                             StringValue name)
  {
    return factory.createFieldGet(this, name);
  }

  /**
   * Creates a field ref
   */
  public Expr createFieldGet(ExprFactory factory,
                             Location location,
                             Expr name)
  {
    return factory.createFieldVarGet(this, name);
  }

  //
  // class field refs $class::$bar
  //

  /**
   * Creates a class field $class::foo
   */
  public Expr createClassConst(QuercusParser parser, StringValue name)
  {
    ExprFactory factory = parser.getExprFactory();

    return factory.createClassConst(this, name);
  }

  /**
   * Creates a class field $class::foo
   */
  public Expr createClassConst(QuercusParser parser, Expr name)
  {
    ExprFactory factory = parser.getExprFactory();

    return factory.createClassConst(this, name);
  }

  /**
   * Creates a class field $class::$foo
   */
  public Expr createClassField(QuercusParser parser, StringValue name)
  {
    ExprFactory factory = parser.getExprFactory();

    return factory.createClassField(this, name);
  }

  /**
   * Creates a class field $class::${foo}
   */
  public Expr createClassField(QuercusParser parser, Expr name)
  {
    ExprFactory factory = parser.getExprFactory();

    return factory.createClassField(this, name);
  }

  //
  // unary operations
  //

  /**
   * Creates a assignment
   */
  public Statement createUnset(ExprFactory factory, Location location)
    throws IOException
  {
    throw new IOException(L.l("{0} is an illegal value to unset",
                              this));
  }

  /**
   * Creates an isset expression
   */
  public Expr createIsset(ExprFactory factory)
    throws IOException
  {
    throw new IOException(L.l("{0} is an illegal value to isset",
                              this));
  }

  //
  // function call creation
  //

  /**
   * Creates a function call expression
   */
  public Expr createCall(QuercusParser parser,
                         Location location,
                         ArrayList<Expr> args)
    throws IOException
  {
    ExprFactory factory = parser.getExprFactory();

    return factory.createVarFunction(location, this, args);
  }

  //
  // evaluation
  //

  /**
   * Evaluates the expression as a constant.
   *
   * @return the expression value.
   */
  public Value evalConstant()
  {
    return null;
  }

  /**
   * Evaluates as a constant prefix.
   *
   * @return the expression value as a prefix.
   */
  public Value evalConstantPrefix()
  {
    return evalConstant();
  }

  /**
   * Evaluates as a constant prefix.
   *
   * @return the expression value as a suffix.
   */
  public Value evalConstantSuffix()
  {
    return evalConstant();
  }

  /**
   * Evaluates the expression, returning a Value, never a Var.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  abstract public @Nonnull V<? extends Value> eval(Env env, FeatureExpr ctx);

  /**
   * Evaluates the expression, always returning a variable.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public V<? extends Var> evalVar(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx).map(a->a.toVar());
  }

  /**
   * Evaluates the expression, returning a Value.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public @Nonnull V<? extends Value> evalValue(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx);
  }

  /**
   * Evaluates the expression, returning a Var for variables, and a Value
   * for values.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public V<? extends ValueOrVar> evalRef(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx);
  }

  /**
   * Evaluates the expression as a copy.
   *
   * The default is not to copy because the absence of copying is more
   * testable.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public @Nonnull V<? extends Value> evalCopy(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx);
  }

  /**
   * Evaluates the expression as a function argument where it is unknown
   * if the value will be used as a reference.
   *
   * @param isTail true for the top expression
   *
   * @param env the calling environment.
   * @param ctx
   * @return the expression value.
   */
  public V<? extends ValueOrVar> evalArg(Env env, FeatureExpr ctx, boolean isTop)
  {
    return eval(env, ctx);
  }

  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public @Nonnull V<? extends Value> evalTop(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx);
  }

  /**
   * Evaluates the expression, with the object expected to be modified,
   * e.g. from an unset.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public @Nonnull V<? extends Value> evalDirty(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx);
  }

  /**
   * Evaluates the expression, creating an array for unassigned values.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public @Nonnull V<? extends Value> evalArray(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx);
  }

  /**
   * Evaluates the expression, creating an object for unassigned values.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public @Nonnull V<? extends Value> evalObject(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx);
  }

  /**
   * Evaluates an assignment. The value must not be a Var.
   */
  public @Nonnull V<? extends Value> evalAssignValue(Env env, FeatureExpr ctx, Expr valueExpr)
  {
    V<? extends Value> value = valueExpr.evalCopy(env, ctx);

    return evalAssignValue(env, ctx, value);
  }

  /**
   * Evaluates an assignment. The value must not be a Var.
   */
  public @Nonnull V<? extends Value> evalAssignValue(Env env, FeatureExpr ctx, V<? extends Value> value)
  {
    throw new RuntimeException(L.l(
      "{0} is an invalid left-hand side of an assignment.",
      this));
  }

  /**
   * Evaluates an assignment. If the value is a Var, it replaces the
   * current Var.
   */
  public V<? extends ValueOrVar> evalAssignRef(Env env, FeatureExpr ctx, Expr valueExpr)
  {
    V<? extends ValueOrVar> value = valueExpr.evalRef(env, ctx);

    return evalAssignRef(env, ctx, value);
  }

  /**
   * Evaluates an assignment. If the value is a Var, it replaces the
   * current Var.
   */
  public V<? extends ValueOrVar> evalAssignRef(Env env, FeatureExpr ctx, V<? extends ValueOrVar> value)
  {
    throw new RuntimeException(L.l(
      "{0} is an invalid left-hand side of an assignment.",
      this));
  }

  /**
   * Evaluates as an array index assign ($a[index] = value).
   * @return what was assigned
   */
  public @Nonnull V<? extends Value> evalArrayAssign(Env env, FeatureExpr ctx, Expr indexExpr, Expr valueExpr)
  {
    // php/03mk, php/03mm, php/03mn, php/04b3
    // overrided in ThisFieldExpr and ThisFieldVarExpr
    //Value var = eval(env);
    //
    //return var.put(index, value);

    V<? extends Value> array = evalArray(env, ctx);
    V<? extends Value> index = indexExpr.eval(env, ctx);

    final V<? extends Value> value = valueExpr.evalCopy(env, ctx);

    V<? extends ValueOrVar> result = VHelper.vflatMapAll(ctx, array, index, (c, _array, _index) -> _array.put(c, _index, value));

    //return array.get(index); // php/03mm php/03mn

    return result.map(k->k.toValue());
  }

  /**
   * Evaluates as an array index assign ($a[index] = value).
   * @return what was assigned
   */
  public V<? extends ValueOrVar> evalArrayAssignRef(Env env, FeatureExpr ctx, Expr indexExpr, Expr valueExpr)
  {
    // php/03mk, php/03mm, php/03mn, php/04b3
    // overrided in ThisFieldExpr and ThisFieldVarExpr
    //Value var = eval(env);
    //
    //return var.put(index, value);

    V<? extends Value> array = evalArray(env, ctx);
    V<? extends Value> index = indexExpr.eval(env, ctx);

    V<? extends ValueOrVar> value = valueExpr.evalRef(env, ctx);

    V<? extends ValueOrVar> result = VHelper.mapAll(array, index, value, (_array, _index, _value) -> _array.put(_index, _value));

    //return array.get(index); // php/03mm php/03mn

    return result;
  }

  /**
   * Evaluates as an array index assign ($a[index] = value).
   * @return what was assigned
   */
  public @Nonnull V<? extends ValueOrVar> evalArrayAssignRef(Env env, FeatureExpr ctx, Expr indexExpr, V<? extends ValueOrVar> value)
  {
    // php/03mk, php/03mm, php/03mn, php/04b3
    // overrided in ThisFieldExpr and ThisFieldVarExpr
    //Value var = eval(env);
    //
    //return var.put(index, value);

    V<? extends Value> array = evalArray(env, ctx);
    V<? extends Value> index = indexExpr.eval(env, ctx);

    V<? extends ValueOrVar> result = VHelper.mapAll(array, index, value, (_array, _index, _value) -> _array.put(_index, _value));

    //return array.get(index); // php/03mm php/03mn

    return result;
  }

  /**
   * Evaluates as an array tail assign ($a[] = value).
   * @return what was assigned
   */
  public @Nonnull V<? extends Value> evalArrayAssignTail(Env env, FeatureExpr ctx, V<? extends Value> value)
  {
    V<? extends Value> array = evalArray(env, ctx);
    array.map(a->a.put(VHelper.noCtx(), value));

    return value;
  }

  /**
   * Handles post increments.
   */
  public V<? extends Value> evalPostIncrement(Env env, FeatureExpr ctx, int incr)
  {
    V<? extends Var> var = evalVar(env, ctx);

    return var.flatMap(a->a.postincr(ctx, incr));
  }

  /**
   * Handles post increments.
   */
  public V<? extends Value> evalPreIncrement(Env env, FeatureExpr ctx, int incr)
  {
    V<? extends Var> var = evalVar(env, ctx);

    return var.flatMap(a->a.preincr(ctx, incr));
  }

  /**
   * Evaluates the expression as a string
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public V<? extends String> evalString(Env env, FeatureExpr ctx)
  {
    V<? extends Value> value = eval(env, ctx);

    return value.map(v-> {
      if (v.isObject())
        return v.toString(env).toJavaString();
      else
        return v.toJavaString();
    });
  }

  /**
   * Evaluates the expression as a string value
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public V<? extends StringValue> evalStringValue(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx).map(a->a.toStringValue(env));
  }

  /**
   * Evaluates the expression as a string
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public V<? extends Character> evalChar(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx).map(a->a.toChar());
  }

  /**
   * Evaluates the expression as a boolean.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public V<? extends Boolean> evalBoolean(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx).map(a->a.toBoolean());
  }

  /**
   * Evaluates the expression as a long
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public V<? extends Long> evalLong(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx).map(a->a.toLong());
  }

  /**
   * Evaluates the expression as a double
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public V<? extends Double> evalDouble(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx).map(a->a.toDouble());
  }

  /**
   * Evaluates the expression as an isset() statement.
   */
  public V<? extends Boolean> evalIsset(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx).map(a->a.isset());
  }

  /**
   * Evaluates the expression as an isset() statement.
   */
  public @Nonnull V<? extends Value> evalIssetValue(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx);
  }

  /**
   * Evaluates the expression as an array index unset
   */
  public void evalUnsetArray(Env env, FeatureExpr ctx, Expr indexExpr)
  {
    V<? extends Value> array = evalDirty(env, ctx);
    V<? extends Value> index = indexExpr.eval(env, ctx);

    array.map(a->a.remove(ctx, index.getOne()));
  }

  /**
   * Evaluates as an empty() expression.
   */
  public V<? extends Boolean> evalEmpty(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx).map(a->a.isEmpty());
  }

  /**
   * Evaluates as a QuercusClass.
   */
  public V<? extends QuercusClass> evalQuercusClass(Env env, FeatureExpr ctx)
  {
    V<? extends Value> obj = eval(env, ctx);

    return obj.map(a->a.findQuercusClass(env));
  }

  /**
   * Evaluates arguments
   */
  public static V<? extends ValueOrVar>[] evalArgs(Env env, Expr[] exprs, FeatureExpr ctx)
  {
    ValueOrVar []args = new ValueOrVar[exprs.length];

    for (int i = 0; i < args.length; i++) {
      args[i] = exprs[i].evalArg(env, ctx, true).getOne();
    }

    return VHelper.toVArray(args);
  }

  /**
   * Prints to the output as an echo.
   */
  public void print(Env env, FeatureExpr ctx)
    throws IOException
  {
    eval(env, ctx).foreach((a)->a.print(env, VHelper.noCtx()));
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    else if (! (obj instanceof Expr)) {
      return false;
    }

    Expr expr = (Expr) obj;

    if (! isLiteral() || ! expr.isLiteral()) {
      return false;
    }

    return evalConstant().equals(expr.evalConstant());
  }

  public String toString()
  {
    return "Expr[]";
  }
}

