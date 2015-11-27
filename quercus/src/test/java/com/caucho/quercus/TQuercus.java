/*
 * Copyright (c) 1998-2010 Caucho Technology -- all rights reserved
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
package com.caucho.quercus;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.page.QuercusPage;
import com.caucho.quercus.servlet.api.QuercusHttpServletRequestImpl;
import com.caucho.quercus.servlet.api.QuercusHttpServletResponseImpl;
import com.caucho.util.CharBuffer;
import com.caucho.vfs.*;
import net.liftweb.mocks.MockHttpServletRequest;
import net.liftweb.mocks.MockHttpServletResponse;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TQuercus
        extends QuercusContext {

    private static final Logger log = Logger.getLogger(TQuercus.class.getName());

    public TQuercus() {
        super();

        init();
    }

    //
    // command-line main
    //
    public static void mainFile(File file, StreamImpl os, HttpServletRequest request, Map<String, String> ini) throws IOException {
        TQuercus quercus = setup(ini);

        quercus.executeFile(file, os, request);
    }

    public static void mainScript(String script, StreamImpl os, HttpServletRequest request, Map<String, String> ini)
            throws IOException {
        TQuercus quercus = setup(ini);

        quercus.executeScript(script, os, request);
    }

    private static TQuercus setup(Map<String, String> ini) {
        TQuercus quercus = new TQuercus();

        quercus.start();


        for (Map.Entry<String, String> e : ini.entrySet())
            quercus.setIni(e.getKey(), e.getValue());
        return quercus;
    }

    /**
     * Returns the SAPI (Server API) name.
     */
    @Override
    public String getSapiName() {
        return "cli";
    }


    public static String executeScript(String code)
            throws IOException {
        HttpServletRequest request = new MockHttpServletRequest((String) null, "");
        StringWriter out = new StringWriter(new CharBuffer());
        out.openWrite();
        TQuercus quercus = setup(Collections.emptyMap());

        quercus.executeScript(code, out, request);

        return out.getString();

    }

    void executeScript(String code, StreamImpl os, HttpServletRequest request)
            throws IOException {
        Path path = new StringPath(code);
        execute(path, os, request);
    }

    void executeFile(File file, StreamImpl os, HttpServletRequest request)
            throws IOException {
        Path path = new FilePath(file.getPath());
        execute(path, os, request);
    }

    public void execute(Path path, StreamImpl os, HttpServletRequest request)
            throws IOException {
        QuercusPage page = parse(path);

        WriteStream ws = new WriteStream(os);
//        WriteStream ws = new WriteStream(StdoutStream.create());

        ws.setNewlineString("\n");
        ws.setEncoding("iso-8859-1");
        MockHttpServletResponse response = new MockHttpServletResponse(new PrintWriter(new ByteArrayOutputStream()), new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {

            }
        });
        Env env = createEnv(page, ws, new QuercusHttpServletRequestImpl(request), new QuercusHttpServletResponseImpl(response));
        env.start();


        try {
            env.execute();
        } catch (QuercusDieException e) {
            log.log(Level.FINER, e.toString(), e);
        } catch (QuercusExitException e) {
            log.log(Level.FINER, e.toString(), e);
        } catch (QuercusErrorException e) {
            log.log(Level.FINER, e.toString(), e);
        } finally {
//            for (Object header: response.getHeaderNames())
//                System.out.println(header + ": "+response.getHeader((String)header)) ;

            env.close();

            ws.flush();
        }
    }
}
