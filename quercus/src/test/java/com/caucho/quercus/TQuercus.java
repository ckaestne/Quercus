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
import com.caucho.vfs.FilePath;
import com.caucho.vfs.Path;
import com.caucho.vfs.StringPath;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.Opt;
import edu.cmu.cs.varex.VWriteStream;
import edu.cmu.cs.varex.vio.VWriteStreamImpl;
import net.liftweb.mocks.MockHttpServletRequest;
import net.liftweb.mocks.MockHttpServletResponse;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TQuercus
        extends QuercusContext {

    private static final Logger log = Logger.getLogger(TQuercus.class.getName());

    public TQuercus() {
        this(new HashMap<>());
    }

    public TQuercus(@Nonnull Map<String, String> ini) {
        super();

        init();

        start();


        for (Map.Entry<String, String> e : ini.entrySet())
            setIni(e.getKey(), e.getValue());
    }

    //
    // command-line main
    //
//    public static void mainFile(File file, StreamImpl os, HttpServletRequest request, Map<String, String> ini) throws IOException {
//        TQuercus quercus = setup(ini);
//
//        quercus.executeFile(file, os, request);
//    }
//
//    public static void mainScript(String script, StreamImpl os, HttpServletRequest request, Map<String, String> ini)
//            throws IOException {
//        TQuercus quercus = setup(ini);
//
//        quercus.executeScript(script, os, request);
//    }



    /**
     * Returns the SAPI (Server API) name.
     */
    @Override
    public String getSapiName() {
        return "cli";
    }


    public static List<Opt<String>> executeScript(String code, FeatureExpr ctx)
            throws IOException {
        HttpServletRequest request = new MockHttpServletRequest((String) null, "");
        VWriteStreamImpl ws = new VWriteStreamImpl();
        TQuercus quercus = new TQuercus(Collections.emptyMap());

        quercus.executeScript(code, ws, request, ctx);

        return ws.getConditionalOutput();
    }

    public void executeScript( @Nonnull String code, @Nonnull VWriteStream os, @Nullable HttpServletRequest request, FeatureExpr ctx)
            throws IOException {
        Path path = new StringPath(code);
        execute(path, os, request, ctx);
    }

    public void executeFile(@Nonnull File file, @Nonnull VWriteStream os, @Nullable HttpServletRequest request, FeatureExpr ctx)
            throws IOException {
        Path path = new FilePath(file.getPath());
        execute(path, os, request, ctx);
    }

    public void execute(@Nonnull Path path, @Nonnull VWriteStream ws, @Nullable HttpServletRequest request, FeatureExpr ctx)
            throws IOException {
        QuercusPage page = parse(path);

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
            env.execute(ctx);
        } catch (QuercusDieException e) {
            System.err.println(e);
            log.log(Level.FINER, e.toString(), e);
        } catch (QuercusExitException e) {
            System.err.println(e);
            log.log(Level.FINER, e.toString(), e);
        } catch (QuercusErrorException e) {
            System.err.println(e);
            log.log(Level.FINER, e.toString(), e);
        } finally {
//            for (Object header: response.getHeaderNames())
//                System.out.println(header + ": "+response.getHeader((String)header)) ;

            env.close();

            ws.flush();
        }
    }
}
