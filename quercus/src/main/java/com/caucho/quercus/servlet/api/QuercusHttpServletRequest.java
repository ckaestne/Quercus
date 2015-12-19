/*
 * Copyright (c) 1998-2014 Caucho Technology -- all rights reserved
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
 * @author Nam Nguyen
 */

package com.caucho.quercus.servlet.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;

public interface QuercusHttpServletRequest
{
  String getMethod();
  String getHeader(String name);
  Enumeration getHeaderNames();
  String getParameter(String name);
  String []getParameterValues(String name);
  Map<String,String[]> getParameterMap();
  String getContentType();
  String getCharacterEncoding();

  String getRequestURI();
  String getQueryString();
  QuercusCookie []getCookies();

  String getContextPath();
  String getServletPath();
  String getPathInfo();
  String getRealPath(String path);

  InputStream getInputStream()
    throws IOException;

  QuercusHttpSession getSession(boolean isCreate);

  String getLocalAddr();
  String getServerName();
  int getServerPort();
  String getRemoteHost();
  String getRemoteAddr();
  int getRemotePort();
  String getRemoteUser();

  boolean isSecure();
  String getProtocol();

  Object getAttribute(String name);
  String getIncludeRequestUri();
  String getForwardRequestUri();
  String getIncludeContextPath();
  String getIncludeServletPath();
  String getIncludePathInfo();
  String getIncludeQueryString();

  QuercusRequestDispatcher getRequestDispatcher(String url);

  <T> T toRequest(Class<T> cls);
}
