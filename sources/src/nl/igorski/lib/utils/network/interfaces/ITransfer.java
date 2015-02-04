package nl.igorski.lib.utils.network.interfaces;

import org.apache.http.HttpResponse;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 03-07-12
 * Time: 20:46
 * To change this template use File | Settings | File Templates.
 */
public interface ITransfer
{
    /* invokes the actual transfer */
    public HttpResponse doTransfer();

    /* returns the response Object */
    public void returnHandler(HttpResponse response);
}
