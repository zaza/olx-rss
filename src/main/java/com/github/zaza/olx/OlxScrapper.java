package com.github.zaza.olx;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class OlxScrapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(OlxScrapper.class);
	
	private static final int FIFTEEN_SECONDS = (int) TimeUnit.SECONDS.toMillis(15);
	private static final Pattern PATTERN_FOUND_N_OFFERS = Pattern.compile("Znaleźliśmy (ponad )?(\\d+) ogłosze(ń|nia)");

	private URL url;

	private Document document;

	private int page;

	public OlxScrapper(URL url) {
		this.url = url;
		this.page = 1;
	}

	public boolean hasOffers() throws IOException {
		return getOffersCount() > 0;
	}

	public int getOffersCount() throws IOException {
		Element element = getDocument().selectFirst("span[data-testid=total-count]");
		if (element == null) return 0;
		String text = element.text();
		Matcher matcher = PATTERN_FOUND_N_OFFERS.matcher(text);
		if (matcher.matches()) {
			return Integer.parseInt(matcher.group(2));
		}
		return 0;
	}

	public List<OlxOffer> getOffers() throws IOException {
		if (!hasOffers()) {
			// offers listed are not related to the query
			LOGGER.info("Found no related offers.");
			return Collections.emptyList();
		}
		Elements elements = new Elements(getOffersCount());
		if (!hasNextPage()) {
			// if this is the only page with results, limit them to those matching the query
			// other results from the page are suggestions e.g. with larger location radius
			elements.addAll(getOfferElements().subList(0, getOffersCount()));
		} else {
			elements.addAll(getOfferElements());
		}
		while (hasNextPage()) {
			LOGGER.debug("Processing next page...");
			url = getNextPageUrl();
			document = null;
			elements.addAll(getOfferElements());
			page++;
		}
		return elements.stream().map(OlxOffer::new).collect(toList());
	}

	private Elements getOfferElements() throws IOException {
		return getDocument().select("div[data-testid=listing-grid] > div[data-testid=l-card]");
	}

	private boolean hasNextPage() throws IOException {
		return !getNextPageElements().isEmpty();
	}

	private URL getNextPageUrl() throws IOException {
		String href = getNextPageElements().first().attr("href");
		String base = OlxQueryBuilder.base().toUrl().toString();
		return URI.create(base + href).toURL();
	}

	private Elements getNextPageElements() throws IOException {
		return getDocument()
				.select("a[data-testid=pagination-link-" + (page + 1) + "]");
	}

	private Document getDocument() throws IOException {
		if (document == null)
			LOGGER.debug("Parsing document from {}", url);
		document = Jsoup.parse(url, FIFTEEN_SECONDS);
		return document;
	}

}
