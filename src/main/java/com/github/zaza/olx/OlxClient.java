package com.github.zaza.olx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;

public class OlxClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(OlxClient.class);
	
	private OlxQueryBuilder builder;

	public OlxClient(Request req) throws MalformedURLException {
		this.builder = OlxQueryBuilder.query(req.queryParams("string"));
	}

	public SearchResult search() throws IOException {
		URL url = builder.toUrl();
		LOGGER.info("Retrieving offers for URL: {}", url);
		List<OlxOffer> offers = new OlxScrapper(url).getOffers();
		LOGGER.info("Found {} offers.", offers.size());
		return new SearchResult(url, builder.getDescription(), offers);
	}

}
