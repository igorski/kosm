package org.urbanstew.soundcloudapi;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.mime.content.FileBody;

/**
 * This FileBody can be used to monitor the progress of a file upload.
 * 
 * <pre>
 * {@code
 *  File file = new File("audio.wav");
 *  
 *  final List<NameValuePair> params = new java.util.ArrayList<NameValuePair>();
 *  params.add(new BasicNameValuePair("track[title]", "This is a test upload"));
 *  params.add(new BasicNameValuePair("track[sharing]", "private"));
 *  
 *  final ProgressFileBody fileBody = new ProgressFileBody(file);
 *  
 *  Thread progressThread = new Thread(new Runnable()
 *  {
 *  	public void run()
 * 		{
 * 			try
 * 			{
 * 				mApi.upload(fileBody, params);
 * 			} catch (Exception e)
 * 			{
 * 				e.printStackTrace();
 * 			}
 * 		}
 * 	});
 *  
 *  progressThread.start();
 *  while(progressThread.isAlive())
 *  	System.out.println(fileBody.getBytesTransferred());
 *  }
 * </pre>
 * 
 * @since 0.9.2
 */
public class ProgressFileBody extends FileBody
{

	public ProgressFileBody(File file)
	{
		super(file);
	}

    public void writeTo(final OutputStream out) throws IOException 
    { 
    	super.writeTo(mStream = new CountingOutputStream(out));
    } 

    public long getBytesTransferred()
    {
    	return mStream == null ? 0 : mStream.getCount();
    }
    
    CountingOutputStream mStream = null;
}
