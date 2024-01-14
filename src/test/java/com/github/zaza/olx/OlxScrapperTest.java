package com.github.zaza.olx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class OlxScrapperTest {

	@Test
	public void hasOffers() throws Exception {
		assertHasOffers("kierowce przyjme");
	}

	@Test
	public void hasNoOffers() throws Exception {
		OlxScrapper scrapper = new OlxScrapper(
				OlxQueryBuilder.query("fdsfsdfsda").toUrl());
		assertFalse(scrapper.hasOffers());
		assertEquals(0, scrapper.getOffersCount());
		assertEquals(0, scrapper.getOffers().size());
	}

	@Test
	public void offersInSingleCategory() throws Exception {
		assertHasOffers("czarny proch");
	}
	
	@Test
	public void emptyQueryReturnsAllOffers() throws Exception {
		OlxScrapper scrapper = new OlxScrapper(
				OlxQueryBuilder.query("").toUrl());
		assertTrue(scrapper.hasOffers());
		assertThat(scrapper.getOffersCount()).isEqualTo(1000); // "Znaleźliśmy ponad 1000 ogłoszeń"
	}

	private void assertHasOffers(String query) throws IOException {
		OlxScrapper scrapper = new OlxScrapper(OlxQueryBuilder.query(query).toUrl());
		assertTrue(scrapper.hasOffers());
		assertThat(scrapper.getOffersCount()).isPositive();
		// TODO: featured offers are displayed on each page, use div id (unique) or count offers with data-testid=adCard-featured once
//		assertEquals(scrapper.getOffersCount(), scrapper.getOffers().size());
	}

}
