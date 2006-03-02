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
 * @author Charles Reich
 */

package com.caucho.quercus.lib;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.StringValueImpl;
import com.caucho.quercus.env.Value;

import org.w3c.dom.ProcessingInstruction;

public class DOMProcessingInstruction extends DOMNodeValue {
  
  public DOMProcessingInstruction(ProcessingInstruction pi)
  {
    super(pi);
  }
  
  @Override
  public Value getField(String name)
  {
    if (_node == null)
      return NullValue.NULL;
    
    if ("data".equals(name))
      return new StringValueImpl(((ProcessingInstruction) _node).getData());
    else if ("target".equals(name))
      return new StringValueImpl(((ProcessingInstruction) _node).getTarget());
    
    return NullValue.NULL;
  }
  
  public Value putField(Env env, String key, Value value)
  {
    if (_node == null)
      return NullValue.NULL;
    
    if ("data".equals(key)) {
      ((ProcessingInstruction) _node).setData(value.toString());
      return value;
    }
    
    return NullValue.NULL;
  }
}
