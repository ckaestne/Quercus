package com.caucho.quercus.env;

import edu.cmu.cs.varex.V;

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
    Value toValue();

    default Var toVar() { return isVar() ? _var() : _value().toVar(); }
    default Var toLocalVarDeclAsRef() { return isVar() ? _var() : _value().toLocalVarDeclAsRef(); }
    default Var toLocalVar() { return isVar() ? _var() : _value().toLocalVar(); }
    default V<? extends Value> _getValues() { return isVar() ? _var().getValue() : V.one(_value().toValue()); }
}
