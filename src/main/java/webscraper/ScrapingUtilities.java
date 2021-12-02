package webscraper;

public class ScrapingUtilities {
	
	public static boolean stringHasDigits(String text, int minimumDigits) {
		int digitCount = 0;
		
		for(int i = 0; i < text.length(); i++) {
			if(Character.isDigit(text.charAt(i))) {
				digitCount++;
			}
		}
		
		if(digitCount >= minimumDigits) {
			return true;
		}
		
		return false;
	}
}