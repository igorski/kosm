package com.ks.iter1;

public class RingBuffer
{
    private double[] buffer;
    private int first = 0, last = 0;

    public RingBuffer( int capacity )
    {
        buffer = new double[ capacity ];
    }

    /* public */

    public int getSize()
    {
        return ( last - first );
    }

    public boolean isEmpty()
    {
        return ( getSize() == 0 );
    }

    public boolean isFull()
    {
        return ( getSize() == buffer.length );
    }

    public void enqueue( double aSample )
    {
        buffer[ last ] = aSample;

        if ( ++last == buffer.length )
            last = 0;
    }

    public double dequeue()
    {
        final double item = buffer[ first ];

        if ( ++first == buffer.length )
            first = 0;

        return item;
    }

    public double peek()
    {
        return buffer[ first ];
    }
}
