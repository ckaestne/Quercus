package edu.cmu.cs.varex.vio;

import com.caucho.vfs.WriteStream;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.VWriteStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * adapter for nonvariational writers
 *
 * will produce #condition whenever the output condition changes
 */
public class PlainWriteStreamAdapter implements VWriteStream {

    private final WriteStream stream;

    private FeatureExpr lastCtx = null;

    public PlainWriteStreamAdapter(WriteStream plainStream) {
        this.stream = plainStream;
    }

    @Override
    public void setEncoding(String encoding) throws UnsupportedEncodingException {
        stream.setEncoding(encoding);
    }

    @Override
    public Charset getEncoding() {
        return null;
    }

    @Override
    public void flush()  {
        try {
            stream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setNewlineString(String s) {
        stream.setNewlineString(s);
    }

    @Override
    public String getNewlineString() {
        return stream.getNewlineString();
    }

    @Override
    public void close()  {
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setImplicitFlush(boolean flag) {
        stream.setImplicitFlush(flag);
    }

    @Override
    public void flushBuffer()  {
        try {
            stream.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    public void free() {
        stream.free();
    }

    @Override
    public void setDisableCloseSource(boolean b) {
        stream.setDisableCloseSource(b);
    }

    @Override
    public PrintWriter getPrintWriter() {
        return stream.getPrintWriter();
    }

    @Override
    public void print(FeatureExpr ctx, String v)  {
        updateCtx(ctx);
        try {
            stream.print(v);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void print(FeatureExpr ctx, Object v)  {
        updateCtx(ctx);
        try {
            stream.print(v);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void print(FeatureExpr ctx, long v)  {
        updateCtx(ctx);
        try {
            stream.print(v);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void print(FeatureExpr ctx, char v)  {
        updateCtx(ctx);
        try {
            stream.print(v);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void print(FeatureExpr ctx, char[] buffer, int offset, int length)  {
        updateCtx(ctx);
        try {
            stream.print(buffer, offset, length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void println(FeatureExpr ctx)  {
        updateCtx(ctx);
        try {
            stream.println();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void println(FeatureExpr ctx, String s)  {
        updateCtx(ctx);
        try {
            stream.println(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void println(FeatureExpr ctx, Object v)  {
        updateCtx(ctx);
        try {
            stream.println(v);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(FeatureExpr ctx, int b)  {
        updateCtx(ctx);
        try {
            stream.write(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(FeatureExpr ctx, byte[] buffer, int offset, int length)  {
        updateCtx(ctx);
        try {
            stream.write(buffer, offset, length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long writeStream(FeatureExpr ctx, InputStream inputStream)  {
        updateCtx(ctx);
        try {
            return stream.writeStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateCtx(FeatureExpr ctx)  {

        if (ctx == lastCtx) return;
        if (lastCtx == null && ctx.isTautology()) {
            lastCtx = ctx;
            return;
        }
        try {
            stream.println("\n#condition " + ctx.toTextExpr() + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
