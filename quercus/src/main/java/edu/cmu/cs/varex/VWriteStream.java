package edu.cmu.cs.varex;

import com.caucho.vfs.WriteStream;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.vio.PlainWriteStreamAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by ckaestne on 11/28/2015.
 */
public interface VWriteStream {
    static VWriteStream create(VTempStream tempStream) {
        return null;
    }
    static VWriteStream adapt(WriteStream writeStream) {
        return new PlainWriteStreamAdapter(writeStream);
    }

    void setEncoding(String encoding) throws UnsupportedEncodingException;
    void flush() throws IOException;
    void setNewlineString(String s);
    void close() throws IOException;
    void setImplicitFlush(boolean flag);
    void flushBuffer() throws IOException;

    void free();


    void setDisableCloseSource(boolean b);

    PrintWriter getPrintWriter();


    void print(FeatureExpr ctx, String v) throws IOException;

    void print(FeatureExpr ctx, Object v) throws IOException;

    void print(FeatureExpr ctx, long v) throws IOException;
    void print(FeatureExpr ctx, char v) throws IOException;


    void print(FeatureExpr ctx, char[] buffer, int offset, int length) throws IOException;

    void println(FeatureExpr ctx) throws IOException;
    void println(FeatureExpr ctx, String s) throws IOException;
    void println(FeatureExpr ctx, Object v) throws IOException;

    void write(FeatureExpr ctx, int b) throws IOException;
    void write(FeatureExpr ctx, byte[] buffer, int offset, int length) throws IOException;
    long writeStream(FeatureExpr ctx, InputStream inputStream) throws IOException;

}
