package webscraper;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import business.data.Laptop;
import me.tongfei.progressbar.ProgressBar;

public class TopAchatScraper {

	public static void main(String[] args) {
		String laptopResultsPage = "https://www.topachat.com/pages/produits_cat_est_ordinateurs_puis_rubrique_est_wport_puis_page_est_1.html";
		ArrayList<Laptop> laptops = new ArrayList<Laptop>();
						
		int lastPage = getLastPageForCategory(laptopResultsPage);
		int countLaptop = 0;
		
		try(ProgressBar pb = new ProgressBar("Scraping...", 100)){
			for(int i = 1; i <= lastPage; i++) {
				pb.stepBy(i);
				String currentPage = "https://www.topachat.com/pages/produits_cat_est_ordinateurs_puis_rubrique_est_wport_puis_page_est_" + i + ".html";
				for(String link : getLinksForPage(currentPage)) {
					countLaptop++;
					System.out.println("Processing laptop n°" + countLaptop + "..");
					scrapLaptopPage(link, laptops);
				}
			}
		}
		
		LaptopPersistence laptopPersistence = new LaptopPersistence();
		laptopPersistence.bulkInsertLaptop(laptops);
	}
	
	public static int getLastPageForCategory(String categoryUrl) {
		int lastPage = 1;
		
		try {
			Document doc = Jsoup.connect(categoryUrl)
				     .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
				     .ignoreHttpErrors(true)
				     .ignoreContentType(true)
				     .timeout(20000)
				     .get();
			
			Element pagination = doc.select("nav.pagination").get(1);
			int paginationSize = pagination.getElementsByTag("a").size();
			String tempLastPage = pagination.select("a").get(paginationSize - 2).text();
			lastPage = Integer.parseInt(tempLastPage.replace("...", "").trim());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return lastPage;
	}
	
	public static ArrayList<String> getLinksForPage(String pageUrl) {
		ArrayList<String> linkList = new ArrayList<>();
		
		try {
			Document doc = Jsoup.connect(pageUrl)
				     .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
				     .ignoreHttpErrors(true)
				     .ignoreContentType(true)
				     .timeout(20000)
				     .get();
			
			Elements products = doc.getElementsByAttributeValueStarting("class", "grille-produit");
			for(Element product : products) {
				String productLink = "https://www.topachat.com" + product.select("div.libelle > a").attr("href");
				linkList.add(productLink);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return linkList;
	}
	
	public static void scrapLaptopPage(String url, ArrayList<Laptop> laptops) {
		try {
			Document doc = Jsoup.connect(url)
				     .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
				     .ignoreHttpErrors(true)
				     .ignoreContentType(true)
				     .timeout(10000)
				     .get();
			
			String name = doc.getElementsByAttributeValue("property", "og:title").attr("content").toString();
			name = name.substring(0, name.indexOf("|")).trim();
			String reference = doc.select("div > span").first().text();
			String imageUri = "https:" + doc.getElementsByClass("main-image").attr("src").toString();
			String price = doc.getElementsByAttributeValue("itemprop", "price").attr("content").toString() + "€";
			
			String content, screenSize, screenResolution, cpu, dedicatedGpu, chipset, storage, ram, os, weight, gpu;
			content = screenSize = screenResolution = cpu = dedicatedGpu = chipset = storage = ram = os = weight = gpu = "";
			
			Elements characteristics = doc.select("div.caracLine");
			for (Element characteristic : characteristics) {
				content = characteristic.select("div.caracDesc").text();
				switch(characteristic.select("div.caracName").text()) {
				case "Processeur":
					cpu = content.substring(0, content.indexOf("Fréquence :")).trim();
					break;
				case "Chipset":
					chipset = content.trim();
					break;
				case "Carte graphique":
					if(!content.contains("Pas de carte graphique")) {
						if(content.contains("Mémoire")) {
							dedicatedGpu = content.substring(0, content.indexOf("Mémoire :")).trim();
						} else if (content.contains("Configuration")) {
							dedicatedGpu = content.substring(0, content.indexOf("Configuration :")).trim();
						}
					}
					break;
				case "Mémoire (RAM)":
					if(content.contains("Configuration")) {
						ram = content.substring(0, content.indexOf("Configuration :")).trim();
					} else {
						ram = content.trim();
					}
					break;
				case "Stockage":
					storage = content.trim();
					break;
				case "Ecran":
					if(content.contains("pouces")) {
						screenSize = content.substring(0, content.indexOf("pouces")).trim();
					} else if (content.contains("\"")) {
						screenSize = content.substring(0, content.indexOf("\"")).trim();
					} else if (content.contains("''")) {
						screenSize = content.substring(0, content.indexOf("''")).trim();
					}
					screenSize = screenSize != null ? screenSize.replaceAll("[^\\d.,]", "") : screenSize;
					
					if(content.contains("Résolution") && content.contains("Type de dalle")) {
						String resolutionInfos = content.substring(content.indexOf("Résolution : ") + 13, content.indexOf("Type de dalle :")).trim();
						
						if(resolutionInfos.contains("(") && resolutionInfos.contains(")")) {
							screenResolution = resolutionInfos.substring(resolutionInfos.indexOf("(") + 1, resolutionInfos.lastIndexOf(")")).trim();
						} else {
							screenResolution = resolutionInfos.trim();
						}
					} else {
						String resolutionInfos = content.substring(content.indexOf("Résolution : ") + 13).trim();
						
						if(resolutionInfos.contains("(") && resolutionInfos.contains(")")) {
							screenResolution = resolutionInfos.substring(resolutionInfos.indexOf("(") + 1, resolutionInfos.indexOf(")")).trim();
						}
					}
					
					break;
				case "Système d'exploitation":
					os = content.trim();
					break;
				case "Poids":
					weight = content.trim().replace(",", ".");
					if(!weight.toLowerCase().contains("kg")) {
						int numbersOnly = Integer.parseInt(weight.replaceAll("[^0-9]", "").trim());
						weight = (numbersOnly / 1000) + " Kg";
					}
					break;
				}
			}
			
			gpu = dedicatedGpu != "" ? dedicatedGpu : chipset;
			
			
			if(!(content == "" & screenSize == "" && screenResolution == "" && cpu == "" && dedicatedGpu == "" && chipset == "" && storage == ""
					&& ram  == "" && os == "" && weight == "" && gpu == "")) {
				System.out.println("Source: TopAchat" + "\nURL: " + url + "\nName: " + name + "\nReference: " + reference + "\nImage URI: " + imageUri + "\nPrice: "+ price
						+ "\nScreen size: " + screenSize + "\nScreen resolution: " + screenResolution + "\nCPU: " + cpu + "\nGPU: " + gpu + "\nRAM: " + ram + "\nStorage: " + storage
						+ "\nOperating system: " + os + "\nWeight: " + weight);
				System.out.println("######################################################################################################################################");
				
				Laptop currentLaptop = new Laptop();
				currentLaptop.setSource("TopAchat");
				currentLaptop.setUri(url);
				currentLaptop.setName(name);
				currentLaptop.setReference(reference);
				currentLaptop.setImageUri(imageUri);
				currentLaptop.setPrice(price);
				currentLaptop.setScreenSize(screenSize);
				currentLaptop.setScreenResolution(screenResolution);
				currentLaptop.setCpu(cpu);
				currentLaptop.setGpu(gpu);
				currentLaptop.setRam(ram);
				currentLaptop.setStorage(storage);
				currentLaptop.setOperatingSystem(os);
				currentLaptop.setWeight(weight);
				laptops.add(currentLaptop);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
