package com.github.zaza.olx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OlxScrapperTest {

	@Test
	public void hasOffers() throws Exception {
		OlxScrapper scrapper = new OlxScrapper(
				OlxQueryBuilder.query("kierowce przyjme").toUrl());
		assertTrue(scrapper.hasOffers());
		assertTrue(scrapper.getOffersCount() > 0);
		assertEquals(scrapper.getOffersCount(), scrapper.getOffers().size());
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
		OlxScrapper scrapper = new OlxScrapper(
				OlxQueryBuilder.query("czarny proch").toUrl());
		assertTrue(scrapper.hasOffers());
		assertTrue(scrapper.getOffersCount() > 0);
		assertEquals(scrapper.getOffersCount(), scrapper.getOffers().size());
	}

}
