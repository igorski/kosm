/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2012-2014 Igor Zinken - http://www.igorski.nl
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package nl.igorski.lib.utils.notifications;

import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;
import nl.igorski.lib.framework.Core;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 12-05-12
 * Time: 13:51
 * To change this template use File | Settings | File Templates.
 */
public final class Confirm
{
    /**
     * show a confirmation window requesting user interaction
     *
     * @param aContext          {Context} current application context, IMPORTANT: Google docs claim "getApplicationContext"
     *                          should suffice, while in reality MyActivity.this should be passed!
     * @param aTitle            {String} title of the window
     * @param aMessage          {String} message displayed in the window
     * @param aPositiveHandler  {DialogInterface.OnClickListener} the handler when the user gives a positive answer
     * @param aNegativeHandler  {DialogInterface.OnClickListener} the handler when the user gives a negative answer
     */
    public static void confirm( Context aContext, String aTitle, String aMessage,
                                DialogInterface.OnClickListener aPositiveHandler, DialogInterface.OnClickListener aNegativeHandler )
    {
        doConfirm( aContext, aTitle, aMessage, aPositiveHandler, aNegativeHandler );
    }

    /**
     * @param aContext          {Context} current application context, IMPORTANT: Google docs claim "getApplicationContext"
     *                          should suffice, while in reality MyActivity.this should be passed!
     * @param aTitle            {int} res ID of the title String for the window
     * @param aMessage          {int} res ID of the message String to be displayed in the window
     * @param aPositiveHandler  {DialogInterface.OnClickListener} the handler when the user gives a positive answer
     * @param aNegativeHandler  {DialogInterface.OnClickListener} the handler when the user gives a negative answer
     */
    public static void confirm( Context aContext, int aTitle, int aMessage,
                                DialogInterface.OnClickListener aPositiveHandler, DialogInterface.OnClickListener aNegativeHandler )
    {
        doConfirm( aContext, aContext.getString( aTitle ), aContext.getString( aMessage ), aPositiveHandler, aNegativeHandler );
    }

    /**
     * shows a popup window with a single "OK" button without handler logic
     * (just to a show text which requires user interaction to dismiss)
     *
     * @param aContext
     * @param aTitle
     * @param aMessage
     */
    public static void singleButtonConfirm( final Context aContext, final int aTitle, final int aMessage )
    {

        Core.getActivity().runOnUiThread( new Runnable()
        {
            public void run()
            {
                final AlertDialog dialog = new AlertDialog.Builder( aContext )
                                           .setIcon( android.R.drawable.ic_dialog_alert )
                                           .setTitle( aTitle )
                                           .setMessage( aMessage )
                                           .setPositiveButton( "OK", null ).create();

                //dialog.getWindow().addFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL );
                dialog.show();
            }
        });
    }

    /* private methods */

    private static void doConfirm( final Context aContext, final String aTitle, final String aMessage,
                                   final DialogInterface.OnClickListener aPositiveHandler,
                                   DialogInterface.OnClickListener aNegativeHandler )
    {
        if ( aNegativeHandler == null )
        {
            aNegativeHandler = new DialogInterface.OnClickListener()
            {
               public void onClick( DialogInterface dialog, int id )
               {
                    dialog.cancel();
               }
           };
        }
        final DialogInterface.OnClickListener theNegHandler = aNegativeHandler;

        Core.getActivity().runOnUiThread( new Runnable()
        {
            public void run()
            {
                final AlertDialog dialog = new AlertDialog.Builder( aContext )
                                           .setIcon( android.R.drawable.ic_dialog_alert )
                                           .setTitle( aTitle )
                                           .setMessage( aMessage )
                                           .setPositiveButton( R.string.yes, aPositiveHandler )
                                           .setNegativeButton( R.string.no,  theNegHandler ).create();

                //dialog.getWindow().addFlags( WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL );
                dialog.show();
            }
        });
    }
}
