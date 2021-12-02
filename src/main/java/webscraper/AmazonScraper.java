package webscraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import business.data.Laptop;

public class AmazonScraper {
	
	final String baseUrl = "https://www.amazon.fr";

	public static void main(String[] args) {
		try {
			String nextPageUri = "https://www.amazon.fr/s?i=computers&rh=n%3A429879031&fs=true&qid=1638120348";
//			String nextPageUri = "https://www.amazon.fr/Lenovo-IdeaPad-Ordinateur-Portable-Graphics/dp/B089N794WT/ref=sr_1_77?qid=1638120348&s=computers&sr=1-77&th=1";
			
//			Document doc = Jsoup.connect(laptopPage)
//				     .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:81.0) Gecko/20100101 Firefox/81.0")
//				     .referrer("https://www.google.fr/")
//				     .ignoreHttpErrors(true)
//				     .ignoreContentType(true)
//				     .maxBodySize(0)
//				     .timeout(10000)
//				     .get();
			
			AmazonScraper as = new AmazonScraper();
			
			while(nextPageUri != null) {
				Document doc = Jsoup.connect(nextPageUri)
					     .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36")
					     .referrer("https://www.google.fr/")
					     .ignoreHttpErrors(true)
					     .ignoreContentType(true)
					     .maxBodySize(0)
					     .timeout(10000)
					     .get();
				
				nextPageUri = as.getNextPageUrl(doc);
				for(String link : as.getAllLinks(doc)) {
					Document newDoc = Jsoup.connect(link)
						     .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36")
						     .referrer("https://www.google.fr/")
						     .ignoreHttpErrors(true)
						     .ignoreContentType(true)
						     .maxBodySize(0)
						     .timeout(10000)
						     .get();
					System.out.println("#######################################################################################################################");
					extractLaptopInfo(newDoc);
				}
			}
//			nextPageUri = as.getNextPageUrl(doc);
//			System.out.println(as.getNextPageUrl(doc));
//			System.out.println(as.getAllLinks(doc));
//			as.extractLaptopInfo(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void extractLaptopInfo(Document doc) {
		String screenSize, screenResolution, cpu, storage, ram, os, weight, gpu;
		screenSize = screenResolution = cpu = storage = ram = os = weight = gpu = null;
		
		
		Element htmlCharacteristics = getLaptopHtmlCharacteristics(doc);
		if(htmlCharacteristics != null) {
			Elements characteristics = htmlCharacteristics.getElementsByTag("tr");
			for(Element characteristic : characteristics) {
				String sectionName = characteristic.getElementsByTag("th").first().text().trim();
				String content = characteristic.getElementsByTag("td").first().text().trim();
				System.out.println(sectionName + content);
			}
		}		
	}
	
	private static Element getLaptopHtmlCharacteristics(Document doc) {
		return doc.getElementById("productDetails_techSpec_section_1");
	}
	
	private List<String> getAllLinks(Document doc) {
		List<String> pageLinks = new ArrayList<String>();
		
		Elements allLinks = doc.getElementsByClass("a-link-normal a-text-normal");
		for(Element link : allLinks) {
			String hrefValue = link.attr("href").trim();
			if(!hrefValue.startsWith("/gp/")) {
				pageLinks.add(baseUrl + link.attr("href"));
			}
		}
		
		return pageLinks;
	}
	
	private String getNextPageUrl(Document doc) {
		String nextPage = null;
		
		Elements nextPageElement = doc.select(".a-pagination > .a-last > a");
		if(nextPageElement.size() > 0) {
			nextPage = baseUrl + nextPageElement.attr("href");
		}
		
		return nextPage;
	}
}
