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

package com.caucho.quercus.program;

import com.caucho.quercus.Location;
import com.caucho.quercus.QuercusException;
import com.caucho.quercus.env.*;
import com.caucho.quercus.expr.Expr;
import com.caucho.quercus.expr.ExprFactory;
import com.caucho.quercus.expr.ParamRequiredExpr;
import com.caucho.quercus.function.AbstractFunction;
import com.caucho.quercus.statement.Statement;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents sequence of statements.
 */
@SuppressWarnings("serial")
public class Function extends AbstractFunction {
  protected final FunctionInfo _info;
  protected final boolean _isReturnsReference;

  protected final String _name;
  protected final Arg []_args;
  protected final Statement _statement;

  protected boolean _hasReturn;

  protected String _comment;

  protected Arg []_closureUseArgs;

  public Function(ExprFactory exprFactory,
                  Location location,
                  String name,
                  FunctionInfo info,
                  Arg []args,
                  Statement []statements)
  {
    super(location);

    _name = name.intern();
    _info = info;
    _info.setFunction(this);
    _isReturnsReference = info.isReturnsReference();

    _args = new Arg[args.length];

    System.arraycopy(args, 0, _args, 0, args.length);

    _statement = exprFactory.createBlock(location, statements);

    setGlobal(info.isPageStatic());
    setClosure(info.isClosure());

    _isStatic = true;
  }

  /**
   * Returns the name.
   */
  public String getName()
  {
    return _name;
  }

  /**
   * Returns the declaring class
   */
  @Override
  public ClassDef getDeclaringClass()
  {
    return _info.getDeclaringClass();
  }

  public FunctionInfo getInfo()
  {
    return _info;
  }

  protected boolean isMethod()
  {
    return getDeclaringClassName() != null;
  }

  /**
   * Returns the declaring class name
   */
  @Override
  public String getDeclaringClassName()
  {
    ClassDef declaringClass = _info.getDeclaringClass();

    if (declaringClass != null)
      return declaringClass.getName();
    else
      return null;
  }

  /**
   * Returns the args.
   */
  @Override
  public Arg []getArgs(Env env)
  {
    return _args;
  }

  /**
   * Returns the args.
   */
  @Override
  public Arg []getClosureUseArgs()
  {
    return _closureUseArgs;
  }

  /**
   * Returns the args.
   */
  @Override
  public void setClosureUseArgs(Arg []useArgs)
  {
    _closureUseArgs = useArgs;
  }

  public boolean isObjectMethod()
  {
    return false;
  }

  /**
   * True for a returns reference.
   */
  @Override
  public boolean isReturnsReference(Env env)
  {
    return _isReturnsReference;
  }

  /**
   * Sets the documentation for this function.
   */
  public void setComment(String comment)
  {
    _comment = comment;
  }

  /**
   * Returns the documentation for this function.
   */
  @Override
  public String getComment()
  {
    return _comment;
  }

  public Value execute(Env env)
  {
    return null;
  }

  /**
   * Evaluates a function's argument, handling ref vs non-ref
   */
  @Override
  public Value []evalArguments(Env env, Expr fun, Expr []args)
  {
    Value []values = new Value[args.length];

    for (int i = 0; i < args.length; i++) {
      Arg arg = null;

      if (i < _args.length)
        arg = _args[i];

      if (arg == null)
        values[i] = args[i].eval(env, VHelper.noCtx()).getOne().copy();
      else if (arg.isReference())
        values[i] = args[i].evalVar(env, VHelper.noCtx()).getOne().makeValue();
      else {
        // php/0d04
        values[i] = args[i].eval(env, VHelper.noCtx()).getOne();
      }
    }

    return values;
  }

  public @NonNull V<? extends Value> call(Env env, FeatureExpr ctx, Expr []args)
  {
    return callImpl(env, ctx, args, false);
  }

  public @NonNull V<? extends Value> callCopy(Env env, FeatureExpr ctx, Expr []args)
  {
    return callImpl(env, ctx, args, false);
  }

  public @NonNull V<? extends Value> callRef(Env env,FeatureExpr ctx,  Expr []args)
  {
    return callImpl(env, ctx, args, true);
  }

  private V<? extends Value> callImpl(Env env, FeatureExpr ctx, Expr[] args, boolean isRef)
  {
    HashMap<StringValue,EnvVar> map = new HashMap<StringValue,EnvVar>();

    V<? extends ValueOrVar> []values = new V[args.length];

    for (int i = 0; i < args.length; i++) {
      Arg arg = null;

      if (i < _args.length) {
        arg = _args[i];
      }

      if (arg == null) {
        values[i] = args[i].eval(env, ctx).map((a)->a.copy());
      }
      else if (arg.isReference()) {
        values[i] = args[i].evalVar(env, ctx);

        map.put(arg.getName(), new EnvVarImpl(values[i].map((a)->a.toLocalVarDeclAsRef())));
      }
      else {
        // php/0d04
        values[i] = args[i].eval(env, ctx);

        V<? extends Var> var = values[i].map((a)->a.toVar());

        map.put(arg.getName(), new EnvVarImpl(var));

        values[i] = var.flatMap((a)->a._getValues());
      }
    }

    for (int i = args.length; i < _args.length; i++) {
      Arg arg = _args[i];

      Expr defaultExpr = arg.getDefault();

      if (defaultExpr == null)
        return VHelper.toV(env.error("expected default expression"));
      else if (arg.isReference())
        map.put(arg.getName(),
                new EnvVarImpl(defaultExpr.evalVar(env, ctx)));
      else {
        map.put(arg.getName(),
                new EnvVarImpl(defaultExpr.eval(env, ctx).map((a)->a.copy().toVar())));
      }
    }

    Map<StringValue,EnvVar> oldMap = env.pushEnv(map);
    V<? extends ValueOrVar> []oldArgs = env.setFunctionArgs(values); // php/0476
    Value oldThis;

    if (isStatic()) {
      // php/0967
      oldThis = env.setThis(env.getCallingClass());
    }
    else
      oldThis = env.getThis();

    try {
      V<? extends Value> value = _statement.execute(env, ctx);

      if (value != null)
        return value;
//      else if (_info.isReturnsReference())      //TODO V not supported
//        return VHelper.toV(new Var());
      else
        return VHelper.toV(NullValue.NULL);
      /*
      else if (_isReturnsReference && isRef)
        return value;
      else
        return value.copyReturn();
        */
    } finally {
      env.restoreFunctionArgs(oldArgs);
      env.popEnv(oldMap);
      env.setThis(oldThis);
    }
  }

  @Override
  public V<? extends Value> call(Env env, FeatureExpr ctx, V<? extends ValueOrVar>[] args)
  {
    return callImpl(env, ctx, args, false, null, null);
  }

  @Override
  public V<? extends Value> callCopy(Env env, FeatureExpr ctx, V<? extends ValueOrVar>[] args)
  {
    return callImpl(env, ctx, args, false, null, null).map((a)->a.copy());
  }

  @Override
  public V<? extends Value> callRef(Env env, FeatureExpr ctx, V<? extends ValueOrVar>[] args)
  {
    return callImpl(env, ctx, args, true, null, null);
  }

  @Override
  public V<? extends Value> callClosure(Env env, FeatureExpr ctx, V<? extends ValueOrVar>[] args, V<? extends ValueOrVar>[] useArgs)
  {
    return callImpl(env, ctx, args, false, getClosureUseArgs(), useArgs).map((a)->a.copy());
  }

  public V<? extends Value> callImpl(Env env, FeatureExpr ctx, V<? extends ValueOrVar>[] args, boolean isRef,
                                     Arg []useParams, V<? extends ValueOrVar>[] useArgs)
  {
    HashMap<StringValue,EnvVar> map = new HashMap<StringValue,EnvVar>(8);

    if (useParams != null) {
      for (int i = 0; i < useParams.length; i++) {
        map.put(useParams[i].getName(), new EnvVarImpl(useArgs[i].map((a)->a.toVar())));
      }
    }

    for (int i = 0; i < args.length; i++) {
      Arg arg = null;

      if (i < _args.length) {
        arg = _args[i];
      }

      if (arg == null) {
      }
      else if (arg.isReference()) {
        map.put(arg.getName(), new EnvVarImpl(args[i].map((a)->a.toLocalVarDeclAsRef())));
      }
      else {
        // XXX: php/1708, toVar() may be doing another copy()
        V<? extends Var> var = args[i].map((a)->a.toLocalVar());

        if (arg.getExpectedClass() != null
            && arg.getDefault() instanceof ParamRequiredExpr) {
          env.checkTypeHint(var.flatMap((a)->a._getValues()),
                            arg.getExpectedClass(),
                            arg.getName().toString(),
                            getName());
        }

        // quercus/0d04
        map.put(arg.getName(), new EnvVarImpl(var));
      }
    }

    for (int i = args.length; i < _args.length; i++) {
      Arg arg = _args[i];

      Expr defaultExpr = arg.getDefault();

      try {
        if (defaultExpr == null)
          return VHelper.toV(env.error("expected default expression"));
        else if (arg.isReference())
          map.put(arg.getName(), new EnvVarImpl(defaultExpr.evalVar(env, VHelper.noCtx())));
        else {
          map.put(arg.getName(), new EnvVarImpl(V.one(defaultExpr.eval(env, VHelper.noCtx()).getOne().toLocalVar())));
        }
      } catch (Exception e) {
        throw new QuercusException(getName() + ":arg(" + arg.getName() + ") "
                                   + e.getMessage(), e);
      }
    }

    Map<StringValue,EnvVar> oldMap = env.pushEnv(map);
    V<? extends ValueOrVar> []oldArgs = env.setFunctionArgs(args);
    Value oldThis;

    if (_info.isMethod()) {
      oldThis = env.getThis();
    }
    else {
      // php/0967, php/091i
      oldThis = env.setThis(NullThisValue.NULL);
    }

    try {
      V<? extends Value> value = _statement.execute(env, ctx);

      return value.map(v-> {
        if (v == null) {
//        if (_isReturnsReference) //TODO V not supported
//          return VHelper.toV(new Var());
//        else
          return NullValue.NULL;
        } else if (_isReturnsReference)
          return v;
        else
          return v.toValue().copy();
      });
    } finally {
      env.restoreFunctionArgs(oldArgs);
      env.popEnv(oldMap);
      env.setThis(oldThis);
    }
  }

  //
  // method
  //

  @Override
  public V<? extends Value> callMethod(Env env, FeatureExpr ctx,
                                       QuercusClass qClass,
                                       Value qThis,
                                       V<? extends ValueOrVar>[] args)
  {
    if (isStatic())
      qThis = qClass;

    Value oldThis = env.setThis(qThis);
    QuercusClass oldClass = env.setCallingClass(qClass);

    try {
      return callImpl(env, ctx, args, false, null, null);
    } finally {
      env.setThis(oldThis);
      env.setCallingClass(oldClass);
    }
  }

  @Override
  public V<? extends Value> callMethodRef(Env env, FeatureExpr ctx,
                                          QuercusClass qClass,
                                          Value qThis,
                                          V<? extends ValueOrVar>[] args)
  {
    Value oldThis = env.setThis(qThis);
    QuercusClass oldClass = env.setCallingClass(qClass);

    try {
      return callImpl(env, ctx, args, true, null, null);
    } finally {
      env.setThis(oldThis);
      env.setCallingClass(oldClass);
    }
  }


  private boolean isVariableArgs()
  {
    return _info.isVariableArgs() || _args.length > 5;
  }

  private boolean isVariableMap()
  {
    // return _info.isVariableVar();
    // php/3254
    return _info.isUsesSymbolTable() || _info.isVariableVar();
  }

  public String toString()
  {
    return getClass().getSimpleName() + "[" + _name + "]";
  }
}

