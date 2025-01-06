package com.github.zaza.olx;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Element;

import java.net.URI;

@RequiredArgsConstructor
public class OlxOffer {

	private final Element element;

	public String getTitle() {
		return element.select("h4").first().text();
	}

	public String getPrice() {
		return element.select("p[data-testid=ad-price]").first().text();
	}

	public URI getUri() {
		String href = element.select("a[href]").first().attr("href");
		URI uri = URI.create(href);
		if (uri.isAbsolute())
			return uri;
		return URI.create(OlxQueryBuilder.base().withPath(href).toUrl().toString());
	}

	public String getCity() {
		String locationDate = element.select("p[data-testid=location-date]").first().text();
		// e.g. "Łapanów - 26 grudnia 2023"
		return locationDate.substring(0, locationDate.indexOf(" - "));
	}

	public URI getPhoto() {
		String src = element.select("img[src]").first().attr("src");
		return URI.create(src);
	}

	public boolean hasPhoto() {
		return getPhoto() != null;
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
