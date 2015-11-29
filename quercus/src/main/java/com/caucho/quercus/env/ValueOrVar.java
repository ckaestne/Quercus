package com.caucho.quercus.env;

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
}
