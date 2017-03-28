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
				OlxQueryBuilder.query("asdfghjkl").toUrl());
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
		assertThat(scrapper.getOffersCount()).isGreaterThan(1000000);
	}

	private void assertHasOffers(String query) throws IOException {
		OlxScrapper scrapper = new OlxScrapper(
				OlxQueryBuilder.query(query).toUrl());
		assertTrue(scrapper.hasOffers());
		assertThat(scrapper.getOffersCount()).isGreaterThan(0);
		assertEquals(scrapper.getOffersCount(), scrapper.getOffers().size());
	}

}
