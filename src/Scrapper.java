import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Scrapper 
{
	
	static int MaxArticleCount = 200;
	static  PrintWriter  outArticleKeyword = null;
	static Queue<String> q = null;
	static HashMap<String, Integer> hmap = null;
	static int articleCount = 0;
	static String charset = "UTF-8";
	static PrintWriter outUrls = null;
	static PrintWriter outArticleTitle = null;
	
	
	public static void main2(String [] a)
	{
		
		String str = "<p><div class=\"headline-kicker\"><h1>The only person Sam Smith didn’t thank at the Grammys deserves a lot of credit</h1><div class=\"clear\"></div></p>";
		Document doc = Jsoup.parse(str);
		
		Elements classs = doc.getElementsByClass("headline-kicker");
		System.out.println(classs.get(0).getElementsByTag("h1").get(0).text());
				
	}
	public static void main(String [] a) throws IOException, InterruptedException
	{
		intialise();
		run();
		clear();
		
	}
	private static void clear() 
	{
		// TODO Auto-generated method stub
		outArticleKeyword.close();
		outUrls.close();
		outArticleTitle.close();
	}
	private static void run() throws IOException, InterruptedException
	{
	
		String urlString = q.poll();
		while(articleCount < MaxArticleCount  && urlString != null)
		{
			if(!hmap.containsKey(urlString))
			{
				
				Document doc = getDocument(urlString);
				if(doc != null)
				{

					handleLinks(doc);
					
					String foundBody = handleArticleBody(doc);
					
					String foundMeta = handleMetadata(doc);
					
					//String foundTitle = handleTitle(doc);
					
					if(!foundBody.equals("") && !foundMeta.equals("") && isValidMetaData(foundMeta) )
					{
						
						System.out.println(articleCount +" body and meta writter for "+ urlString );
						//System.out.println(articleCount + " metadata : " + foundMeta);
						
						outputArticleBody(foundBody);
						outputArticleKeyword(foundMeta);
						outArticleUrls(urlString);
						
						articleCount++;
					}
					else if(foundBody.equals("") && foundMeta.equals(""))
					{
						System.out.println("found no article and metadata nothing for " + urlString);
					}	
					else if(isValidMetaData(foundMeta) == false)
					{
						System.out.println("invalid metadata for url : " + urlString);
					}
					else
					{
						//error indicates that found either foundbody or foundmeta.
						System.out.println("ERROR for url " + urlString);
					}
				}
				else
				{
					System.out.println("no xml document found for " + urlString);
				}
				hmap.put(urlString, 1);
				
			}
			else
			{
				System.out.println("Already visited");
			}
		
			urlString = q.poll();
			
			
			
		}
		
	}
	private static void outArticleTitle(String foundTitle) 
	{
		outArticleTitle.println(articleCount);
		outArticleTitle.println(foundTitle);
	}
	
	private static String handleTitle(Document doc) 
	{
		String retVal = "";
		Elements titles = doc.getElementsByClass("headline-kicker");
		if(titles.size() != 0)
		{
			Elements titlesH1 = titles.get(0).getElementsByTag("h1");
			if(titlesH1.size() != 0)
			{
				retVal = titlesH1.get(0).text(); 
			}
		}
		
		return retVal;
	}
	private static boolean isValidMetaData(String foundMeta)
	{
		boolean retVal = false;
		if(!foundMeta.equals("Post Keywords") && !foundMeta.equals("Washington Post terms of service") && !foundMeta.equals("Washington Post privacy policy") && !foundMeta.equals("advertising cookies") && !foundMeta.equals("plum line"))
		{
			retVal = true;
		}
		
		
		return retVal;
	}
	private static void handleLinks(Document doc) 
	{
		Elements links = doc.getElementsByTag("a");
		if(links != null)
		{
			for (Element link1 : links)
			{
				String href1 = link1.attr("href");
				if(href1 != null )	
				{
					if(href1.startsWith("http://www.washingtonpost.com") && href1.endsWith("/"))
					{
						q.add(href1);
			//			System.out.println(href1);
					}
						
					if(href1.startsWith("/") && href1.endsWith("/"))
					{
						q.add("http://www.washingtonpost.com"+href1);
				//		System.out.println(href1);
					}
					
				}
			}
		}
		
		

	}

	private static String handleMetadata(Document doc) 
	{
	
		String retVal  = "";
		
		Elements metas = doc.select("meta[name=news_keywords]");
		
		if(metas != null)
		{

			String keyWords = "";
			
			for(Element meta : metas)
			{
				keyWords = meta.attr("content");
			}
			
			if(!keyWords.equals(""))
			{
				//outputArticleKeyword(keyWords);
				retVal = keyWords;
			
			}
		}		
		return retVal;

	}
	
	private static String handleArticleBody(Document doc) throws FileNotFoundException 
	{
		String retVal = "";
		Element content = doc.getElementById("article-body");
		
		if(content != null)
		{
			Elements paras = content.select("p");
			
			if(paras != null)
			{
				String articleBody = "";
				
				for (Element para : paras) 
				{
					String paraText = para.text();
					paraText = paraText.replaceAll("[^\\x20-\\x7e]", "");
					articleBody = articleBody + paraText + " ";
				}
				
				if(!articleBody.equals(""))
				{
					//outputArticleBody(articleBody);
					retVal = articleBody;
				}
			}
			
		}
		
		return removesAttributesFromArticleBody(retVal);
	}
	private static String removesAttributesFromArticleBody(String rawArticleBody) 
	{
	      
		String retVal = "";
	      Document doc = Jsoup.parse("<p>"+rawArticleBody + "</p>");
	      
	      
	      doc.getElementsByTag("a").remove();
	      doc.getElementsByTag("img").remove();
	      doc.getElementsByTag("iframe").remove();
	      
	      Elements paras = doc.getElementsByTag("p");
	      for(Element para : paras)
	      {
	    	  String paraText = para.text();
	    	  retVal += paraText;
	      }
	      
	   
	
		return retVal;
	}
	static Document getDocument(String urlString) throws IOException, InterruptedException   
	{
		Thread.sleep(1000);
		Document retVal = null;
		URL url = new URL(urlString);
		
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("192.168.18.10", 10786)); 
		//Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("172.31.16.10", 8080));
		HttpURLConnection uc = (HttpURLConnection)url.openConnection(proxy);

		uc.connect();
		  
		String line = null;
		StringBuffer tmp = new StringBuffer();
		BufferedReader in;
		try 
		{
			in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			while ((line = in.readLine()) != null) 
			{
				tmp.append(line);
			}
			retVal = Jsoup.parse(String.valueOf(tmp));
		
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	private static void intialise() throws FileNotFoundException 
	{
		outArticleKeyword = new PrintWriter(new File("C:\\Users\\kshitij\\workspace\\Scrapper\\try\\keywords.txt"));
		outUrls = new PrintWriter(new File("C:\\Users\\kshitij\\workspace\\Scrapper\\try\\urls.txt"));
		outArticleTitle = new PrintWriter(new File("C:\\Users\\kshitij\\workspace\\Scrapper\\try\\titles.txt"));
		q = new LinkedList();
		hmap = new HashMap();
		//String urlString = "http://www.washingtonpost.com/";
		String urlString = "http://www.washingtonpost.com/blogs/style-blog/wp/2015/02/09/the-only-person-sam-smith-didnt-thank-at-the-grammys-deserves-a-lot-of-credit/?tid=hybrid_sidebar_alt1_strip_1";
		
		q.add(urlString);
	}
	
	private static void outputArticleKeyword(String attr) 
	{
		
		outArticleKeyword.println(articleCount);
		outArticleKeyword.println(attr);
		//System.out.println("writing keyword : " + attr +" for article : " + articleCount);
		
	}
	
	private static void outArticleUrls(String url)
	{
		outUrls.println(articleCount);
		outUrls.println(url);
		
	}
	private static void outputArticleBody(String articleBody) throws FileNotFoundException 
	{
		PrintWriter  outArticle = new PrintWriter(new File("C:\\Users\\kshitij\\workspace\\Scrapper\\try\\" + String.valueOf(articleCount) + ".txt"));
		outArticle.print(articleBody);
		outArticle.close();
	}

}
