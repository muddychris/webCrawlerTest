package com.muddycottage.webCrawler;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawlerMain {

	public static void main(String[] args) {
		WebCrawlerMain webCrawlerMain = new WebCrawlerMain() ;

		webCrawlerMain.run () ;
	}

	private void run() {

		crawlUrl("http://www.mit.edu", null, "mit.edu");
		//crawlUrl("http://www.open.ac.uk", null, "open.ac.uk");
		//crawlUrl("https://www.hw.ac.uk", null, "hw.ac.uk") ;
	}

	/**
	 * crawlUrl - reads the content of the specified URL and reports on its link
	 * 
	 * Uses exceptions thrown when trying to parse tyhe URL's content as xml to determine the type of the link
	 * 
	 * @param url - url to be processed
	 * @param parentUrl - used to report the origin of this link
	 * @param homeSitePattern - used to filter out links to other sites
	 */
	void crawlUrl(String url, String parentUrl, String homeSitePattern) {
		
		// have we processed this URL already?
		
		if (isThisaNewUrl(url, homeSitePattern))
		{
			// provide minimal recovery from timeouts ...
			int retryCount = 3 ;

			while (retryCount > 0)
			{
				try
				{
					// connect to the URL
					Connection conn = Jsoup.connect(url) ;
					
					// attempt to parse the content as xml/html
					Document doc = conn.get();

					// we get here if the link is an xml/html page, so record it
					recordLink (url, parentUrl, "LINK") ;

					//get all links from the page and recursively crawl their pages
					Elements questions = doc.select("a[href]");
					for(Element link: questions){
						crawlUrl(link.attr("abs:href"), url, homeSitePattern);
					}

					// all done

					return ;
				}
				catch (SocketTimeoutException timeoutEx)
				{
					// decrement the retry count and try again
					retryCount-- ;
				}
				catch (UnsupportedMimeTypeException unSuppMimeEx)
				{
					// record but ignore non-xml pages (e.g. PDF documents)
					
					recordLink (url,parentUrl, "CONTENT") ;
					
					return ;
				}
				catch (MalformedURLException mlUrlEx)
				{
					// record ignore urls which are not http or https (e.g. mailto)
					
					recordLink (url,parentUrl, "OTHER") ;
					
					return ;
				}
				catch (HttpStatusException httpStatEx)
				{
					//ignore missing pages

					return ;
				}
				catch (Exception ex)
				{
					// crudely report other exceptions
					String outStr = String.format("URL [%s] EX [%s]",  url, ex.getMessage()) ;
					System.out.println(outStr);

					// ignore this page

					return ;
				}
			}
		}

	}

	/**
	 * recordLink - used to record a link and the type of its content
	 * 
	 * @param url - link being reported
	 * @param parentUrl - its parent
	 * @param type - description of type of link
	 */
	private void recordLink(String url, String parentUrl, String type) {
		System.out.print(type + " " + url);
		if (parentUrl != null)
			System.out.print(" PARENT " + parentUrl);
		
		System.out.println();
		
	}


	// a simple but effective caching system to prevent repeated processing of common links
	
	static Map<String, String> urlCache ;

	/**
	 * determines if the supplied URL has been processed before
	 * 
	 * @param url - url to be checked
	 * @param homeSitePattern - home site to check on/off links against
	 * 
	 * @return - true if the URL has not been processed before
	 */
	boolean isThisaNewUrl(String url, String homeSitePattern)
	{
		// ignore null or blank URLs

		if ( (url == null) || ("".equals(url.trim())))
			return false ;

		// ignore links away from the home site
		
		if ( ! url.contains(homeSitePattern))
			return false ;

		// do we have a cache? if not create one
		if (urlCache == null)
		{
			urlCache = new HashMap<String, String> () ;
		}
		
		// is this link already in the cache?

		if (urlCache.get(url) != null)
			return false ;

		// the supplied url is not in the cache, so add it

		urlCache.put(url, url) ;

		// return true to permit processing of this link
		return true ;
	}
}
