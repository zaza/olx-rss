package com.github.zaza.olx;

import java.net.URI;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

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
		return getCell(1, 0).select("p > small").first().text();
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

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this.getClass()).add("tytul", getTitle()) //
				.add("cena", getPrice()) //
				.add("lokalizacja", getCity()).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		OlxOffer other = (OlxOffer) obj;
		return Objects.equal(this.getTitle(), other.getTitle()) // 
				&& Objects.equal(this.getPrice(), other.getPrice())
				&& Objects.equal(this.getCity(), other.getCity());
	}

	public int hashCode() {
		return Objects.hashCode(getTitle(), getPrice(), getCity());
	}

}
