package com.github.zaza.olx;

import static java.lang.String.format;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.regex.Pattern;

import io.mikael.urlbuilder.UrlBuilder;

public class OlxQueryBuilder {

	private String query;
	private boolean withPhotoOnly;
	private String location;
	private Radius radius;

	private OlxQueryBuilder(String query) {
		this.query = query;
	}

	static OlxQueryBuilder query(String query) {
		return new OlxQueryBuilder(query);
	}

	OlxQueryBuilder withPhotoOnly() {
		this.withPhotoOnly = true;
		return this;
	}

	OlxQueryBuilder location(String location) {
		this.location = location;
		return this;
	}

	OlxQueryBuilder radius(Radius radius) {
		this.radius = radius;
		return this;
	}

	URL toUrl() throws MalformedURLException {
		UrlBuilder builder = UrlBuilder.empty().withScheme("https")
				.withHost("www.olx.pl");
		builder = builder.withPath(getPath());
		if (withPhotoOnly)
			builder = builder.addParameter("search[photos]", "1");
		if (radius != null)
			builder = builder.addParameter("search[dist]", radius.toString());
		return builder.toUrl();
	}

	private String getPath() {
		StringBuilder sb = new StringBuilder();
		// TODO: verify location
		if (location != null) {
			sb.append("/").append(deaccent(location));
		}
		sb.append("/oferty");
		if (query != null && !query.isEmpty())
			sb.append("/q-").append(getEncodedQuery()).append("/");
		return sb.toString();
	}

	private String getEncodedQuery() {
		return query.replace(' ', '-');
	}

	private String deaccent(String input) {
	    String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD); 
	    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
	    return pattern.matcher(nfdNormalizedString).replaceAll("");
	}

	public String getDescription() {
		return format("'%s'%s", query, withPhotoOnly ? " with photo" : "");
	}

}
