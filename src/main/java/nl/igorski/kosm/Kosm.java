package nl.igorski.kosm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import nl.igorski.kosm.controller.menu.OpenEffectsMenuCommand;
import nl.igorski.kosm.controller.menu.OpenWaveformMenuCommand;
import nl.igorski.lib.listeners.ICompleteListener;

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
    private static Kosm INSTANCE;
    private ParticleSequencer _sequencer;

    public static int PERMISSIONS_CODE = 12340012;

    /* UI elements */

    public static boolean waveformMenuOpened = false;
    public static boolean effectsMenuOpened  = false;

    public Kosm() {
        INSTANCE = this;
    }

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
        getBtnSine().setImageDrawable( getResources().getDrawable( R.drawable.icon_sine_active, null ));
    }

    /* getters */

    public static ImageButton getBtnWaveformToggle() {
        return INSTANCE.findViewById( R.id.WFToggle );
    }

    public static ImageButton getBtnSine() {
        return INSTANCE.findViewById( R.id.ButtonSine );
    }

    public static ImageButton getBtnSaw() {
        return INSTANCE.findViewById( R.id.ButtonSaw );
    }

    public static ImageButton getBtnTwang() {
        return INSTANCE.findViewById( R.id.ButtonTwang );
    }

    public static ImageButton getBtnKick() {
        return INSTANCE.findViewById( R.id.ButtonKick );
    }

    public static ImageButton getBtnSnare() {
        return INSTANCE.findViewById( R.id.ButtonSnare );
    }

    public static ImageButton getBtnEffectsToggle() {
        return INSTANCE.findViewById( R.id.FXToggle );
    }

    public static ImageButton getBtnDelay() {
        return INSTANCE.findViewById( R.id.ButtonDelay );
    }

    public static ImageButton getBtnDistortion() {
        return INSTANCE.findViewById( R.id.ButtonDistortion );
    }

    public static ImageButton getBtnFilter() {
        return INSTANCE.findViewById( R.id.ButtonFilter );
    }

    public static ImageButton getBtnFormant() {
        return INSTANCE.findViewById( R.id.ButtonFormant );
    }

    public static ImageButton getBtnPitchshifter() {
        return INSTANCE.findViewById( R.id.ButtonPitchshifter );
    }

    public static ImageButton getBtnRecord() {
        return INSTANCE.findViewById( R.id.ButtonRecord );
    }

    public static ViewRenderer getViewRenderer() {
        return INSTANCE._sequencer.getViewRenderer();
    }

    /* protected methods */

    @Override
    protected void initAssets()
    {
        _sequencer = findViewById( R.id.app );

        getBtnWaveformToggle().setOnClickListener( v -> {
            if ( !waveformMenuOpened )
                Core.notify( new OpenWaveformMenuCommand() );
            else
                Core.notify( new CloseWaveformMenuCommand() );
        });

        getBtnWaveformToggle().setTranslationX( -getBtnWaveformToggle().getWidth() / 2 + getBtnWaveformToggle().getLeft() );

        getBtnEffectsToggle().setOnClickListener( v -> {
            if ( !effectsMenuOpened )
                Core.notify( new OpenEffectsMenuCommand() );
            else
                Core.notify( new CloseEffectsMenuCommand() );
        });

        _sequencer.setModeToggleListener( findViewById( R.id.ButtonModeToggle ));
        _sequencer.setRecordListener    ( findViewById( R.id.ButtonRecord ));

        _sequencer.setKickListener ( getBtnKick()  );
        _sequencer.setSawListener  ( getBtnSaw()   );
        _sequencer.setSnareListener( getBtnSnare() );
        _sequencer.setSineListener ( getBtnSine()  );
        _sequencer.setTwangListener( getBtnTwang() );

        _sequencer.setDelayListener       ( getBtnDelay() );
        _sequencer.setDistortionListener  ( getBtnDistortion() );
        _sequencer.setFilterListener      ( getBtnFilter() );
        _sequencer.setFormantListener     ( getBtnFormant() );
        _sequencer.setPitchShifterListener( getBtnPitchshifter() );

        // initialize the assets
        Assets.init( getApplicationContext(), this );
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if ( requestCode == PERMISSIONS_CODE && resultCode == RESULT_OK ) {
            _sequencer.toggleRecordingState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        INSTANCE = null;
    }
}
