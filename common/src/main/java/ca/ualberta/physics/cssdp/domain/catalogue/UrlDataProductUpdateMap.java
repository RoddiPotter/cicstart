package ca.ualberta.physics.cssdp.domain.catalogue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlDataProductUpdateMap {

	private static Logger logger = LoggerFactory.getLogger(UrlDataProductUpdateMap.class);
	
	private final Map<String, UrlDataProduct> urlDataProductMap;

	private final List<String> urls;

	public UrlDataProductUpdateMap(Collection<UrlDataProduct> urlDataProducts) {

		int size = urlDataProducts.size();
		urlDataProductMap = new HashMap<String, UrlDataProduct>(size);
		urls = new ArrayList<String>(size);

		for (UrlDataProduct urlDataProduct : urlDataProducts) {
			String url = urlDataProduct.getUrl();

			if (url == null || urlDataProduct.getDataProduct() == null) {
				System.out.println("Crappy - one of these is null: url =" + url
						+ ", dp=" + urlDataProduct.getDataProduct());
			}
			if(urlDataProductMap.containsKey(url)){
				logger.warn("Duplicate url found!  Ignoring it:" + url);
			}
			urlDataProductMap.put(url, urlDataProduct);
			urls.add(url);
		}
	}

	public List<String> getUrls() {
		return urls;
	}

	public UrlDataProduct get(String url) {
		return urlDataProductMap.get(url);
	}

	public void remove(String url) {
		urlDataProductMap.remove(url);
		urls.remove(url);
	}

}
