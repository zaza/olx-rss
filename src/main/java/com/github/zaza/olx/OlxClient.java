package com.github.zaza.olx;

import static java.lang.String.format;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import spark.Request;

public class OlxClient {

	private OlxQueryBuilder builder;

	public OlxClient(Request req) throws MalformedURLException {
		this.builder = OlxQueryBuilder.query(req.queryParams("string"));
	}

	public SearchResult search() throws IOException {
		URL url = builder.toUrl();
		System.out.println("Retrieving offers for URL: " + url);
		List<OlxOffer> offers = new OlxScrapper(url).getOffers();
		System.out.println(format("Found %d offers.", offers.size()));
		return new SearchResult(url, builder.getDescription(), offers);
	}

}
