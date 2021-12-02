package tests;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestScrape {

	public static void main(String[] args) {
		try {
			Document doc = Jsoup.connect("https://www.amazon.fr/s?rh=n%3A429879031&fs=true&ref=lp_429879031_sar")
				     .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:81.0) Gecko/20100101 Firefox/81.0")
				     .referrer("https://www.google.fr/")
				     .ignoreHttpErrors(true)
				     .ignoreContentType(true)
				     .timeout(10000)
				     .get();
			
			Elements allLinks = doc.getElementsByClass("a-link-normal a-text-normal");
			int count = 0;
			for(Element link : allLinks) {
				if(!link.attr("href").startsWith("/gp/")) {
					count++;
					System.out.println(link.attr("href"));
				}
			}
			System.out.println(count);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
