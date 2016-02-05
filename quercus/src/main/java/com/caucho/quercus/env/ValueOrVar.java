package com.caucho.quercus.env;

import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.annotation.VDeprecated;

/**
 * Created by ckaestne on 11/28/2015.
 */
public interface ValueOrVar {
    boolean isVar();
    /** throws an exception if object is not a Value */
    Value _value();
    /** throws an exception if object is not a Var */
    Var _var();

    @Deprecated
    default Value toValue() { return isVar() ? _var().getValue().getOne() : _value(); }

    default Var toVar() { return isVar() ? _var() : _value().toVar(); }
    default V<? extends Var> toLocalVarDeclAsRef() { return isVar() ? V.one(_var()) : _value().toLocalVarDeclAsRef(); }
    default Var toLocalVar() { return isVar() ? new VarImpl(_var().getValue().map(a->a.toLocalValue())) : _value().toLocalVar(); }
    default V<? extends ValueOrVar> toRefValue() { return _getValues(); }
    default V<? extends Value> _getValues() { return isVar() ? _var().getValue() : V.one(_value().toValue()); }
}
