package nl.igorski.kosm;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.nineoldandroids.view.ViewHelper;
import nl.igorski.kosm.controller.menu.OpenEffectsMenuCommand;
import nl.igorski.kosm.controller.menu.OpenWaveformMenuCommand;
import nl.igorski.lib.listeners.ICompleteListener;

import nl.igorski.kosm.R;
import nl.igorski.kosm.controller.menu.CloseEffectsMenuCommand;
import nl.igorski.kosm.controller.menu.CloseWaveformMenuCommand;
import nl.igorski.kosm.controller.startup.StartupCommand;
import nl.igorski.kosm.definitions.Assets;
import nl.igorski.kosm.view.ui.ViewRenderer;
import nl.igorski.kosm.view.ParticleSequencer;
import nl.igorski.lib.activities.BaseActivity;
import nl.igorski.lib.framework.Core;
import nl.igorski.lib.utils.debugging.DebugTool;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 6/8/12
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class Kosm extends BaseActivity implements ICompleteListener
{
    private static ParticleSequencer _sequencer;

    /* UI elements */

    public static ImageButton btnWaveformToggle;
    public static boolean     waveformMenuOpened = false;
    public static ImageButton btnSine;
    public static ImageButton btnSaw;
    public static ImageButton btnTwang;
    public static ImageButton btnKick;
    public static ImageButton btnSnare;

    public static ImageButton btnEffectsToggle;
    public static boolean     effectsMenuOpened = false;
    public static ImageButton btnDelay;
    public static ImageButton btnDistortion;
    public static ImageButton btnFilter;

    /* called when the activity is first created. */

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState, R.layout.main, true );

        DebugTool.LOG_TAG = "KOSM";

        Core.notify( new StartupCommand() );
    }

    /* public methods */

    public void handleComplete( boolean completed )
    {
        // sine is default particle sound set in ParticleSequencer, highlight button
        btnSine.setImageDrawable( getResources().getDrawable( R.drawable.icon_sine_active ));
    }

    public static ViewRenderer getViewRenderer()
    {
        return _sequencer.getViewRenderer();
    }

    /* protected methods */

    @Override
    protected void initAssets()
    {
        _sequencer = ( ParticleSequencer ) findViewById( R.id.app );

        /* grab references to waveform buttons in template */

        btnWaveformToggle = ( ImageButton ) findViewById( R.id.WFToggle );
        btnSine           = ( ImageButton ) findViewById( R.id.ButtonSine );
        btnSaw            = ( ImageButton ) findViewById( R.id.ButtonSaw );
        btnTwang          = ( ImageButton ) findViewById( R.id.ButtonTwang );
        btnKick           = ( ImageButton ) findViewById( R.id.ButtonKick );
        btnSnare          = ( ImageButton ) findViewById( R.id.ButtonSnare );

        btnWaveformToggle.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if ( !waveformMenuOpened )
                    Core.notify( new OpenWaveformMenuCommand() );
                else
                    Core.notify( new CloseWaveformMenuCommand() );
            }
        });

        ViewHelper.setTranslationX( btnWaveformToggle, -btnWaveformToggle.getWidth() / 2 + btnWaveformToggle.getLeft() );

        /* grab references to effects buttons in template */

        btnEffectsToggle = ( ImageButton ) findViewById( R.id.FXToggle );
        btnDelay         = ( ImageButton ) findViewById( R.id.ButtonDelay );
        btnDistortion    = ( ImageButton ) findViewById( R.id.ButtonDistortion );
        btnFilter        = ( ImageButton ) findViewById( R.id.ButtonFilter );

        btnEffectsToggle.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                if ( !effectsMenuOpened )
                    Core.notify( new OpenEffectsMenuCommand() );
                else
                    Core.notify( new CloseEffectsMenuCommand() );
            }
        });

        _sequencer.setModeToggleListener(( Button )      findViewById( R.id.ButtonModeToggle ));
        _sequencer.setRecordListener    (( ImageButton ) findViewById( R.id.ButtonRecord ));

        _sequencer.setKickListener ( btnKick  );
        _sequencer.setSawListener  ( btnSaw   );
        _sequencer.setSnareListener( btnSnare );
        _sequencer.setSineListener ( btnSine  );
        _sequencer.setTwangListener( btnTwang );

        _sequencer.setDelayListener     ( btnDelay );
        _sequencer.setDistortionListener( btnDistortion );
        _sequencer.setFilterListener    ( btnFilter );

        // initialize the assets
        Assets.init( getApplicationContext(), this );
    }
}
