package com.github.zaza.olx;

import java.net.URL;
import java.util.List;

public class SearchResult {

	private URL url;
	private String filterDescription;
	private List<OlxOffer> items;

	SearchResult(URL url, String description, List<OlxOffer> items) {
		this.url = url;
		this.filterDescription = description;
		this.items = items;
	}

	public String getQueryUrl() {
		return url.toString();
	}

	public String getFilterDescription() {
		return filterDescription;
	}

	public List<OlxOffer> getItems() {
		return items;
	}

}
