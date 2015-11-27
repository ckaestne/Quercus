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

import com.caucho.quercus.env.BooleanValue;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.module.AbstractQuercusModule;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import edu.cmu.cs.varex.Choice;
import edu.cmu.cs.varex.V;

import java.util.logging.Logger;

/**
 * Basic infrastructure for creating conditional values
 */
public class VModule extends AbstractQuercusModule {
    private static final Logger log
            = Logger.getLogger(VModule.class.getName());


    public VModule() {
    }

    public String[] getLoadedExtensions() {
        return new String[]{"v"};
    }

    public <T extends Value> V<T> create_conditional_value(FeatureExpr condition, T value, T elseValue) {
        return new Choice<T>(condition, value, elseValue);
    }

    public V<BooleanValue> create_conditional(StringValue condition) {
        return new Choice<BooleanValue>(FeatureExprFactory.createDefinedExternal(condition.toString()), BooleanValue.TRUE, BooleanValue.FALSE);
    }


}

