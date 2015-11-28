package edu.cmu.cs.varex;

/**
 * Created by ckaestne on 11/28/2015.
 */
public interface VNullWriteStream extends VWriteStream {
    static VNullWriteStream create() {
        return null;
    }
}
