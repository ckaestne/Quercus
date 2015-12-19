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
        throw new UnimplementedVException();
    }
    static VWriteStream adapt(WriteStream writeStream) {
        return new PlainWriteStreamAdapter(writeStream);
    }

    void setEncoding(String encoding) throws UnsupportedEncodingException;
    void flush();
    void setNewlineString(String s);
    void close();
    void setImplicitFlush(boolean flag);
    void flushBuffer();

    void free();


    void setDisableCloseSource(boolean b);

    PrintWriter getPrintWriter();


    void print(FeatureExpr ctx, String v);

    void print(FeatureExpr ctx, Object v);

    void print(FeatureExpr ctx, long v);
    void print(FeatureExpr ctx, char v);


    void print(FeatureExpr ctx, char[] buffer, int offset, int length);

    void println(FeatureExpr ctx);
    void println(FeatureExpr ctx, String s);
    void println(FeatureExpr ctx, Object v);

    void write(FeatureExpr ctx, int b);
    void write(FeatureExpr ctx, byte[] buffer, int offset, int length);
    long writeStream(FeatureExpr ctx, InputStream inputStream);

}
