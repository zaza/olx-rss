package com.github.zaza.olx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class OlxOfferTest {

	@Rule
	public TestName testName = new TestName();

	@Offline
	@Test
	public void jobOffer() throws Exception {
		OlxOffer offer = new OlxOffer(getElement());

		assertEquals("Przyjmę do pracy kierowce kat. C", offer.getTitle());
		assertEquals("2 000 zł/mies.", offer.getPrice());
		assertEquals(
				URI.create(
						"https://www.olx.pl/oferta/przyjme-do-pracy-kierowce-kat-c-CID4-IDkbKqa.html#e966dbc794"),
				offer.getUri());
		assertEquals("Szczytno", offer.getCity());
		assertNull(offer.getPhoto());
	}

	@Offline
	@Test
	public void jobOfferWithPhoto() throws Exception {
		OlxOffer offer = new OlxOffer(getElement());

		assertEquals("Przyjmę kierowce na busa miedzynarodowka",
				offer.getTitle());
		assertEquals("", offer.getPrice());
		assertEquals(
				URI.create(
						"https://www.olx.pl/oferta/przyjme-kierowce-na-busa-miedzynarodowka-CID619-IDlbjgm.html#e966dbc794"),
				offer.getUri());
		assertEquals("Zabrze", offer.getCity());
		assertEquals(
				URI.create(
						"https://olxpl-ring05.akamaized.net/images_tablicapl/509502740_1_261x203_przyjme-kierowce-na-busa-miedzynarodowka-zabrze.jpg"),
				offer.getPhoto());
	}
	
	@Offline
	@Test
	public void offerNegotiablePrice() throws Exception {
		OlxOffer offer = new OlxOffer(getElement());

		assertEquals("Sprzedam Opla Omegę Lpg",
				offer.getTitle());
		assertEquals("5 500 zł Do negocjacji", offer.getPrice());
		assertEquals(
				URI.create(
						"https://www.olx.pl/oferta/sprzedam-opla-omege-lpg-CID5-IDl7NDy.html#d3b98b0b80"),
				offer.getUri());
		assertEquals("Garcz", offer.getCity());
		assertEquals(
				URI.create(
						"https://olxpl-ring09.akamaized.net/images_tablicapl/508204936_1_261x203_sprzedam-opla-omege-lpg-kartuzy_rev001.jpg"),
				offer.getPhoto());
	}

	@Test
	public void offerWithPhoto() throws Exception {
		OlxScrapper scrapper = new OlxScrapper(
				OlxQueryBuilder.query("sprzedam opla").toUrl());

		OlxOffer offer = scrapper.getOffers().iterator().next();

		assertThat(offer.getTitle()).containsIgnoringCase("sprzedam");
		assertThat(offer.getTitle()).containsIgnoringCase("opla");
		assertThat(offer.getPrice()).matches("[ \\d]+ zł( Do negocjacji)?");
		assertThat(offer.getUri()).isNotNull();
		assertThat(offer.getCity()).isNotEmpty();
		assertThat(offer.getPhoto()).isNotNull();
	}

	private Element getElement() throws IOException {
		String tag = readFile(testName.getMethodName() + ".htm");
		return Jsoup.parse(tag, "", Parser.xmlParser());
	}

	private String readFile(String name) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(name).getFile());
		return new String(Files.readAllBytes(file.toPath()), "UTF-8");
	}
}
