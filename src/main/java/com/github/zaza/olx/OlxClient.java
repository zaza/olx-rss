package com.github.zaza.olx;

import lombok.extern.slf4j.Slf4j;
import spark.Request;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Slf4j
public class OlxClient {

	private final OlxQueryBuilder builder;

	public OlxClient(Request req) {
		this.builder = OlxQueryBuilder.query(req.queryParams("string"));
	}

	public SearchResult search() throws IOException {
		URL url = builder.toUrl();
		log.info("Retrieving offers for URL: {}", url);
		List<OlxOffer> offers = new OlxScrapper(url).getOffers();
		log.info("Found {} offers.", offers.size());
		return new SearchResult(url, builder.getDescription(), offers);
	}

}
