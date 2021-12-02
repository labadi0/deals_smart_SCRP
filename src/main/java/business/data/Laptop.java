package business.data;

import java.lang.reflect.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j

public class Laptop {
	private String title;
	private String source;
	private String uri;
	private String name;
	private String reference;
	private String imageUri;
	private String screenSize;
	private String screenResolution;
	private String cpu;
	private String gpu;
	private String ram;
	private String storage;
	private String operatingSystem;
	private String weight;
	private String price;

	public boolean checkNotNull(Laptop laptop) throws IllegalAccessException {
		if ((laptop.getTitle() != null) || (laptop.getSource() != null) || (laptop.getUri() != null)
				|| (laptop.getName() != null) || (laptop.getReference() != null) || (laptop.getImageUri() != null)
				|| (laptop.getScreenSize() != null) || (laptop.getScreenResolution() != null)
				|| (laptop.getCpu() != null) || (laptop.getGpu() != null) || (laptop.getRam() != null)
				|| (laptop.getStorage() != null) || (laptop.getOperatingSystem() != null)
				|| (laptop.getWeight() != null) || (laptop.getPrice() != null)) {

			return true;

		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return source + ";" + uri + ";" + name + ";" + reference + ";" + imageUri + ";" + screenSize + ";"
				+ screenResolution + ";" + cpu + ";" + gpu + ";" + ram + ";" + storage + ";" + operatingSystem + ";"
				+ weight + ";" + price + ";" + title;

		// Laptop [source=Darty,
		// uri=https://www.darty.com/nav/achat/informatique/macbook_imac_ipad/macbook/apple_newmba13_m1_8_256_gs.html,
		// name=Apple MacBook Air 13'' 256 Go SSD 8 Go RAM Puce M1 Gris sidéral Nouveau,
		// reference=4789075,
		// imageUri=https://image.darty.com/informatique/macbook_imac_ipad/macbook/apple_newmba13_m1_8_256_gs_s2011104789075A_221333728.jpg,
		// screenSize=13,3 ", screenResolution=2 560 x 1 600 pixels, cpu=Nombre de cœur
		// : 8; Apple M1, gpu=Apple M1 7-core, ram=8 Go, storage= ssd 256 Go,
		// operatingSystem=MacOS, weight=1,29 kg, price=1 029,99 €]

	}

}
