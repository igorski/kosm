package nl.igorski.lib.audio.control;

/**
 * Created with IntelliJ IDEA.
 * User: igorzinken
 * Date: 10/12/12
 * Time: 10:25 AM
 * To change this template use File | Settings | File Templates.
 */
public final class LinearLaw extends AbstractLaw
{
    /*
    * A unitless LinearLaw from zero to one (unity)
    */

    /**
     * @link aggregationByValue
     * @supplierCardinality 1
     * @label UNITY
     */
    public final static LinearLaw UNITY = new LinearLaw( 0, 1, "" );

    public LinearLaw( double min, double max, String units )
    {
        super( min, max, units );
    }

    public int intValue( double v )
    {
        return ( int )(( resolution - 1 ) * ( v - min ) / ( max - min ));
    }

    public double userValue( int v )
    {
        return min + ( max - min ) * (( double ) v / ( resolution - 1 ));
    }
}
