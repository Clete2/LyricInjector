package com.clete2.LyricInjector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class LyricWiki {
	/**
	 * Class to connect to LyricWiki and get lyrics.
	 */
	// TODO: Harden error handling
	// TODO: JAR cleanup (Apache Commons)
	// TODO: Use HTTPS
	HttpClient httpClient;

	public LyricWiki() {
		super();
		this.httpClient = new DefaultHttpClient();
	}

	/**
	 * Returns lyrics given an artist name and a song name.
	 * @param artistName - Name of artist
	 * @param songName - Name of song by given artist
	 * @return - String of lyrics. An empty String if no lyrics are found.
	 * @throws UnexpectedDataException - Given a bad URL or bad characters or other issue.
	 */
	public String getLyrics(String artistName, String songName) throws UnexpectedDataException {
		String lyrics = "";

		// TODO: Sanitize string
		try {
			URI xmlURI = new URI("http", "lyrics.wikia.com", "/api.php", 
					"artist="+ artistName +"&song="+ songName +"&fmt=xml", null);
			InputStream xmlStream = this.getInputStream(xmlURI.toURL().toString());
			String fullLyricsURL = this.getLyricURLFromXML(xmlStream);
			lyrics = this.getLyricsFromURL(fullLyricsURL);
		} catch (URISyntaxException e) {
			throw new UnexpectedDataException();
		} catch (MalformedURLException e) {
			throw new UnexpectedDataException();
		}

		return lyrics;
	}

	/**
	 * 
	 */
	public String getLyricsFromURL(String url) {
		InputStream lyricsPageInputStream = this.getInputStream(url);
		BufferedReader br = new BufferedReader(new InputStreamReader(lyricsPageInputStream));
		StringBuilder sb = new StringBuilder();
		String line = "";
		try {
			while((line = br.readLine()) != null) {
				sb.append(line +"\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO: Throw exception if sb is empty
		Pattern lyricsPattern = 
				Pattern.compile("&lt;lyrics\\>(.*?)&lt;/lyrics\\>",
						Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher lyricsMatcher = lyricsPattern.matcher(sb);
		lyricsMatcher.find();
		String lyrics = "";
		try {
			lyrics = lyricsMatcher.group(1);
		} catch (IllegalStateException e) {
			// No match found
		}
		
		return lyrics;
	}

	/**
	 * Gets the URL to the full lyrics page from LyricWiki.
	 * @param xmlStream - XML InputStream of LyricsWiki XML
	 * @return
	 */
	private String getLyricURLFromXML(InputStream xmlStream) {
		return this.getXPathResult(xmlStream, "/LyricsResult/url/text()") +"?action=edit";
	}

	/**
	 * Gets an InputStream from a URL.
	 * @param url
	 * @return
	 */
	private InputStream getInputStream(String url) {
		// TODO: Sanitize strings
		InputStream is = null;

		HttpGet lyricsGet = new HttpGet(url);

		try {
			HttpResponse response = this.httpClient.execute(lyricsGet);
			is = response.getEntity().getContent();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return is;
	}

	/**
	 * Gets an XPath statement result as a String given an InputStream and an XPath statement.
	 * @param xmlStream - XML InputStream
	 * @param xPathStatement - XPath statement
	 * @return String result of the XPath statement executed on the XML InputStream
	 */
	private String getXPathResult(InputStream xmlStream, String xPathStatement) {
		String result = "";

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlStream);
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xPath = xPathFactory.newXPath();
			XPathExpression lyricExpression = xPath.compile(xPathStatement);
			result = lyricExpression.evaluate(doc);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
}
