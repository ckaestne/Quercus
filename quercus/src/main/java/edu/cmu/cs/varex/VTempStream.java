package edu.cmu.cs.varex;

import com.caucho.vfs.TempBuffer;

/**
 * Created by ckaestne on 11/28/2015.
 */
public interface VTempStream {
    void destroy();

    void writeToStream(VWriteStream out);

    void clearWrite();

    static VTempStream create() {
        return null;
    }

    int getLength();

    TempBuffer getHead();
}
