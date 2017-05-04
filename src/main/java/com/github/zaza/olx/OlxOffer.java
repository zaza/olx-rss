package com.github.zaza.olx;

import java.net.URI;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OlxOffer {

	private Element element;

	public OlxOffer(Element element) {
		this.element = element;
	}

	public String getTitle() {
		return getCell(0, 1).select("a[href]").first().text();
	}

	public String getPrice() {
		return getCell(0, 2).text();
	}

	public URI getUri() {
		return URI.create(getCell(0, 1).select("a[href]").first().attr("href"));
	}

	public String getCity() {
		return getCell(1, 0).select("p").first().text();
	}

	public URI getPhoto() {
		Elements images = getCell(0, 0).select("img");
		if (images.isEmpty())
			return null;
		return URI.create(images.first().attr("src"));
	}

	public boolean hasPhoto() {
		return getPhoto() != null;
	}

	Element getCell(int row, int column) {
		Element table = element.select("table").first();
		Element tr = table.select("tbody > tr").get(row);
		return tr.select("td").get(column);
	}

}
