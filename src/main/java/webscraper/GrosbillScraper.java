package webscraper;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import business.data.Laptop;
import me.tongfei.progressbar.ProgressBar;

public class GrosbillScraper {
	
	public static void main(String[] args) {
		String baseUrl = "https://www.grosbill.com/2-ordinateur_portable-cat-ordinateurs?page=";
		int lastPage = getLastPageForCategory(baseUrl + "1");
		ArrayList<Laptop> laptops = new ArrayList<Laptop>();
		
		int countLaptop = 0;
		
		try(ProgressBar pb = new ProgressBar("Scraping...", 100)){
			for(int i = 1; i <= lastPage; i++) {
				pb.stepBy(i);
				
				String currentPage = baseUrl + i;
				for(String link : getPageLinks(currentPage)) {
					countLaptop++;
					System.out.println("############################################################################");
					System.out.println("Extracting laptop n°" + countLaptop + "...");
					
					scrapLaptopPage(link, laptops);
				}
			}
		}
		
		LaptopPersistence laptopPersistence = new LaptopPersistence();
		laptopPersistence.bulkInsertLaptop(laptops);
	}
	
	public static void scrapLaptopPage(String url, ArrayList<Laptop> laptops) {
		try {
			Document doc = Jsoup.connect(url)
				     .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:81.0) Gecko/20100101 Firefox/81.0")
				     .ignoreHttpErrors(true)
				     .ignoreContentType(true)
				     .timeout(10000)
				     .get();
			
			String name, price, reference, image;
			name = price = reference = image = null;
			
			name = doc.getElementsByClass("title_fiche").text().replace("PC portable ", "").trim();
			
			price = doc.getElementsByAttributeValue("property", "product:price:amount").attr("content").trim() + "€";
			price = price.contains(",") ? price.replace(",", ".") : price;
			
			JSONObject jsonData = getJsonCharacteristics(doc);
			
			reference = getReference(doc, jsonData);
			image = getImage(doc, jsonData);
			
//			System.out.println("lien : " + url);
//			System.out.println("source : Grosbill");
//			System.out.println("nom : " + name);
//			System.out.println("prix : " + price);
//			System.out.println("reference : " + reference);
//			System.out.println("image : " + image);

			Laptop laptop = new Laptop();
			laptop.setSource("Grosbill");
			laptop.setUri(url);
			laptop.setName(name);
			laptop.setReference(reference);
			laptop.setImageUri(image);
			laptop.setPrice(price);
			extractCharacteristics(doc, laptop);
			System.out.println(laptop);
			
			laptops.add(laptop);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void extractCharacteristics(Document doc, Laptop laptop) {
		String screenSize, screenResolution, cpu, storage, ram, os, weight, gpu;
		screenSize = screenResolution = cpu = storage = ram = os = weight = gpu = null;
		
		Elements htmlCharacteristics = getLaptopCharacteristics(doc);
		for(Element characteristic : htmlCharacteristics) {
			Elements line = characteristic.getElementsByTag("td");
			// Removing fucking spaces before section name, this dev is an asshole
			String section = line.first().text().replace("\u00a0", "").trim();
			
			if(line.size() > 1) {
				String content = null;
				
				// Sometimes the content is located in the third element
				if(line.size() > 2) {
					content = line.get(2).text().replace("\u00a0", "").trim();
				} else {
					content = line.get(1).text().replace("\u00a0", "").trim();
				}
				
				switch(section.toLowerCase()) {
				case "os":
				case "système":
				case "système d'exploitation":
				case "version système d'exploitation":
					os = content;
					break;
				case "type de processeur":
				case "processeur":
				case "processeurs":
				case "cpu":
					if(cpu == null) {
						cpu = content;
					} else {
						if(content.length() > cpu.length()) {
							cpu = content;
						}
					}
					cpu = content;
					break;
				case "mémoire":
				case "taille de la mémoire":
				case "taille mémoire vive":
				case "ram":
				case "mémoire, standard":
					ram = content;
					break;
				case "ssd":
				case "disque dur":
				case "stockage":
				case "capacité de stockage totale":
				case "stockage principal":
					storage = content;
					break;
				case "taille de l'écran":
					screenSize = content.replace("pouces", "").trim();
				case "type":
					if(content.contains("\"")) {
						screenSize = content.replace("\"", "");
					}
					break;
				case "résolution max":
					if(ScrapingUtilities.stringHasDigits(content, 3)) {
						screenResolution = content.replace("pixels", "").trim();
					}
				case "résolution":
					if(!content.toLowerCase().contains("mégapixel") && ScrapingUtilities.stringHasDigits(content, 3)) {
						screenResolution = content;
					}
					break;
				case "carte graphique":
				case "chipset graphique":
				case "processeur graphique":
				case "graphiques":
					gpu = content;
					break;
				case "poids du produit":
				case "poids":
					weight = content.replace(",", ".");
					if(!weight.toLowerCase().contains("kg") && weight.trim() != "") {
						int numbersOnly = Integer.parseInt(weight.replaceAll("[^0-9]", "").trim());
						weight = (numbersOnly / 1000) + " Kg";
					}
					break;
				default:
					break;
				}
			}
		}
		
		Elements primaryCharacteristics = doc.getElementsByClass("caracterisques_principales");
		for(Element primaryCharacteristic : primaryCharacteristics) {
			String type = primaryCharacteristic.getElementsByClass("title-car").first().text().trim();
			String typeContent = primaryCharacteristic.text().split(":")[1].trim();
			
			switch(type.toLowerCase()) {
			case "taille pc portable":
				screenSize = screenSize == null || screenSize.trim() == "" ? typeContent.replace("\"", "") : screenSize;
				break;
			case "processeur":
				cpu = cpu == null || cpu.trim() == "" ? typeContent : cpu;
				break;
			case "mémoire":
				ram = ram == null || ram.trim() == "" ? typeContent : ram;
				break;
			case "ssd":
			case "disque dur":
				storage = storage == null || storage.trim() == "" ? typeContent : storage;
				break;
			case "chipset graphique":
				gpu = gpu == null || gpu.trim() == "" ? typeContent : gpu;
				break;
			case "os":
				os = os == null || os.trim() == "" ? typeContent : os;
				break;
			}
		}
				
//		System.out.println("screenSize : " + screenSize);
//		System.out.println("screenResolution : " + screenResolution);
//		System.out.println("cpu : " + cpu);
//		System.out.println("storage : " + storage);
//		System.out.println("ram : " + ram);
//		System.out.println("os: " + os);
//		System.out.println("weight : " + weight);
//		System.out.println("gpu : " + gpu);
		
		laptop.setScreenSize(screenSize);
		laptop.setScreenResolution(screenResolution);
		laptop.setCpu(cpu);
		laptop.setGpu(gpu);
		laptop.setRam(ram);
		laptop.setStorage(storage);
		laptop.setOperatingSystem(os);
		laptop.setWeight(weight);
	}
	
	public static Elements getLaptopCharacteristics(Document doc) {
		return doc.getElementsByClass("glob-carac").first().select("tbody > tr");
	}
	
	public static JSONObject getJsonCharacteristics(Document doc) {
		JSONObject jsonData = null;
		
		Elements jsonScripts = doc.getElementsByAttributeValue("type", "application/ld+json");
		for(Element script : jsonScripts) {
			if(script.data().contains("mpn") || script.data().contains("image")) {
				jsonData = new JSONObject(script.data());
			}
		}
		
		return jsonData;
	}
	
	public static String getReference(Document doc, JSONObject jsonData) {
		String reference = null;
		
		if(jsonData.has("mpn") && jsonData.get("mpn") != "") {
			reference = jsonData.get("mpn").toString().trim();
		}
		
		if(reference == null) {
			String title = doc.getElementsByTag("title").text().trim();
			reference = title.split("-")[0].split(" ")[1].trim();
		}
		
		return reference;
	}
	
	public static String getImage(Document doc, JSONObject jsonData) {
		String image = null;
		
		if(jsonData.has("image") && jsonData.get("image") instanceof JSONArray) {
			JSONArray images = jsonData.getJSONArray("image");
			
			if (images != null && images.length() > 0) {
				image = images.get(0).toString().trim();
			}
		}
		
		
		if(image == null) {
			image = "https://www.grosbill.com" + doc.getElementsByAttributeValue("property", "og:image").attr("content").trim();
		}
		
		return image;
	}
	
	public static int getLastPageForCategory(String categoryUrl) {
		int lastPage = 1;
		final int rowsPerPage = 30;
		
		try {
			Document doc = Jsoup.connect(categoryUrl)
				     .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:81.0) Gecko/20100101 Firefox/81.0")
				     .ignoreHttpErrors(true)
				     .ignoreContentType(true)
				     .timeout(10000)
				     .get();
			
			int totalResults = Integer.parseInt(doc.getElementsByClass("chiffre-prod").text().trim());
			lastPage = (int) Math.ceil((double) totalResults / rowsPerPage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return lastPage;
	}

	public static ArrayList<String> getPageLinks(String pageUrl) {
		ArrayList<String> pageLinks = new ArrayList<String>();
		
		try {
			Document doc = Jsoup.connect(pageUrl)
				     .userAgent("Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:81.0) Gecko/20100101 Firefox/81.0")
				     .ignoreHttpErrors(true)
				     .ignoreContentType(true)
				     .timeout(10000)
				     .get();
			
			Elements laptopBlocs = doc.select("div.categorie-filtre.lst_grid");
			for(Element bloc : laptopBlocs) {
				for(Element elementLink : bloc.getElementsByTag("a")) {
					String link = elementLink.attr("href").trim();
					if(!pageLinks.contains(link) && !link.contains("addpanier") && link != "" && link != null) {
						pageLinks.add(link);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return pageLinks;
	}
}
