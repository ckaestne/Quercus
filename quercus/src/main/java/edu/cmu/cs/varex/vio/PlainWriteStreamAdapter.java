package edu.cmu.cs.varex.vio;

import com.caucho.vfs.WriteStream;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.VWriteStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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
    public void flush() throws IOException {
        stream.flush();
    }

    @Override
    public void setNewlineString(String s) {
        stream.setNewlineString(s);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void setImplicitFlush(boolean flag) {
        stream.setImplicitFlush(flag);
    }

    @Override
    public void flushBuffer() throws IOException {
        stream.flushBuffer();
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
    public void print(FeatureExpr ctx, String v) throws IOException {
        updateCtx(ctx);
        stream.print(v);
    }


    @Override
    public void print(FeatureExpr ctx, Object v) throws IOException {
        updateCtx(ctx);
        stream.print(v);
    }

    @Override
    public void print(FeatureExpr ctx, long v) throws IOException {
        updateCtx(ctx);
        stream.print(v);
    }

    @Override
    public void print(FeatureExpr ctx, char v) throws IOException {
        updateCtx(ctx);
        stream.print(v);
    }

    @Override
    public void print(FeatureExpr ctx, char[] buffer, int offset, int length) throws IOException {
        updateCtx(ctx);
        stream.print(buffer, offset, length);
    }

    @Override
    public void println(FeatureExpr ctx) throws IOException {
        updateCtx(ctx);
        stream.println();
    }

    @Override
    public void println(FeatureExpr ctx, String s) throws IOException {
        updateCtx(ctx);
        stream.println(s);
    }

    @Override
    public void println(FeatureExpr ctx, Object v) throws IOException {
        updateCtx(ctx);
        stream.println(v);
    }

    @Override
    public void write(FeatureExpr ctx, int b) throws IOException {
        updateCtx(ctx);
        stream.write(b);
    }

    @Override
    public void write(FeatureExpr ctx, byte[] buffer, int offset, int length) throws IOException {
        updateCtx(ctx);
        stream.write(buffer, offset, length);
    }

    @Override
    public long writeStream(FeatureExpr ctx, InputStream inputStream) throws IOException {
        updateCtx(ctx);
        return stream.writeStream(inputStream);
    }

    private void updateCtx(FeatureExpr ctx) throws IOException {

        if (ctx == lastCtx) return;
        if (lastCtx == null && ctx.isTautology()) {
            lastCtx = ctx;
            return;
        }
        stream.println("\n#condition " + ctx.toTextExpr() + "\n");
    }
}
