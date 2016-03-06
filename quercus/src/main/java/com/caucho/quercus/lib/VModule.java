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

package com.caucho.quercus.lib;

import com.caucho.quercus.env.*;
import com.caucho.quercus.module.AbstractQuercusModule;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import edu.cmu.cs.varex.annotation.VParamType;
import edu.cmu.cs.varex.annotation.VSideeffectFree;
import edu.cmu.cs.varex.annotation.VVariational;

import javax.annotation.Nonnull;
import java.util.logging.Logger;

/**
 * Basic infrastructure for creating conditional values
 */
public class VModule extends AbstractQuercusModule {
    private static final Logger log
            = Logger.getLogger(VModule.class.getName());


    public VModule() {
    }

    @Override
    public String[] getLoadedExtensions() {
        return new String[]{"v"};
    }

//    public <T extends Value> V<? extends T> create_conditional_value(FeatureExpr condition, T value, T elseValue) {
//        return V.choice(condition, value, elseValue);
//    }

    @VVariational@VParamType(BooleanValue.class)
    public V<? extends BooleanValue> create_conditional(@Nonnull@VParamType(StringValue.class) V<? extends StringValue> condition) {
        //assuming that condition is not variational!
        return V.choice(FeatureExprFactory.createDefinedExternal(condition.getOne().toString()), BooleanValue.TRUE, BooleanValue.FALSE);
    }

    @VSideeffectFree
    public Value vtest_add(Value a, Value b) {
        return LongValue.create(a.toLong() + b.toLong());
    }

    @VVariational@VParamType(Value.class)
    public V<? extends Value> vtest_addandprint(Env env, FeatureExpr ctx, @VParamType(Value.class) V<? extends Value> a, @VParamType(Value.class) V<? extends Value> b) {
        V<? extends LongValue> result = VHelper.smapAll(ctx, a, b, (x, y) -> LongValue.create(x.toLong() + y.toLong()));
        result.sforeach(ctx, (c, v) -> env.print(c, v));
        return result;
    }

    public Value vtest_unlifted(Value a, Value b) {
        return LongValue.create(a.toLong() + b.toLong());
    }

}

