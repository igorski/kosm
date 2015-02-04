/*
 *  Copyright 2009 urbanSTEW
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.urbanstew.soundcloudapi;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.signature.HmacSha1MessageSigner;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class SoundCloudAPI
{
	public enum OAuthVersion
	{
		V1_0,
		V1_0_A,
		V2_0
	}
	
	public enum SoundCloudSystem
	{
		PRODUCTION,
		SANDBOX
	}
	
	public static final SoundCloudOptions USE_SANDBOX = new SoundCloudOptions(SoundCloudSystem.SANDBOX);
	public static final SoundCloudOptions USE_PRODUCTION = new SoundCloudOptions(SoundCloudSystem.PRODUCTION);
	
    public enum State
    {
    	UNAUTHORIZED,
    	REQUEST_TOKEN_OBTAINED,
    	AUTHORIZED
    };

    /**
     * Constructor for the case when neither the request or access token have
     * been obtained.
     */
    public SoundCloudAPI(String consumerKey, String consumerSecret)
	{
		this(consumerKey, consumerSecret, "", "", new SoundCloudOptions());
	}
    
    /**
     * Constructor for the case when neither the request or access token have
     * been obtained, with specified options.
     */
    public SoundCloudAPI(String consumerKey, String consumerSecret, SoundCloudOptions options)
	{
		this(consumerKey, consumerSecret, "", "", options);
	}

    /**
     * Constructor for the case when the access token has been obtained.
     */
    public SoundCloudAPI(String consumerKey, String consumerSecret, String token, String tokenSecret)
	{
    	this(consumerKey, consumerSecret, token, tokenSecret, new SoundCloudOptions());
	}
    
    /**
     * Constructor for the case when the access token has been obtained, with specified options.
     */
    public SoundCloudAPI(String consumerKey, String consumerSecret, String token, String tokenSecret, SoundCloudOptions options)
	{
    	initialize();
    	
    	mOptions = options;
		mConsumer.set(new CommonsHttpOAuthConsumer(consumerKey, consumerSecret));
		getConsumer().setMessageSigner(new HmacSha1MessageSigner());
    	if(token.length()==0 || tokenSecret.length()==0)
    	{
    		mState = State.UNAUTHORIZED;
    	}
    	else
    	{
    		mState = State.AUTHORIZED;
    		getConsumer().setTokenWithSecret(token, tokenSecret);
    	}
		setUsingSandbox(options.system == SoundCloudSystem.SANDBOX);
	}
	
    /**
     * Constructor from another SoundCloudAPI.
     */
    public SoundCloudAPI(SoundCloudAPI soundCloudAPI)
	{
    	initialize();
    	
        mState = soundCloudAPI.mState;
        mOptions = soundCloudAPI.mOptions;
        mConsumer.set(new CommonsHttpOAuthConsumer(soundCloudAPI.getConsumer().getConsumerKey(), soundCloudAPI.getConsumer().getConsumerSecret()));
        getConsumer().setMessageSigner(new HmacSha1MessageSigner());
        
        if(mState == State.AUTHORIZED)
        	getConsumer().setTokenWithSecret(soundCloudAPI.getConsumer().getToken(), soundCloudAPI.getConsumer().getTokenSecret());
    	mSoundCloudURL = soundCloudAPI.mSoundCloudURL;
    	mSoundCloudApiURL = soundCloudAPI.mSoundCloudApiURL;

    	mProvider = new DefaultOAuthProvider
	    	(
	    		mSoundCloudApiURL + "oauth/request_token",
	    		mSoundCloudApiURL + "oauth/access_token",
	    		mSoundCloudURL + "oauth/authorize"
	    	);
	}
    
    private void initialize()
    {    	
    	HttpClient client = new DefaultHttpClient();
    	
        httpClient = new DefaultHttpClient
        	(
        		new ThreadSafeClientConnManager(client.getParams(), client.getConnectionManager().getSchemeRegistry()),
        		client.getParams()
        	);
                
    }
	
	private void setUsingSandbox(boolean use)
	{
		String protocol = mOptions.version == OAuthVersion.V2_0 ? "https://" : "http://";
		if(!use)
		{
			mSoundCloudURL = protocol + "soundcloud.com/";
			mSoundCloudApiURL = protocol + "api.soundcloud.com/";
		}
		else
		{
			mSoundCloudURL = protocol + "sandbox-soundcloud.com/";
			mSoundCloudApiURL = protocol + "api.sandbox-soundcloud.com/";
		}
		
	    mProvider = new DefaultOAuthProvider
	    	(
	    		mSoundCloudApiURL + "oauth/request_token",
	    		mSoundCloudApiURL + "oauth/access_token",
	    		mSoundCloudURL + "oauth/authorize"
	    	);
	}
	
	public void unauthorize()
	{
		getConsumer().setTokenWithSecret("", "");
		mState = State.UNAUTHORIZED;
	}
	
    /**
     * Obtains the request token from Sound Cloud
     * @return authorization URL on success, null otherwise.
     * @throws OAuthCommunicationException 
     * @throws OAuthExpectationFailedException 
     * @throws OAuthNotAuthorizedException 
     * @throws OAuthMessageSignerException 
     */
	public String obtainRequestToken() throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException
	{
		return obtainRequestToken(null);
	}

    /**
     * Obtains the request token from Sound Cloud, with a specified callback URL.
     * @return authorization URL on success, null otherwise.
     * @throws OAuthCommunicationException 
     * @throws OAuthExpectationFailedException 
     * @throws OAuthNotAuthorizedException 
     * @throws OAuthMessageSignerException 
     */
	public String obtainRequestToken(String callbackURL) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException
	{
		unauthorize();
      
    	if(callbackURL == null && mOptions.version == OAuthVersion.V1_0_A)
    		callbackURL = OAuth.OUT_OF_BAND;
		String url = mProvider.retrieveRequestToken(getConsumer(), callbackURL);
		mState = State.REQUEST_TOKEN_OBTAINED;
		return url;
	}
	
    /**
     * Completes the OAuth 1.0a authorization steps with SoundCloud, assuming the consumer application
     * can use a local port to receive the verification code.
     * 
     * <p>The function acts as a minimal HTTP server and will listen on the port specified in the
     * <code>url</code> (or the default HTTP port, if no port is specified in the <code>url</code>).  It will provide the
     * specified <code>response</code> when it receives a request for the path specified in the <code>url</code>, and
     * assuming the request includes the verification code, terminate successfully.
     * To all other requests it will respond with a <code>Not Found</code> error, and continue listening.
     * 
     * <p>The following example assumes the consumer application is running on the client's computer / device.
     * Hence, it uses a local URL ("http://localhost:8088/") to receive the verification code callback. The function
     * will listen on specified port 8088 to receive the callback.</p>
     * 
     * <pre>
     * {@code
     *  soundcloudapi.authorizeUsingUrl
	 *	(
	 *		"http://localhost:8088/",
	 *		"Thank you for authorizing",
	 *		new AuthorizationURLOpener()
	 *		{
	 *			public void openAuthorizationURL(String authorizationURL)
	 *			{
	 *				System.out.println("Please visit " + authorizationURL);
	 *			}
	 *		}
	 *	);
	 * }
	 * </pre>
	 * 
     * @param url - a callback URL via which the user can provide the verification code.
     * @param response - a response given back to the user when they allow access and get redirected to the callback URL.
     * @param URLOpener - an AuthorizationURLOpener which can open the authorization URL to the user when needed.
	 *
     * @return true if the process is completed successfully, false if the process was canceled via <code>cancelAuthorizeUsingUrl</code>.  
     *  
     * @throws OAuthCommunicationException 
     * @throws OAuthExpectationFailedException 
     * @throws OAuthNotAuthorizedException 
     * @throws OAuthMessageSignerException 
     * @throws IOException 

     * @since 0.9.1
     * @see #cancelAuthorizeUsingUrl()
	*/
	public boolean authorizeUsingUrl(final String url, final String response, final AuthorizationURLOpener URLOpener) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException, IOException
	{
		mCancelAuthorization = false;
		unauthorize();

		URLOpener.openAuthorizationURL(obtainRequestToken(url));

		URL parsedUrl = new URL(url);
		int port = parsedUrl.getPort();
		if(port == -1)
			port = parsedUrl.getDefaultPort();

		ServerSocket server = null;
		String verificationCode=null;
		
		try
		{
			server = new ServerSocket(port);
			server.setSoTimeout(500);

			while(verificationCode==null)
			{
				Socket socket = null;
				BufferedReader in = null;
				PrintWriter out = null;
				
				try
				{
					try
					{
						socket = server.accept();
					} catch (java.io.InterruptedIOException e)
					{
						if(mCancelAuthorization)
						{
							unauthorize();
							return false;
						}
						else continue;
					}
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
					String requestedUrl = in.readLine().split("\\s+")[1];
				
					URL parsedRequestedUrl = new URL("http://localhost" + requestedUrl);
					out = new PrintWriter(socket.getOutputStream(), true);
	
					if(!parsedRequestedUrl.getPath().equals(parsedUrl.getPath()))
						out.print("HTTP/1.1 404 Not Found");
					else
					{
						out.print("HTTP/1.1 200 OK\n\n" + response);
						for(String parameter : parsedRequestedUrl.getQuery().split("&"))
						{
							String[] keyValue = parameter.split("=");
							if(keyValue[0].equals("oauth_verifier"))
								verificationCode=keyValue[1];
						}
						if(verificationCode==null)
							// problem - why didn't we get a verification code?
							verificationCode="";
					}
					out.flush();
				}
				finally
				{
					closeQuietly(in);
					closeQuietly(out);
					closeQuietly(socket);					
				}
			}
		}
		finally
		{
			closeQuietly(server);
		}
        
        if(verificationCode.length()>0)
        {
        	obtainAccessToken(verificationCode);
        	return true;
        }
        else
        	return false;
	}
	
	/**
     * If a call to authorizeUsingUrl is currently executing, it will be canceled and return
     * shortly after cancelAuthorizeUsingUrl is called.  If there is no active authorizeUsingUrl
     * call, there is no effect.
     * 
     * @see #authorizeUsingUrl(String, String, AuthorizationURLOpener)
     * @since 0.9.1
     */
	public void cancelAuthorizeUsingUrl()
	{
		mCancelAuthorization = true;
	}
	
	/**
     * Swaps the authorized request token for an access token.
	 * @throws OAuthCommunicationException 
	 * @throws OAuthExpectationFailedException 
	 * @throws OAuthNotAuthorizedException 
	 * @throws OAuthMessageSignerException 
     */
	public void obtainAccessToken(String verificationCode) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException
	{
		mProvider.retrieveAccessToken(getConsumer(), verificationCode);
		mState = State.AUTHORIZED;
	}
	
	/**
     * Obtains an OAuth 2.0 access token using username/password credentials.
	 * @throws IOException
	 * @throws ClientProtocolException
     */
	public void obtainAccessToken(String username, String password) throws ClientProtocolException, IOException
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		  
	    params.add(new BasicNameValuePair("grant_type", "password"));
	    params.add(new BasicNameValuePair("username", username));
	    params.add(new BasicNameValuePair("password", password));

	    obtainAccessToken(params);
	}
	
	/**
     * Refreshes an OAuth 2.0 access token using the refresh token.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws OAuthCommunicationException 
     */
	public void refreshAccessToken() throws ClientProtocolException, IOException 
	{		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		  
	    params.add(new BasicNameValuePair("grant_type", "refresh_token"));
	    params.add(new BasicNameValuePair("refresh_token", this.getTokenSecret()));
	    
	    obtainAccessToken(params);
	}
	
	private void obtainAccessToken(List<NameValuePair> params) throws ClientProtocolException, IOException
	{
		if(mOptions.version != SoundCloudAPI.OAuthVersion.V2_0)
			throw new RuntimeException("username/password authorization is only supported for OAuth 2.0");
		
		HttpPost post = new HttpPost(urlEncode("oauth2/token", null));

	    params.add(new BasicNameValuePair("client_id", getConsumer().getConsumerKey()));
	    params.add(new BasicNameValuePair("client_secret", getConsumer().getConsumerSecret()));
		
		try
		{
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		} catch (UnsupportedEncodingException e)
		{
		}
	    
		HttpResponse response = httpClient.execute(post);
		
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder entityString = new StringBuilder();

        try
        {
            String line;
            while ((line = reader.readLine()) != null)
            	entityString.append(line + "\n");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        JSONObject json = null;
        try
		{
			json=new JSONObject(entityString.toString());

			if(json.has("access_token") && json.has("refresh_token"))
			{
				getConsumer().setTokenWithSecret
				(
					json.getString("access_token"),
					json.getString("refresh_token")
				);

				mState = State.AUTHORIZED;
			}
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
    /**
     * Performs a GET request on a specified resource.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws OAuthCommunicationException 
     */
	public HttpResponse get(String resource) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
		return get(resource, null);
	}

    /**
     * Performs a GET request on a specified resource, with parameters.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws OAuthCommunicationException 
     */
	public HttpResponse get(String resource, List<NameValuePair> params) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
		return httpClient.execute(getRequest(resource, params));
	}
	
    /**
     * Prepares a GET request on a specified resource, with parameters.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws OAuthCommunicationException 
     */
	public HttpUriRequest getRequest(String resource, List<NameValuePair> params) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException
	{
		return signRequest(new HttpGet(urlEncode(resource, params)));
	}
	
	/**
     * Performs a PUT request on a specified resource.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws OAuthCommunicationException 
     */
	public HttpResponse put(String resource) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
		return put(resource, (List<NameValuePair>) null);
	}

    /**
     * Performs a PUT request on a specified resource, with parameters.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws OAuthCommunicationException 
     */
	public HttpResponse put(String resource, List<NameValuePair> params) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
        return httpClient.execute(putRequest(resource, params));   
	}

    /**
     * Prepares a PUT request on a specified resource, with parameters.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws OAuthCommunicationException 
     */
	public HttpUriRequest putRequest(String resource, List<NameValuePair> params) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException
	{
		HttpPut put = new HttpPut(urlEncode(resource, params));
		
		// fix contributed by Bjorn Roche
		put.getParams().setBooleanParameter( "http.protocol.expect-continue", false );

		return signRequest(put);
	}

    /**
     * Performs a PUT request on a specified resource, with an entity.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws OAuthCommunicationException 
     */
	public HttpResponse put(String resource, HttpEntity entity) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
        return httpClient.execute(putRequest(resource, entity));
	}

    /**
     * Prepares a PUT request on a specified resource, with an entity.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws OAuthCommunicationException 
     * @throws UnsupportedEncodingException 
     */
	public HttpUriRequest putRequest(String resource, HttpEntity entity) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, UnsupportedEncodingException
	{
		HttpPut put = new HttpPut(urlEncode(resource, null));

		put.setEntity(entity);
		// fix contributed by Bjorn Roche
		put.getParams().setBooleanParameter( "http.protocol.expect-continue", false );

		return signRequest(put);
	}

    /**
     * Performs a POST request on a specified resource.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws OAuthCommunicationException 
     */
	public HttpResponse post(String resource) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
		return post(resource, (List<NameValuePair>) null);
	}

    /**
     * Performs a POST request on a specified resource, with parameters.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws OAuthCommunicationException 
     */
	public HttpResponse post(String resource, List<NameValuePair> params) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
        return httpClient.execute(postRequest(resource, params));
	}

    /**
     * Prepares a POST request on a specified resource, with parameters.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws OAuthCommunicationException 
     * @throws UnsupportedEncodingException 
     */
	public HttpUriRequest postRequest(String resource, List<NameValuePair> params) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, UnsupportedEncodingException
	{
		HttpPost post = new HttpPost(urlEncode(resource, params));

		// fix contributed by Bjorn Roche
		post.getParams().setBooleanParameter( "http.protocol.expect-continue", false );
		
		return signRequest(post);
	}
	
    /**
     * Performs a POST request on a specified resource, with an entity.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws OAuthCommunicationException 
     */
	public HttpResponse post(String resource, HttpEntity entity) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
        return httpClient.execute(postRequest(resource, entity));
	}
	
    /**
     * Prepares a POST request on a specified resource, with an entity.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws OAuthCommunicationException 
     * @throws UnsupportedEncodingException 
     */
	public HttpUriRequest postRequest(String resource, HttpEntity entity) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, UnsupportedEncodingException
	{
		HttpPost post = new HttpPost(urlEncode(resource, null));
		
		post.setEntity(entity);
		// fix contributed by Bjorn Roche
		post.getParams().setBooleanParameter( "http.protocol.expect-continue", false );

		return signRequest(post);
	}

    /**
     * Performs a DELETE request on a specified resource.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws OAuthCommunicationException 
     */
	public HttpResponse delete(String resource) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
		return delete(resource, null);
	}

    /**
     * Performs a DELETE request on a specified resource, with parameters.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws IOException 
     * @throws ClientProtocolException 
     * @throws OAuthCommunicationException 
     */
	public HttpResponse delete(String resource, List<NameValuePair> params) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
        return httpClient.execute(deleteRequest(resource, params));   
	}
	
    /**
     * Prepares a DELETE request on a specified resource, with parameters.
     * @throws OAuthExpectationFailedException 
     * @throws OAuthMessageSignerException 
     * @throws OAuthCommunicationException 
     */
	public HttpUriRequest deleteRequest(String resource, List<NameValuePair> params) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException
	{
		return signRequest(new HttpDelete(urlEncode(resource, params)));
	}
	
	/**
     * Uploads a file by performing a POST request on the "tracks" resource.
	 * @throws OAuthExpectationFailedException 
	 * @throws OAuthMessageSignerException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws OAuthCommunicationException 
     */
	public HttpResponse upload(File file, List<NameValuePair> params) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
		return upload(new FileBody(file), params);
	}

	/**
     * Uploads an arbitrary body by performing a POST request on the "tracks" resource.
	 * @throws OAuthExpectationFailedException 
	 * @throws OAuthMessageSignerException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws OAuthCommunicationException 
     */
	public HttpResponse upload(ContentBody fileBody, List<NameValuePair> params) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
		HttpPost post = new HttpPost(urlEncode("tracks", null));  
		// fix contributed by Bjorn Roche
		post.getParams().setBooleanParameter( "http.protocol.expect-continue", false );

		MultipartEntity entity = new MultipartEntity();
		for(NameValuePair pair : params)
		{
			try
			{
				entity.addPart(pair.getName(), new StringBodyNoHeaders(pair.getValue()));
			} catch (UnsupportedEncodingException e)
			{
			}  
		}
		entity.addPart("track[asset_data]", fileBody);  

		post.setEntity(entity);
		return performRequest(post);  
	}
	
	/**
     * Signs the provided URL.  Useful for stream URLs which you might need to access
     * using e.g., an audio player which won't do the signing for you. 
	 * @throws OAuthExpectationFailedException 
	 * @throws OAuthMessageSignerException 
	 * @throws OAuthCommunicationException 
     */
	public String signStreamUrl(String url) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException
	{
		if(mOptions.version == OAuthVersion.V2_0 && mState == State.AUTHORIZED)
			return url + (url.contains("?") ? "&" : "?") + "oauth_token=" + getToken();
		else
			return getConsumer().sign(url);
	}
	
	/**
     * Executes a GET request for the given URL, without following any redirects.
     * Useful for stream URLs which return a redirect.
	 * @throws OAuthExpectationFailedException 
	 * @throws OAuthMessageSignerException 
	 * @throws OAuthCommunicationException
	 * @throws ClientProtocolException
	 * @throws IOException
     */
	public HttpResponse getStreamRedirect(String url) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException, IOException
	{
		String signedResource = signStreamUrl(url);
		HttpGet get = new HttpGet(signedResource);
		get.getParams().setBooleanParameter("http.protocol.handle-redirects",false);
		return httpClient.execute(get);
	}

	/**
     * Utility function that returns the value of the Location header in a redirect
     * response.
     */
	public String parseRedirectResponse(HttpResponse response)
	{
		if(response.getStatusLine().getStatusCode() / 100 != 3)
			return null;
		else
		{
			for(Header header : response.getHeaders("Location"))
				return header.getValue();
			return null;
		}
	}
	
	/**
     * Returns a URL you can use to access a stream resource without any further need for signing.
     * If the GET request on the stream URL results in a redirect, this is equivalent to
     * parseRedirectResoinse(getStreamRedirect(resource).  If not, it is equivalent to
     * signStreamUrl(resource).
     * 
	 * @throws OAuthExpectationFailedException 
	 * @throws OAuthMessageSignerException 
	 * @throws OAuthCommunicationException
	 * @throws ClientProtocolException
	 * @throws IOException
     */
	public String getRedirectedStreamUrl(String resource) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException, ClientProtocolException, IOException
	{
		String redirect = parseRedirectResponse(getStreamRedirect(resource));
		return (redirect != null) ? redirect : signStreamUrl(resource);
	}

	public HttpResponse getStream(String resource) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
		return httpClient.execute(new HttpGet(signStreamUrl(resource)));
	}
	
	/**
     * Prepares and signs a request.
	 * @throws OAuthExpectationFailedException 
	 * @throws OAuthMessageSignerException 
	 * @throws OAuthCommunicationException 
     */
	private HttpUriRequest signRequest(HttpUriRequest request) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException 
	{
		if(mOptions.version == OAuthVersion.V2_0 && mState == State.AUTHORIZED)
			request.addHeader("Authorization", "OAuth " + getToken());
		else
			getConsumer().sign(request);
		return request;
	}
	
	/**
     * Signs and executes a request.
	 * @throws OAuthExpectationFailedException 
	 * @throws OAuthMessageSignerException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws OAuthCommunicationException 
     */
	public HttpResponse performRequest(HttpUriRequest request) throws OAuthMessageSignerException, OAuthExpectationFailedException, ClientProtocolException, IOException, OAuthCommunicationException
	{
		signRequest(request);
        return httpClient.execute(request);
	}
	
	private String urlEncode(String resource, List<NameValuePair> params)
	{
		String resourceUrl;
			if(resource.startsWith("/"))
				resourceUrl = mSoundCloudApiURL + resource.substring(1);
			else
				resourceUrl = resource.contains("://") ? resource : mSoundCloudApiURL + resource;
		return params == null ?
			resourceUrl :
			resourceUrl + "?" + URLEncodedUtils.format(params, "UTF-8");
	}
	
	private void closeQuietly(Closeable closeable)
	{
		try
		{
			if(closeable != null)
				closeable.close();
		}
		catch(Exception e)
		{
			System.err.println("Exception during Closeable close: " + e);
		}
	}
	
	private void closeQuietly(Socket socket)
	{
		try
		{
			if(socket != null)
				socket.close();
		}
		catch(Exception e)
		{
			System.err.println("Exception during Socket close: " + e);
		}
	}
	
	private void closeQuietly(ServerSocket server)
	{
		try
		{
			if(server != null)
				server.close();
		}
		catch(Exception e)
		{
			System.err.println("Exception during SocketServer close: " + e);
		}
	}
	
    /**
     * Returns the Request or Access Token.
     */
    public String getToken()
	{
		return getConsumer().getToken();
	}

    /**
     * Returns the Request or Access Token Secret.
     */
	public String getTokenSecret()
	{
		return getConsumer().getTokenSecret();
	}
	
    /**
     * Returns the current state of the API wrapper.
     */
	public State getState()
	{
		return mState;
	}
	
	protected OAuthConsumer getConsumer()
	{
		return mConsumer.get();
	}
	
    private State mState;
    
    InheritableOAuthConsumer mConsumer = new InheritableOAuthConsumer();
    OAuthProvider mProvider;
    volatile boolean mCancelAuthorization;
    
	String mSoundCloudURL, mSoundCloudApiURL;

	SoundCloudOptions mOptions;
	
	DefaultHttpClient httpClient;
        
    class InheritableOAuthConsumer extends InheritableThreadLocal<OAuthConsumer>
    {
    	protected OAuthConsumer childValue(OAuthConsumer parentValue)
    	{
            if(mState != State.AUTHORIZED)
            	return parentValue;
            
    		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(parentValue.getConsumerKey(), parentValue.getConsumerSecret());
    		consumer.setMessageSigner(new HmacSha1MessageSigner());
    		consumer.setTokenWithSecret(parentValue.getToken(), parentValue.getTokenSecret());
    		
    		return consumer;
    	}
    }
}



class StringBodyNoHeaders extends StringBody
{
	public StringBodyNoHeaders(String value) throws UnsupportedEncodingException
	{
		super(value);
	}	
	
	public String getMimeType()
	{
		return null;
	}

	public String getTransferEncoding()
	{
		return null;
	}	
}


