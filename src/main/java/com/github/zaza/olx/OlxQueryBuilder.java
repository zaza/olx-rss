package com.github.zaza.olx;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Range;
import io.mikael.urlbuilder.UrlBuilder;
import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.text.Normalizer;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

@RequiredArgsConstructor
public class OlxQueryBuilder {

	private final String query;
	private boolean withPhotoOnly;
	private String location;
	private Radius radius;
	private Range<Integer> price;

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
		checkState(location != null, "Location must be provided first.");
		this.radius = radius;
		return this;
	}

	OlxQueryBuilder minPrice(int minPrice) {
		checkArgument(minPrice >= 0, "Min price must be 0 at least.");
		if (price != null && price.hasUpperBound()) {
			checkArgument(price.upperEndpoint() > minPrice, "Min price must be less than max price.");
			this.price = Range.closed(minPrice, price.upperEndpoint());
		} else {
			this.price = Range.atLeast(minPrice);
		}
		return this;
	}

	OlxQueryBuilder maxPrice(int maxPrice) {
		checkArgument(maxPrice >= 0, "Max price must be 0 at least.");
		if (price != null && price.hasLowerBound()) {
			checkArgument(price.lowerEndpoint() < maxPrice, "Max price must be more than max price.");
			this.price = Range.closed(price.lowerEndpoint(), maxPrice);
		} else {
			this.price = Range.atMost(maxPrice);
		}
		return this;
	}

	static UrlBuilder base() {
		return UrlBuilder.empty().withScheme("https").withHost("www.olx.pl");
	}

	URL toUrl() {
		UrlBuilder builder = base().withPath(getPath());
		if (withPhotoOnly)
			builder = builder.addParameter("search[photos]", "1");
		if (radius != null)
			builder = builder.addParameter("search[dist]", radius.toString());
		if (price != null && price.hasLowerBound())
			builder = builder.addParameter("search[filter_float_price:from]",
					price.lowerEndpoint() == 0 ? "free" : price.lowerEndpoint().toString());
		if (price != null && price.hasUpperBound() && price.upperEndpoint() > 0)
			builder = builder.addParameter("search[filter_float_price:to]", price.upperEndpoint().toString());
		return builder.toUrl();
	}

	private String getPath() {
		StringBuilder sb = new StringBuilder();
		// TODO: verify location, issue #29
		if (location != null) {
			sb.append("/").append(deaccent(location));
		}
		if (radius == null) {
			sb.append("/oferty");
		}
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

	@VisibleForTesting
	Range<Integer> getPrice() {
		return price;
	}

}
