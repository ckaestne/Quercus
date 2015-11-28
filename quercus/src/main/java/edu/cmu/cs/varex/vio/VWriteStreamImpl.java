package edu.cmu.cs.varex.vio;


import com.caucho.quercus.UnimplementedException;
import com.caucho.vfs.VfsWriteObject;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import edu.cmu.cs.varex.Opt;
import edu.cmu.cs.varex.OptImpl;
import edu.cmu.cs.varex.VWriteStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A variational version of the WriteStream class. Does not do any
 * sophisticated buffering though, might be a little slow therefore.
 */
public class VWriteStreamImpl
        implements VWriteStream {

    private FeatureExpr _currentCtx = FeatureExprFactory.True();
    private StringBuffer _buffer = new StringBuffer();
    private final List<Opt<String>> _output = new ArrayList<>();
    private String newLine = "\n";
    Charset _charset = Charset.defaultCharset();

    public List<Opt<String>> getConditionalOutput() {
        doFlush();
        return Collections.unmodifiableList(_output);
    }

    private void doFlush() {
        if (_buffer.length() > 0) {
            String c = _buffer.toString();
            _buffer = new StringBuffer();
            _output.add(new OptImpl<>(_currentCtx, c));
        }
    }

    @Override
    public void setEncoding(String encoding) throws UnsupportedEncodingException {
        _charset = Charset.forName(encoding);
    }

    @Override
    public void flush() throws IOException {
        doFlush();
    }

    @Override
    public void setNewlineString(String s) {
        newLine = s;
    }

    @Override
    public void close() throws IOException {
        doFlush();
    }

    @Override
    public void setImplicitFlush(boolean flag) {

    }

    @Override
    public void flushBuffer() throws IOException {
        doFlush();

    }

    @Override
    public void free() {

    }

    @Override
    public void setDisableCloseSource(boolean b) {

    }

    @Override
    public PrintWriter getPrintWriter() {
        throw new UnimplementedException();
    }

    @Override
    public void print(FeatureExpr ctx, String v) throws IOException {
        updateCtx(ctx);
        _buffer.append(v);
    }


    @Override
    public void print(FeatureExpr ctx, Object o) throws IOException {
        if (o == null)
            print(ctx, "null");
        else if (o instanceof VfsWriteObject)
            throw new UnimplementedException();
//            ((VfsWriteObject) o).print(ctx, this);
        else
            print(ctx, o.toString());
    }

    @Override
    public void print(FeatureExpr ctx, long v) throws IOException {
        print(ctx, String.valueOf(v));
    }

    @Override
    public void print(FeatureExpr ctx, char[] buffer, int offset, int length) throws IOException {
        updateCtx(ctx);
        _buffer.append(buffer, offset, length);

    }

    @Override
    public void println(FeatureExpr ctx) throws IOException {
        print(ctx, newLine);
    }

    @Override
    public void println(FeatureExpr ctx, String s) throws IOException {
        print(ctx, s + newLine);
    }

    @Override
    public void println(FeatureExpr ctx, Object o) throws IOException {
        if (o == null)
            println(ctx, "null");
        else
            println(ctx, o.toString());

    }

    @Override
    public void write(FeatureExpr ctx, int b) throws IOException {
        updateCtx(ctx);
        _buffer.append(b);
    }

    @Override
    public void write(FeatureExpr ctx, byte[] buffer, int offset, int length) throws IOException {
        updateCtx(ctx);
        _buffer.append(_charset.decode(ByteBuffer.wrap(buffer, offset, length)));
    }

    @Override
    public long writeStream(FeatureExpr ctx, InputStream inputStream) throws IOException {
        throw new UnimplementedException();
//        return 0;
    }


    private void updateCtx(FeatureExpr ctx) throws IOException {
        if (ctx == _currentCtx) return;
        doFlush();
        _currentCtx = ctx;
    }

}
