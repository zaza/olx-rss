package com.github.zaza.olx;

import static java.lang.String.format;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class OlxQueryBuilder {

	private String query;

	private OlxQueryBuilder(String query) {
		this.query = query;
	}

	static OlxQueryBuilder query(String query) {
		return new OlxQueryBuilder(query);
	}

	URL toUrl() throws MalformedURLException {
		return URI.create(
				format("https://www.olx.pl/oferty/%s", getQuery()))
				.toURL();
	}

	private String getQuery() {
		if (query == null || query.isEmpty())
			return "";
		return format("q-%s/", getEncodedQuery());
	}

	private String getEncodedQuery() {
		return query.replace(' ', '-');
	}

}
