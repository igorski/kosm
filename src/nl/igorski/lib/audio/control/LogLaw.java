package nl.igorski.lib.audio.control;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 10/12/12
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public final class LogLaw extends AbstractLaw
{
    private double logMin, logMax;
    private double logSpan;

    public LogLaw( double min, double max, String units )
    {
        super( min, max, units );
        assert min != 0.0;
        assert max != 0.0;

        logMin = Math.log10( min ); // error on zero min
        logMax = Math.log10( max ); // error zero max

        logSpan = logMax - logMin;
    }

    //  min <= userVal <= max
    public int intValue( double userVal )
    {
        if ( userVal == 0 )
            userVal = 1; // !!! protect log

        return ( int )(( resolution - 1 ) *  (Math.log10( userVal ) - logMin ) / logSpan );
    }

    // 0 <= intVal < resolution
    public double userValue( int intVal )
    {
        double p = logMin + ( logSpan * intVal ) / ( resolution - 1 ) ;
        return Math.pow( 10, p );
    }
}
