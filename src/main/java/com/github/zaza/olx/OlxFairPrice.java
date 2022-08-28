package com.github.zaza.olx;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;

import com.google.common.math.Quantiles;

public class OlxFairPrice {

	public static void main(String[] args) throws IOException {
		String query = Arrays.stream(args).collect(joining(" "));
		checkArgument(!query.isEmpty(), "must provide search query");

		URL url = OlxQueryBuilder.query(query).toUrl();
		List<OlxOffer> offers = new OlxScrapper(url).getOffers();
		System.out.printf("Found %d offers.%n", offers.size());

		List<Double> prices = offers.stream().map(OlxOffer::getPrice).filter(p -> !"Zamienię".equals(p))
				.filter(p -> !"Za darmo".equals(p)).map(p -> p.substring(0, p.indexOf(" ")))
				.map(price -> price.replace(',', '.')).mapToDouble(price -> Double.parseDouble(price)).boxed()
				.collect(toList());

		OptionalDouble maybeAveragePrice = prices.stream().mapToDouble(d -> d).average();
		maybeAveragePrice.ifPresent(ap -> System.out.printf("* average price (rounded): %d%n", Math.round(ap)));

		double median = Quantiles.median().compute(prices);
		System.out.printf("* median price: %f%n", median);
	}
}
