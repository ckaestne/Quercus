package edu.cmu.cs.varex;

import edu.cmu.cs.varex.vio.VTempStreamImpl;

import java.util.Iterator;

/**
 * Created by ckaestne on 11/28/2015.
 */
public interface VTempStream {
    void destroy();

    void writeToStream(VWriteStream out);

    void clearWrite();

    static VTempStream create() {
        return new VTempStreamImpl();
    }

    Iterable<Opt<String>> getContent();

    V<? extends Integer> getLength();

}
