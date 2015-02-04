package nl.igorski.kosm.externals.soundcloud;

import nl.igorski.lib.utils.data.XMLTool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: igorzinken
 * Date: 27-06-12
 * Time: 20:41
 * To change this template use File | Settings | File Templates.
 */
public final class SoundCloudFactory
{
    public static SoundCloudUser parseUserFromXML( Document xml )
    {
        final SoundCloudUser user = new SoundCloudUser();

        final Element element = ( Element ) xml.getFirstChild(); // is <user> node

        user.avatarUri = XMLTool.getTagValue(element, "avatar-url");
        user.city      = XMLTool.getTagValue(element, "city");
        user.country   = XMLTool.getTagValue(element, "country");
        user.fullName  = XMLTool.getTagValue(element, "full-name");
        user.id        = Integer.parseInt   ( XMLTool.getTagValue(element, "id"));
        user.kind      = XMLTool.getTagValue(element, "kind");
        user.uri       = XMLTool.getTagValue(element, "uri");
        user.username  = XMLTool.getTagValue(element, "username");

        return user;
    }

    public static SoundCloudResult parseResultFromXML( Document xml )
    {
        final SoundCloudResult result = new SoundCloudResult();

        final Element element = ( Element ) xml.getFirstChild(); // is <track> node

        result.trackId     = Integer.parseInt( XMLTool.getTagValue(element, "id"));
        result.userId      = Integer.parseInt( XMLTool.getTagValue(element, "user-id"));
        result.trackURL    = XMLTool.getTagValue(element, "permalink-url");
        result.trackTitle  = XMLTool.getTagValue(element, "track-title");
        result.permaLink   = XMLTool.getTagValue(element, "permalink");
        result.waveFormURL = XMLTool.getTagValue(element, "waveform-url");

        return result;
    }
}
