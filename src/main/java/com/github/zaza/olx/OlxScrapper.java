package com.github.zaza.olx;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class OlxScrapper {

	private static final int FIFTEEN_SECONDS = (int) TimeUnit.SECONDS.toMillis(15);

	private URL url;

	private Document document;

	private int page;

	public OlxScrapper(URL url) throws MalformedURLException {
		this.url = url;
		this.page = 1;
	}

	public boolean hasOffers() throws IOException {
		Elements elements = getDocument().select("div#topLink");
		return !elements.isEmpty() && elements.first().hasText();
	}

	public int getOffersCount() throws IOException {
		if (!hasOffers())
			return 0;
		Elements spans = getDocument()
				.select("div#topLink > div > ul > li.hidden > span");
		return spans.stream()
				.mapToInt(s -> Integer.parseInt(s.text().replaceAll(" ", "")))
				.sum();
	}

	public List<OlxOffer> getOffers() throws IOException {
		if (!hasOffers()) {
			// offers listed are not related to the query
			System.out.println("Found no related offers.");
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
			System.out.println("Processing next page...");
			url = getNextPageUrl();
			document = null;
			elements.addAll(getOfferElements());
			page++;
		}
		return elements.stream().map(OlxOffer::new).collect(toList());
	}

	private Elements getOfferElements() throws IOException {
		return getDocument()
				.select("table#offers_table > tbody > tr > td > div > table");
	}

	private boolean hasNextPage() throws IOException {
		return !getNextPageElements().isEmpty();
	}

	private URL getNextPageUrl() throws IOException {
		String href = getNextPageElements().get(0).attr("href");
		return URI.create(href).toURL();
	}

	private Elements getNextPageElements() throws IOException {
		return getDocument()
				.select("a[class=link pageNextPrev {page:" + (page + 1) + "}");
	}

	private Document getDocument() throws IOException {
		if (document == null)
			System.out.println(url);
		document = Jsoup.parse(url, FIFTEEN_SECONDS);
		return document;
	}

}
