package kr.ac.kit.util;

/*
 * DictationHTTPClient.java
 *
 * This is a simple command-line java app that shows how to use the NMDP HTTP Client Interface for
 *	Dictation and WebSearch requests using the POST method
 *
 * This basic java app will:
 *	1. Create an instance of an HttpClient to interact with our HTTP Client Interface for TTS
 *	2. Use some simple helper methods to setup the URI and HTTP POST parameters
 *	3. Execute the HTTP Request, passing streamed audio from file to the interface
 *	4. Process the HTTP Response, writing the results to the console
 *
 *	Output of progress of the request is logged to console
 *	Values to be passed to the HTTP Client Interface are simply hard-coded class members for demo purposes
 *
 * @copyright  Copyright (c) 2010 Nuance Communications, inc. (http://www.nuance.com)
 *
 * @Created	: June 6, 2011
 * @Author	: Peter Freshman
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.xiph.speex.util.streamer.FileAudioStreamer;

import android.os.AsyncTask;
import android.util.Log;
import kr.ac.kit.listener.RecognizeResponseListener;

@SuppressWarnings("deprecation")
public class AsyncDictationHTTPClient extends AsyncTask<String, Void, String>
{

	/*
	 **********************************************************************************************************
	 * Client Interface Parameters:
	 *
	 * appId: 		You received this by email when you registered
	 * appKey:	 	You received this as a 64-byte Hex array when you registered.
	 * 				If you provide us with your username, we can convert this to a 128-byte string for you.
	 * id: 			Device Id is any character string. Typically a mobile device Id, but for test purposes, use the default value
	 * Language:	The language code to use.
	 *
	 *				Please refer to the FAQ document available at the Nuance Mobile Developer website for a detailed list
	 *				of available languages (http://dragonmobile.nuancemobiledeveloper.com/faq.php)
	 *
	 * codec:		The desired audio format. The supported codecs are:
	 *
	 *					audio/x-wav;codec=pcm;bit=16;rate=8000
	 *					audio/x-wav;codec=pcm;bit=16;rate=11025
	 * 					audio/x-wav;codec=pcm;bit=16;rate=16000
	 *					audio/x-wav
	 *					speex_nb', 'audio/x-speex;rate=8000
	 *					speex_wb', 'audio/x-speex;rate=16000
	 *					audio/amr
	 *					audio/qcelp
 	 *					audio/evrc
 	 *
	 * Language Model:	The language model to be used for speech-to-text conversion. Supported values are
	 * 					Dictation and WebSearch
	 * 
	 * Results Format: The format the results she be returned as. Supported values are text/plan and application/xml.
	 * 					Currently, application/xml is ignored and will return results as text/plain. However, the next
	 * 					release of Network Speech Services will support results returned in xml format.
	 *
	 *********************************************************************************************************
	 */
	private String APP_ID = "NMDPTRIAL_gloriajean81320150326023815";
	private String APP_KEY = "acda570b9c32f9033d6521b1d56d9dc7c8e387a1e9dca094e657d759e38b0690665e9b02909406cdf9277fce941c955dff1cfee8f89e7cdef55338d9df3e6945";
	private String DEVICE_ID = "kitd445";
	private String LANGUAGE = "ko_KR";
	private String CODEC = "audio/amr";
	private String LM = "Dictation";	// or WebSearch
	private String RESULTS_FORMAT = "text/plain";	// or application/xml
	
	private RecognizeResponseListener listener;
	

	/*********************************************************************************************************
	 *
	 * HTTP Client Interface URI parameters
	 *
	 * PORT:		To access this interface, port 443 is required
	 * HOSTNAME:	DNS address is dictation.nuancemobility.net
	 * SERVLET:		Dictation Servlet Resource
	 *
	 *********************************************************************************************************
	 */
	private static short PORT = (short) 443;
	private static String HOSTNAME = "dictation.nuancemobility.net";
	private static String SERVLET = "/NMDPAsrCmdServlet/dictation";

	private static String ADD_CONTEXT = "/NMDPAsrCmdServlet/addContext";

	private static String cookie = null;

	/*
	 * HttpClient member to handle the Dictation request/response
	 */
	private boolean encodeToSpeex = false;

	private String sampleRate = "8000";
	private boolean isStreamed = false;
	private long fileSize = 0L;
	
	public AsyncDictationHTTPClient(RecognizeResponseListener listener)
	{
		this.listener = listener;
	}
	

	/*
	 * This function will initialize httpclient, set some basic HTTP parameters (version, UTF),
	 *	and setup SSL settings for communication between the httpclient and our Nuance servers
	 */
	private HttpClient getHttpClient() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException, CertificateException, IOException
	{
		// Standard HTTP parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUseExpectContinue(params, false);
		
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);

        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", sf, 443));

        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

        return new DefaultHttpClient(ccm, params);
	}

	/*
	 * This is a simple helper function to setup the query parameters. We need the following:
	 *	1. appId
	 *	2. appKey
	 *	3. id
	 *
	 *	If your query fails, please be sure to review carefully what you are passing in for these
	 *	name/value pairs. Misspelled names, and invalid AppKey values are a VERY common mistake.
	 */
	private List<NameValuePair> setParams()
	{
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();

		qparams.add(new BasicNameValuePair("appId", APP_ID));
		qparams.add(new BasicNameValuePair("appKey", APP_KEY));
		qparams.add(new BasicNameValuePair("id",  DEVICE_ID));

		return qparams;
	}

	/*
	 * This is a simple helper function to create the URI.
	 */
	private URI getURI() throws Exception
	{
		// Get the standard set of parameters to be passed in...
		List<NameValuePair> qparams = this.setParams();

		URI uri = URIUtils.createURI("https", HOSTNAME, PORT, SERVLET, URLEncodedUtils.format(qparams, "UTF-8"), null);

		return uri;
	}

	/*
	 * This is a simpler helper function to setup the Header parameters
	 */
	private HttpPost getHeader(URI uri, long contentLength) throws UnsupportedEncodingException
	{
		HttpPost httppost = new HttpPost(uri);
		
		if( contentLength == 0 )
			;	//httppost.setHeader("Transfer-Encoding", "chunked");	//httppost.addHeader("Transfer-Encoding", "chunked");
		else
			;	//httppost.setHeader("Content-Length", Long.toString(contentLength));	//httppost.addHeader("Content-Length", Long.toString(contentLength));
		
		httppost.addHeader("Content-Type",  CODEC);
		httppost.addHeader("Content-Language", LANGUAGE);
		httppost.addHeader("Accept-Language", LANGUAGE);
		httppost.addHeader("Accept", RESULTS_FORMAT);
		httppost.addHeader("Accept-Topic", LM);

		return httppost;
	}
	
	private InputStreamEntity setAudioContent(String audioFile) throws NumberFormatException, Exception
	{
		File f = new File(audioFile);
		if( !f.exists() )
		{
			System.out.println("Audio file does not exist: " + audioFile);
			return null;
		}
		if( !isStreamed )
		{
			fileSize = f.length();
		}
		
		FileAudioStreamer fs = new FileAudioStreamer(audioFile, isStreamed , encodeToSpeex, Integer.parseInt(sampleRate));
		InputStreamEntity reqEntity  = new InputStreamEntity(fs.getInputStream(), -1);
		fs.start();

		reqEntity.setContentType(CODEC);

		//reqEntity.setChunked(true);
		
		return reqEntity;
	}
	
	private String getSentence(HttpResponse response) throws IllegalStateException, IOException
	{
		String sentence = null;
		HttpEntity resEntity = response.getEntity();

		System.out.println(response.getStatusLine());
		if (resEntity != null) {
			Log.i("getContents", "Response content length: " + resEntity.getContentLength());
			Log.i("getContents", "Chunked?: " + resEntity.isChunked());
			Log.i("getContents", "Nuance Session Id: " + response.getFirstHeader("x-nuance-sessionid").getValue());

			if(cookie == null){
				Header cookieHeader = response.getFirstHeader("Set-Cookie");
				cookie = cookieHeader.getValue();
				StringTokenizer st = new StringTokenizer(cookie,";");
				cookie = st.nextToken().trim();
				Log.i("getContents", "Cookie: " + cookie);
			} 
			Log.i("getContents", "------------------Result----------------------");
			
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(resEntity.getContent()));
			try {

				sentence = reader.readLine();
				Log.i("getContents", "인식된문장 : " + sentence);
				
			} catch (Exception ex) {

			} finally {

				// Closing the input stream will trigger connection release
				reader.close();

			}
			resEntity.consumeContent();
		}
		return sentence;
	}
	
	
	private void processResponse(HttpResponse response) throws IllegalStateException, IOException
	{
		HttpEntity resEntity = response.getEntity();
		
		System.out.println(response.getStatusLine());
		if (resEntity != null) {
			System.out.println("Response content length: " + resEntity.getContentLength());
			System.out.println("Chunked?: " + resEntity.isChunked());
			System.out.println("Nuance Session Id: " + response.getFirstHeader("x-nuance-sessionid").getValue());

			if(cookie == null){
				Header cookieHeader = response.getFirstHeader("Set-Cookie");
				cookie = cookieHeader.getValue();
				StringTokenizer st = new StringTokenizer(cookie,";");
				cookie = st.nextToken().trim();
				System.out.println("Cookie: " + cookie);
			} 
			System.out.println("------------------Result----------------------");
			
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(resEntity.getContent()));
			try {

				String sentence;
				int index = 1;
				while((sentence = reader.readLine()) != null){
					// do something useful with the response
					System.out.println("sentence #" + index++ + " : " + sentence);
				}
			} catch (IOException ex) {

				// In case of an IOException the connection will be released
				// back to the connection manager automatically
				throw ex;

			} catch (RuntimeException ex) {

				// In case of an unexpected exception you may want to abort
				// the HTTP request in order to shut down the underlying 
				// connection and release it back to the connection manager.
				//httppost.abort();
				throw ex;

			} finally {

				// Closing the input stream will trigger connection release
				reader.close();

			}
			resEntity.consumeContent();
		}
	}
	
	/*****************************************************
	 * 여기부터는 AsyncTask의 메소드들
	 *****************************************************/

	@Override
	protected void onPreExecute(){}

	@Override
	protected String doInBackground(String... param)
	{
		try
		{
			HttpClient httpclient = getHttpClient();
			InputStreamEntity reqEntity = setAudioContent(param[0]);

			URI uri = getURI();
			HttpPost httppost = getHeader(uri, 0);	//fileSize);
			httppost.setEntity(reqEntity);			
			
			Log.i("onPreExecute()", "----------------- Send Audio ----------------------");
			Log.i("onPreExecute()", "executing request " + httppost.getRequestLine());
			
			HttpResponse response = httpclient.execute(httppost);
			
			String sentence = getSentence(response);
			httpclient.getConnectionManager().shutdown();
			
			return sentence;
		}
		catch (Exception e){e.printStackTrace(); return null;}
	}

	@Override
	protected void onPostExecute(String sentence)
	{
		listener.onTaskCompleted(sentence);
	}
}
