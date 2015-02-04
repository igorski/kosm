package nl.igorski.lib.audio.interfaces;

import nl.igorski.lib.audio.vo.instruments.InternalSynthInstrument;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 28-04-12
 * Time: 14:59
 * To change this template use File | Settings | File Templates.
 */
public interface IUpdateableInstrument
{
    public void updateInstrument( InternalSynthInstrument instrument );
}
