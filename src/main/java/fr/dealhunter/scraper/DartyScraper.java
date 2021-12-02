package fr.dealhunter.scraper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import business.data.Laptop;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DartyScraper {

	public static void main(String[] args) throws InterruptedException, IOException {
		/*
		 * for (String string : getLinkOfLaptop(
		 * "https://www.darty.com/nav/achat/informatique/ordinateur_portable/page1.html"
		 * )) { System.out.println(string); } // System.err.println(getLinkOfLaptop(
		 * "https://www.darty.com/nav/achat/informatique/ordinateur_portable/page2.html"
		 * ).size());
		 * 
		 */
		// readFile(3230);

		// getLaptopInfo("https://www.darty.com/nav/achat/informatique/macbook_imac_ipad/macbook/apple_newmba13_m1_8_256_gs.html");
		writeInfo(3142);
	}

	public static ArrayList<String> getLinkOfLaptop(String pagelink) throws InterruptedException {
		String laptopLink = "https://www.darty.com";
		ArrayList<String> links = new ArrayList<>();
		for (int i = 68; i < Integer.MAX_VALUE; i++) {
			String url = "https://www.darty.com/nav/achat/informatique/ordinateur_portable/page" + i + ".html";
			// System.err.println(url);
			// if (i%2==0) {
			int rand1 = getRandom(50000, 90000);

			Thread.sleep(rand1);
			System.err.println(rand1);
			// }
			try {

				Document doc = getDoc(i, url);
				if (doc.getAllElements().toString()
						.contains("ATTENTION : la page demandée n'existe pas ou n'est plus disponible.")) {
					System.err.println("page not working i is" + i);
					break;
				}
				System.out.println(doc.getAllElements());
				Elements elements = doc.getElementsByClass("next_prev");
				for (Element element : elements) {
					String link = laptopLink + element.attr("href").trim();
					if (!links.contains(link)) {
						BufferedWriter output = new BufferedWriter(new FileWriter("../out.txt", true));
						output.newLine();
						output.write(link);
						output.close();
						links.add(link);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			System.err.println("size of array is" + links.size());
			int rand2 = getRandom(30000, 40000);
			Thread.sleep(rand2);
		}
		return links;

	}

	public static Document getDoc(int i, String url) {
		Document doc = null;
		String agent1 = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:81.0) Gecko/20100101 Firefox/81.0";
		String agent2 = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
		try {
			if (i % 2 == 0) {
				doc = Jsoup.connect(url).userAgent(agent1).referrer("http://www.google.fr").ignoreHttpErrors(true)
						.ignoreContentType(true).timeout(10000).get();
			} else {
				doc = Jsoup.connect(url).userAgent(agent1).referrer("http://www.google.fr").ignoreHttpErrors(true)
						.ignoreContentType(true).timeout(10000).get();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return doc;

	}

	public static int getRandom(int min, int max) {

		Random rand = new Random();

		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;

	}

	public static ArrayList<String> readFile(int startLine) throws IOException {
		BufferedReader br = null;
		ArrayList<String> linkLaptop = new ArrayList<>();
		int lineNumber = 1;
		br = new BufferedReader(new FileReader(
				"C:\\Users\\TAREK\\Documents\\workspace-spring-tool-suite-4-4.9.0.RELEASE\\DealHunter\\out.txt"));
		String line = null;
		while ((line = br.readLine()) != null) {
			if (lineNumber >= startLine) {
				linkLaptop.add(line);
			}
			lineNumber++;
		}
		br.close();
//		for (String ss : linkLaptop) {
//			System.out.println(ss);
//		}
		return linkLaptop;
	}

	public static Document getLaptopHtml(String link, int val) {
		Document doc = null;
		if (val % 2 == 0) {
			try {
				String agent1 = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:81.0) Gecko/20100101 Firefox/81.0";
				//String agent1 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36";
				//String agent1 = "Mozilla/5.0 (Android 7.1.1; Mobile; rv:93.0) Gecko/93.0 Firefox/93.0";
				//String agent1 = "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0; MDDCJS)";
				doc = Jsoup.connect(link).userAgent(agent1).referrer("https://www.google.fr").ignoreHttpErrors(true)
						.ignoreContentType(true).get();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			String agent2 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:94.0) Gecko/20100101 Firefox/94.0";
			//String agent2 = "Mozilla/5.0 (Linux; Android 7.1.1; E6653) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.74 Mobile Safari/537.36";
			//String agent2 = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)";
			//String agent2 = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Safari/537.36";
			try {
				doc = Jsoup.connect(link).userAgent(agent2).referrer("https://www.google.ma").ignoreHttpErrors(true)
						.ignoreContentType(true).get();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return doc;

	}

	public static String getLaptopInfo(String linkLaptop, int val) throws InterruptedException {

		Document doc = getLaptopHtml(linkLaptop, val);

		if (doc.getAllElements().text().toLowerCase().matches(".*enable js.*")) {
			log.error("my bot is not working");
			log.error("exit on the link : " + linkLaptop);
			System.exit(0);
		}
		int rand1 = getRandom(10000, 50000);
		Thread.sleep(rand1);
		Elements elements = doc.getElementsByTag("tbody");
		Laptop laptop = new Laptop();
		String cpu = "";
		String gpu = "";
		String ram = "";
		String resolution = "";
		String weight = "";
		String reference = "";
		String screenSize = "";
		String stockage = "";
		String os = "";
		String name = doc.getElementsByClass("product_name font-2-b").text();
		String price = doc.getElementsByClass("product-price__price ").text();
		Element img = doc.select("img[itemprop=image]").first();
		String imageUrl = img.attr("src");
		laptop.setImageUri(imageUrl);
		laptop.setPrice(price);
		laptop.setSource("Darty");
		laptop.setName(name);
		laptop.setUri(linkLaptop);
		laptop.setTitle(name);
		if (name.contains("Apple") || name.contains("MacBook")) {
			// System.out.println("dkhelt hna tarek");
			laptop.setOperatingSystem("MacOS");

		}

		for (Element element : elements) {
			Elements trs = element.getElementsByTag("tr");
			for (Element e : trs) {
				String th = e.select("tr > th").text();
				// System.out.println(th);

				/*
				 * if (th.contains("Type de disque dur")) { String type =
				 * e.select("tr > td > ul > li").text(); System.out.println("dkhelt hna 1"); if
				 * (type.matches(".*SSD.*")) { ssd = true; System.out.println("dkhelt hna 2"); }
				 * if (type.matches(".*HDD.*")) { hdd = true;
				 * System.out.println("dkhelt hna 5"); } }
				 */

				if (th.matches(".*Modèle du processeur.*")) {
					cpu += " " + e.select("tr > td > ul > li").text();
					laptop.setCpu(cpu);
				} else if (th.matches(".*carte graphique.*")) {
					gpu += " " + e.select("tr > td > ul > li").text();
					laptop.setGpu(gpu);
				} else if (th.matches(".*Caractéristiques de l'écran.*")) {
					resolution += " " + e.select("tr > td > ul > li").text();
					laptop.setScreenResolution(resolution);
				} else if (th.matches(".*Résolution.*")) {
					resolution += " " + e.select("tr > td > ul > li").text();
					laptop.setScreenResolution(resolution);
				} else if (th.matches(".*RAM.*")) {
					ram += " " + e.select("tr > td > ul > li").text();
					laptop.setRam(ram);
				} else if (th.matches(".*Poids.*")) {
					weight = e.select("tr > td > ul > li").text();
					laptop.setWeight(weight);
				} else if (th.matches(".*Code.*")) {
					reference = e.select("tr > td > ul > li").text();
					laptop.setReference(reference);
				} else if (th.matches(".*Taille de l'écran.*")) {
					screenSize = e.select("tr > td > ul > li").text();
					laptop.setScreenSize(screenSize);
				} else if (th.matches(".*Système d'exploitation.*")) {
					os = e.select("tr > td > ul > li").text();
					laptop.setOperatingSystem(os);
				}

				if (th.matches(".*Capacité de stockage SSD.*")) {
					stockage += " ssd " + e.select("tr > td > ul > li").text();
					laptop.setStorage(stockage);
				}
				if (th.matches(".*Capacité de disque dur.*")) {
					stockage += " hdd " + e.select("tr > td > ul > li").text();
					laptop.setStorage(stockage);
				}

			}
		}

		return laptop.toString();

	}

	public static void writeInfo(int startLine) throws IOException, InterruptedException {

		ArrayList<String> linesLink = readFile(startLine);
		for (int i = 1; i <= linesLink.size(); i++) {
			try {
				log.info("am on the line : " + i + " url : " + linesLink.get(i));
				String lapTopInfo = getLaptopInfo(linesLink.get(i), i);
				BufferedWriter output = new BufferedWriter(new FileWriter("../sql.txt", true));
				output.newLine();
				output.write(lapTopInfo);
				output.close();

				log.info("am on the line : " + i + " url : " + linesLink.get(i));
			} catch (NullPointerException e) {
				log.error(
						"??????? ???? ??? i have probleme with this line: " + i + " on the url : " + linesLink.get(i));
			}

		}
		log.info("am done");

	}

}

/*
 * if (th.matches(".*stockage SSD.*")) { System.out.println("dkhelt hna 3");
 * stockage += " ssd " + e.select("tr > td > ul > li").text();
 * laptop.setStorage(stockage); System.out.println("dkhelt hna 4"); }
 * 
 */