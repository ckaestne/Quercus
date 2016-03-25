/*
 * Copyright (c) 1998-2012 Caucho Technology -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Resin Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resin Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Emil Ong
 */

package com.caucho.quercus.lib.file;

import com.caucho.quercus.QuercusModuleException;
import com.caucho.quercus.env.*;
import com.caucho.quercus.function.AbstractFunction;
import com.caucho.vfs.TempBuffer;
import edu.cmu.cs.varex.VHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * A stream that has its operations mediated by a Quercus object.
 */
public class WrappedStream implements BinaryInput, BinaryOutput {
  private static final ConstStringValue STREAM_CLOSE
    = new ConstStringValue("stream_close");
  private static final ConstStringValue STREAM_EOF
    = new ConstStringValue("stream_eof");
  private static final ConstStringValue STREAM_FLUSH
    = new ConstStringValue("stream_flush");
  private static final ConstStringValue STREAM_OPEN
    = new ConstStringValue("stream_open");
  private static final ConstStringValue STREAM_READ
    = new ConstStringValue("stream_read");
  private static final ConstStringValue STREAM_SEEK
    = new ConstStringValue("stream_seek");
  private static final ConstStringValue STREAM_TELL
    = new ConstStringValue("stream_tell");
  private static final ConstStringValue STREAM_WRITE
    = new ConstStringValue("stream_write");

  private static final UnicodeBuilderValue STREAM_CLOSE_U
    = new UnicodeBuilderValue("stream_close");
  private static final UnicodeBuilderValue STREAM_EOF_U
    = new UnicodeBuilderValue("stream_eof");
  private static final UnicodeBuilderValue STREAM_FLUSH_U
    = new UnicodeBuilderValue("stream_flush");
  private static final UnicodeBuilderValue STREAM_OPEN_U
    = new UnicodeBuilderValue("stream_open");
  private static final UnicodeBuilderValue STREAM_READ_U
    = new UnicodeBuilderValue("stream_read");
  private static final UnicodeBuilderValue STREAM_SEEK_U
    = new UnicodeBuilderValue("stream_seek");
  private static final UnicodeBuilderValue STREAM_TELL_U
    = new UnicodeBuilderValue("stream_tell");
  private static final UnicodeBuilderValue STREAM_WRITE_U
    = new UnicodeBuilderValue("stream_write");

  private byte []printBuffer = new byte[1];

  private Env _env;
  private Value _wrapper;
  private LineReader _lineReader;

  private InputStream _is;
  private OutputStream _os;
  private int _buffer;
  private boolean _doUnread = false;

  private int _writeLength;

  private WrappedStream(Env env, Value wrapper)
  {
    _env = env;

    _wrapper = wrapper;

    _lineReader = new LineReader(env);
  }

  public WrappedStream(Env env,
                       QuercusClass qClass,
                       StringValue path,
                       StringValue mode,
                       LongValue options)
  {
    _env = env;

    _lineReader = new LineReader(env);

    _wrapper = qClass.callNew(env, VHelper.noCtx(), Value.VNULL_ARGS).getOne();

    if (env.isUnicodeSemantics())
      _wrapper.callMethod(env, VHelper.noCtx(), STREAM_OPEN_U,
                          path, mode, options, NullValue.NULL);
    else
      _wrapper.callMethod(env, VHelper.noCtx(), STREAM_OPEN,
                          path, mode, options, NullValue.NULL);
  }

  @Override
  public InputStream getInputStream()
  {
    if (_is == null)
      _is = new WrappedInputStream();

    return _is;
  }

  @Override
  public OutputStream getOutputStream()
  {
    if (_os == null)
      _os = new WrappedOutputStream();

    return _os;
  }

  /**
   * Opens a new copy.
   */
  @Override
  public BinaryInput openCopy()
    throws IOException
  {
    return new WrappedStream(_env, _wrapper);
  }

  /**
   * Sets the current read encoding.  The encoding can either be a
   * Java encoding name or a mime encoding.
   *
   * @param encoding name of the read encoding
   */
  public void setEncoding(String encoding)
    throws UnsupportedEncodingException
  {
  }

  @Override
  public void closeRead()
  {
    close();
  }

  @Override
  public void closeWrite()
  {
    close();
  }

  @Override
  public void close()
  {
    QuercusClass cls = _wrapper.getQuercusClass();

    if (cls != null) {
      StringValue funName;

      if (_env.isUnicodeSemantics()) {
        funName = STREAM_CLOSE_U;
      }
      else {
        funName = STREAM_CLOSE;
      }

      AbstractFunction fun = cls.findFunction(funName);

      if (fun == null) {
        //_env.warning(L.l("{0}::{1} is not implemented", cls.getName(), funName));

        return;
      }

      _wrapper.callMethod(_env, VHelper.noCtx(), funName);
    }
  }

  @Override
  public int getAvailable()
    throws IOException
  {
    return 1;
  }

  /**
   * Reads a character from a file, returning -1 on EOF.
   */
  @Override
  public int read()
    throws IOException
  {
    if (_doUnread) {
      _doUnread = false;

      return _buffer;
    } else {
      Value output;

      if (_env.isUnicodeSemantics())
        output = _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_READ_U, LongValue.ONE).getOne().toValue();
      else
        output = _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_READ, LongValue.ONE).getOne().toValue();

      _buffer = (int) output.toLong();

      return _buffer;
    }
  }

  /**
   * Unread a character.
   */
  @Override
  public void unread()
    throws IOException
  {
    _doUnread = true;
  }

  @Override
  public int read(byte []buffer, int offset, int length)
  {
    // XXX: should be reimplemented

    Value output;

    if (_env.isUnicodeSemantics())
      output = _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_READ_U,
                                   LongValue.create(length)).getOne().toValue();
    else
      output = _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_READ,
                                   LongValue.create(length)).getOne().toValue();

    if (output.length() == 0)
      return -1;

    byte []outputBytes = output.toString().getBytes();

    if (outputBytes.length < length)
      length = outputBytes.length;

    System.arraycopy(outputBytes, 0, buffer, offset, length);

    return length;
  }

  public int read(char []buffer, int offset, int length)
  {
    // XXX: should be reimplemented

    Value output;

    if (_env.isUnicodeSemantics())
      output = _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_READ_U,
                                   LongValue.create(length)).getOne().toValue();
    else
      output = _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_READ,
                                   LongValue.create(length)).getOne().toValue();

    if (output.length() == 0)
      return -1;

    byte []outputBytes = output.toString().getBytes();

    if (outputBytes.length < length)
      length = outputBytes.length;

    System.arraycopy(outputBytes, 0, buffer, offset, length);

    return length;
  }

  /**
   * Appends to a string builder.
   */
  @Override
  public StringValue appendTo(StringValue builder)
  {
    try {
      int ch;

      while ((ch = read()) >= 0) {
        builder.append(VHelper.noCtx(), (char) ch);
      }

      return builder;
    } catch (IOException e) {
      throw new QuercusModuleException(e);
    }
  }


  /**
   * Reads a Binary string.
   */
  @Override
  public StringValue read(int length)
    throws IOException
  {
    Value output;

    if (_env.isUnicodeSemantics())
      output = _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_READ_U,
                                   LongValue.create(length)).getOne().toValue();
    else
      output = _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_READ,
                                   LongValue.create(length)).getOne().toValue();

    return output.toBinaryValue(_env);
  }

  /**
   * Reads the optional linefeed character from a \r\n
   */
  @Override
  public boolean readOptionalLinefeed()
    throws IOException
  {
    int ch = read();

    if (ch == '\n') {
      return true;
    }
    else {
      unread();
      return false;
    }
  }

    /**
   * Reads a line from a file, returning null on EOF.
   */
  @Override
  public StringValue readLine(long length)
    throws IOException
  {
    return _lineReader.readLine(_env, this, length);
  }

  @Override
  public void write(byte []buffer, int offset, int length)
    throws IOException
  {
    StringValue bb = _env.createBinaryBuilder(buffer, offset, length);

    Value output;

    if (_env.isUnicodeSemantics())
      output = _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_WRITE_U, bb).getOne().toValue();
    else
      output = _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_WRITE, bb).getOne().toValue();

    _writeLength = (int) output.toLong();
  }

  /**
   * Writes to a stream.
   */
  @Override
  public int write(InputStream is, int length)
  {
    int writeLength = 0;

    TempBuffer tb = TempBuffer.allocate();
    byte []buffer = tb.getBuffer();

    try {
      while (length > 0) {
        int sublen;

        if (length < buffer.length)
          sublen = length;
        else
          sublen = buffer.length;

        sublen = is.read(buffer, 0, sublen);

        if (sublen < 0)
          break;

        for (int offset = 0; offset < sublen;) {
          write(buffer, offset, sublen);

          if (_writeLength > 0)
            offset += _writeLength;
          else
            return writeLength;
        }

        writeLength += sublen;
        length -= sublen;
      }

      return writeLength;
    } catch (IOException e) {
      throw new QuercusModuleException(e);
    } finally {
      TempBuffer.free(tb);
    }
  }

  /**
   * Prints a string to a file.
   */
  @Override
  public void print(char v)
    throws IOException
  {
    printBuffer[0] = (byte) v;

    write(printBuffer, 0, 1);
  }

  /**
   * Prints a string to a file.
   */
  @Override
  public void print(String v)
    throws IOException
  {
    for (int i = 0; i < v.length(); i++)
      print(v.charAt(i));
  }

  /**
   * Returns true if end-of-file has been reached
   */
  @Override
  public boolean isEOF()
  {
    if (_env.isUnicodeSemantics())
      return _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_EOF_U).getOne().toValue().toBoolean();
    else
      return _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_EOF).getOne().toValue().toBoolean();
  }

  /**
   * Tells the position in the stream
   */
  @Override
  public long getPosition()
  {
    if (_env.isUnicodeSemantics())
      return _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_TELL_U).getOne().toValue().toLong();
    else
      return _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_TELL).getOne().toValue().toLong();
  }

  /**
   * Sets the position.
   */
  @Override
  public boolean setPosition(long offset)
  {
    LongValue offsetValue = LongValue.create(offset);
    LongValue whenceValue = LongValue.create(SEEK_SET);

    if (_env.isUnicodeSemantics())
      return _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_SEEK_U,
                                 offsetValue, whenceValue).getOne().toValue().toBoolean();
    else
      return _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_SEEK,
                                 offsetValue, whenceValue).getOne().toValue().toBoolean();
  }

  @Override
  public long seek(long offset, int whence)
  {
    LongValue offsetValue = LongValue.create(offset);
    LongValue whenceValue = LongValue.create(whence);

    if (_env.isUnicodeSemantics())
      return _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_SEEK_U,
                                 offsetValue, whenceValue).getOne().toValue().toLong();
    else
      return _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_SEEK,
                                 offsetValue, whenceValue).getOne().toValue().toLong();
  }

  @Override
  public void flush()
    throws IOException
  {
    boolean result;

    if (_env.isUnicodeSemantics())
      result = _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_FLUSH_U).getOne().toValue().toBoolean();
    else
      result = _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_FLUSH).getOne().toValue().toBoolean();

    if (! result)
      throw new IOException(); // Get around java.io.Flushable
  }

  @Override
  public Value stat()
  {
    if (_env.isUnicodeSemantics())
      return _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_FLUSH_U).getOne().toValue();
    else
      return _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_FLUSH).getOne().toValue();
  }

  private class WrappedInputStream extends InputStream {
    @Override
    public int read()
      throws IOException
    {
      return WrappedStream.this.read();
    }
  }

  private class WrappedOutputStream extends OutputStream {
    @Override
    public void write(int b)
      throws IOException
    {
      if (_env.isUnicodeSemantics())
        _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_WRITE_U, LongValue.create(b));
      else
        _wrapper.callMethod(_env, VHelper.noCtx(), STREAM_WRITE, LongValue.create(b));
    }
  }
}
