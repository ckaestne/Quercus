package edu.cmu.cs.varex;

import com.caucho.vfs.WriteStream;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.vio.PlainWriteStreamAdapter;
import edu.cmu.cs.varex.vio.VTempStreamImpl;
import edu.cmu.cs.varex.vio.VWriteStreamImpl;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;


public interface VWriteStream {
    static VWriteStream create(VTempStream tempStream) {
        assert tempStream instanceof VTempStreamImpl;
        return (VWriteStream) tempStream;
    }
    static VWriteStream adapt(WriteStream writeStream) {
        return new PlainWriteStreamAdapter(writeStream);
    }

    void setEncoding(String encoding) throws UnsupportedEncodingException;

    Charset getEncoding();
    void flush();
    void setNewlineString(String s);

    String getNewlineString();
    void close();
    void setImplicitFlush(boolean flag);
    void flushBuffer();

    void free();


    void setDisableCloseSource(boolean b);

    PrintWriter getPrintWriter();


    void print(FeatureExpr ctx, String v);

    void print(FeatureExpr ctx, Object v);


    void print(FeatureExpr ctx, char[] buffer, int offset, int length);

    void write(FeatureExpr ctx, int b);
    long writeStream(FeatureExpr ctx, InputStream inputStream);


    default void print(FeatureExpr ctx, long v) {
        print(ctx, String.valueOf(v));
    }

    default void print(FeatureExpr ctx, char v) {
        print(ctx, Character.toString(v));
    }

    default void println(FeatureExpr ctx) {
        print(ctx, getNewlineString());
    }

    default void println(FeatureExpr ctx, String s) {
        print(ctx, s + getNewlineString());
    }

    default void println(FeatureExpr ctx, Object o) {
        if (o == null)
            println(ctx, "null");
        else
            println(ctx, o.toString());

    }

    default void write(FeatureExpr ctx, byte[] buffer, int offset, int length) {
        print(ctx, getEncoding().decode(ByteBuffer.wrap(buffer, offset, length)));
    }
}
