/*
 * Copyright (c) 1998-2006 Caucho Technology -- all rights reserved
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

import java.io.IOException;
import java.util.IdentityHashMap;

import com.caucho.vfs.WriteStream;

import com.caucho.quercus.Quercus;

import com.caucho.quercus.gen.PhpWriter;

import com.caucho.quercus.lib.QuercusStringModule;

/**
 * Represents a PHP string value.
 */
abstract public class AbstractStringValue extends Value {
  protected static final int IS_STRING = 0;
  protected static final int IS_LONG = 1;
  protected static final int IS_DOUBLE = 2;

  /**
   * Pre-increment the following value.
   */
  public Value preincr(int incr)
    throws Throwable
  {
    return postincr(incr);
  }

  /**
   * Post-increment the following value.
   */
  public Value postincr(int incr)
    throws Throwable
  {
    if (incr > 0) {
      String s = toString();
      
      StringBuilder tail = new StringBuilder();

      for (int i = s.length() - 1; i >= 0; i--) {
	char ch = s.charAt(i);

	if (ch == 'z') {
	  if (i == 0)
	    return new StringValue("aa" + tail);
	  else
	    tail.insert(0, 'a');
	}
	else if ('a' <= ch && ch < 'z') {
	  return new StringValue(s.substring(0, i) +
				 (char) (ch + 1) +
				 tail);
	}
	else if (ch == 'Z') {
	  if (i == 0)
	    return new StringValue("AA" + tail.toString());
	  else
	    tail.insert(0, 'A');
	}
	else if ('A' <= ch && ch < 'Z') {
	  return new StringValue(s.substring(0, i) +
				 (char) (ch + 1) +
				 tail);
	}
	else if ('0' <= ch && ch <= '9' && i == s.length() - 1) {
	  return new LongValue(toLong() + 1);
	}
      }

      return new StringValue(tail.toString());
    }
    else if (isLong()) {
      return new LongValue(toLong() - 1);
    }
    else {
      return this;
    }
  }

  /**
   * Returns true for equality
   */
  public boolean eq(Value rValue)
  {
    String v = toString();
    
    rValue = rValue.toValue();

    if (rValue instanceof BooleanValue) {
      if (rValue.toBoolean())
	return ! v.equals("") && ! v.equals("0");
      else
	return v.equals("") || v.equals("0");
    }

    int type = getNumericType();

    if (type == IS_STRING) {
      if (rValue instanceof StringValue)
	return v.equals(rValue.toString());
      else if (rValue.isLong())
	return toLong() ==  rValue.toLong();
      else if (rValue instanceof BooleanValue)
	return toLong() == rValue.toLong();
      else
	return v.equals(rValue.toString());
    }
    else if (rValue.isNumber())
      return toDouble() == rValue.toDouble();
    else
      return toString().equals(rValue.toString());
  }

  /**
   * Converts to a double.
   */
  protected int getNumericType()
  {
    String s = toString();
    int len = s.length();

    if (len == 0)
      return IS_STRING;

    int i = 0;
    int ch = 0;
    boolean hasPoint = false;

    if (i < len && ((ch = s.charAt(i)) == '+' || ch == '-')) {
      i++;
    }

    if (len <= i)
      return IS_STRING;

    ch = s.charAt(i);

    if (ch == '.') {
      for (i++; i < len && '0' <= (ch = s.charAt(i)) && ch <= '9'; i++) {
	return IS_DOUBLE;
      }

      return IS_STRING;
    }
    else if (! ('0' <= ch && ch <= '9'))
      return IS_STRING;

    for (; i < len && '0' <= (ch = s.charAt(i)) && ch <= '9'; i++) {
    }

    if (len <= i)
      return IS_LONG;
    else if (ch == '.' || ch == 'e' || ch == 'E') {
      for (i++;
	   i < len && ('0' <= (ch = s.charAt(i)) && ch <= '9' ||
		       ch == '+' || ch == '-' || ch == 'e' || ch == 'E');
	   i++) {
      }

      if (i < len)
	return IS_STRING;
      else
	return IS_DOUBLE;
    }
    else
      return IS_STRING;
  }

  /**
   * Exports the value.
   */
  public void varExport(StringBuilder sb)
  {
    sb.append("'");

    String value = toString();
    int len = value.length();
    for (int i = 0; i < len; i++) {
      char ch = value.charAt(i);

      switch (ch) {
      case '\'':
	sb.append("\\'");
	break;
      case '\\':
	sb.append("\\\\");
	break;
      default:
	sb.append(ch);
      }
    }
    sb.append("'");
  }

  /**
   * Returns the hash code.
   */
  public int hashCode()
  {
    return toString().hashCode();
  }

  /**
   * Test for equality
   */
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    else if (! (o instanceof AbstractStringValue))
      return false;

    return toString().equals(o.toString());
  }
  
  public void varDumpImpl(Env env,
                          WriteStream out,
                          int depth,
                          IdentityHashMap<Value, String> valueSet)
    throws Throwable
  {
    String s = toString();

    out.print("string(" + s.length() + ") \"" + s + "\"");
  }
}

