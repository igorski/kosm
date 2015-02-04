package org.urbanstew.soundcloudapi;

import java.io.IOException;
import java.io.OutputStream;


public class CountingOutputStream extends OutputStream
{

    public CountingOutputStream(OutputStream out)
    {
        mOut = out;
    }

    public void write(byte[] b) throws IOException
    {
        mCount += b.length;
        mOut.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException
    {
    	mCount += len;
        mOut.write(b, off, len);
    }

    public void write(int b) throws IOException
    {
    	mCount++;
        mOut.write(b);
    }

    public int getCount()
    {
        return mCount;
    }

    private int mCount;
    protected OutputStream mOut;

}
