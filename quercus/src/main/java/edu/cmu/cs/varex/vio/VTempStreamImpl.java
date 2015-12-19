package edu.cmu.cs.varex.vio;

import com.caucho.vfs.TempBuffer;
import edu.cmu.cs.varex.*;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ckaestne on 12/19/2015.
 */
public class VTempStreamImpl extends VWriteStreamImpl implements VTempStream {
  @Override
  public void destroy() {
  }

  @Override
  public void writeToStream(VWriteStream out) {
    doFlush();
    for (Opt<String> frag: this._output) {

      out.print(frag.getCondition(), frag.getValue());
    }
  }

  @Override
  public void clearWrite() {
    doFlush();
    this._output.clear();
  }

  @Override
  public Iterable<Opt<String>> getContent() {
    return _output;
  }

  @Override
  public V<? extends Integer> getLength() {
    return VList.<Integer,String>foldRight(_output.iterator(), V.one(0), VHelper.True(), (c,s,r) -> V.one(r+s.length()));
  }

//  @Override
//  public int getLength() {
//    throw new UnimplementedVException();
//  }
//
//  @Override
//  public TempBuffer getHead() {
//    throw new UnimplementedVException();
//  }
}
