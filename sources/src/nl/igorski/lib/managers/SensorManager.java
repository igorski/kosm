package nl.igorski.lib.managers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import nl.igorski.lib.listeners.ISensorChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 4/27/12
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public final class SensorManager implements SensorEventListener
{
    private android.hardware.SensorManager sensorManager;

    private double ax, ay, az;

    private ISensorChangeListener _listener;

    public SensorManager( Context aContext )
    {
        sensorManager = ( android.hardware.SensorManager) aContext.getSystemService( Context.SENSOR_SERVICE );
        sensorManager.registerListener( this, sensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER ),
                                        android.hardware.SensorManager.SENSOR_DELAY_NORMAL );
    }

    /* public */

    public void addListener( ISensorChangeListener listener )
    {
        _listener = listener;
    }

    public void onAccuracyChanged( Sensor arg0, int arg1 )
    {

    }

    public void onSensorChanged( SensorEvent event )
    {
        if ( event.sensor.getType() == Sensor.TYPE_ACCELEROMETER )
        {
            ax = event.values[ 0 ];
            ay = event.values[ 1 ];
            az = event.values[ 2 ];

            Log.d("SYNTH", "x" + ax + " y " + ay + "z " + az);
        }
    }
}
