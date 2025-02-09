package com.github.zaza.olx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;

import org.assertj.core.api.Condition;
import org.assertj.core.api.SoftAssertions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class OlxOfferTest {

	@Rule
	public TestName testName = new TestName();

	@Offline
	@Test
	@Ignore("update test sample") // TODO issue #28
	public void jobOffer() throws Exception {
		OlxOffer offer = new OlxOffer(getElement());

		assertEquals("Przyjmę do pracy kierowce kat. C", offer.getTitle());
		assertEquals("2 000 zł/mies.", offer.getPrice());
		assertEquals(
				URI.create("https://www.olx.pl/oferta/przyjme-do-pracy-kierowce-kat-c-CID4-IDkbKqa.html#e966dbc794"),
				offer.getUri());
		assertEquals("Szczytno", offer.getCity());
		assertNull(offer.getPhoto());
	}

	@Offline
	@Test
	@Ignore("update test sample") // TODO issue #28
	public void jobOfferWithPhoto() throws Exception {
		OlxOffer offer = new OlxOffer(getElement());

		assertEquals("Przyjmę kierowce na busa miedzynarodowka", offer.getTitle());
		assertEquals("", offer.getPrice());
		assertEquals(URI.create(
				"https://www.olx.pl/oferta/przyjme-kierowce-na-busa-miedzynarodowka-CID619-IDlbjgm.html#e966dbc794"),
				offer.getUri());
		assertEquals("Zabrze", offer.getCity());
		assertEquals(URI.create(
				"https://olxpl-ring05.akamaized.net/images_tablicapl/509502740_1_261x203_przyjme-kierowce-na-busa-miedzynarodowka-zabrze.jpg"),
				offer.getPhoto());
	}

	@Offline
	@Test
	public void offerNegotiablePrice() throws Exception {
		OlxOffer offer = new OlxOffer(getElement());

		assertThat(offer.getTitle()).isEqualTo("Sprzedam Opel AstraJ");
		assertThat(offer.getPrice()).isEqualTo("18 900 złdo negocjacji");
		assertThat(offer.getUri()).asString().isEqualTo("https://www.olx.pl/d/oferta/sprzedam-opel-astraj-CID5-ID13N7Uu.html");
		assertThat(offer.getCity()).isEqualTo("Dąbrówka-Ług");
		assertThat(offer.getPhoto()).hasHost("ireland.apollo.olxcdn.com");
	}

	@Offline
	@Test
	public void offerFromOtomoto() throws Exception {
		OlxOffer offer = new OlxOffer(getElement());

		assertThat(offer.getTitle()).isEqualTo("Opel Astra Sprzedam");
		assertThat(offer.getPrice()).isEqualTo("4 900 zł");
		assertThat(offer.getUri()).asString().isEqualTo("https://www.otomoto.pl/osobowe/oferta/opel-astra-sprzedam-ID6GZojD.html");
		assertThat(offer.getCity()).isEqualTo("Strzelce Krajeńskie");
		assertThat(offer.getPhoto()).hasHost("ireland.apollo.olxcdn.com");
	}

	@Test
	public void offerWithPhoto() throws Exception {
		OlxScrapper scrapper = new OlxScrapper(OlxQueryBuilder.query("sprzedam konia").withPhotoOnly().toUrl());

		OlxOffer offer = scrapper.getOffers().iterator().next();

		SoftAssertions softly = new SoftAssertions();
		softly.assertThat(offer.getTitle()).containsIgnoringCase("sprzedam");
		softly.assertThat(offer.getTitle().toLowerCase(Locale.US)).containsAnyOf("konia", "koni", "ogier", "klacz", "źrebkę", "kucyka", "kuca");
		softly.assertThat(offer.getPrice()).matches("[ \\d]+ zł( do negocjacji)?");
		softly.assertThat(offer.getUri()).isNotNull();
		softly.assertThat(offer.getCity()).isNotEmpty();
		softly.assertThat(offer.hasPhoto()).as("offer %s has no photo", offer.getUri()).isTrue();
		softly.assertThat(offer.getPhoto()).isNotNull();
		softly.assertAll();
	}

	@Test
	@Ignore("find a query that is guaranteed to have an ad with no photo") // FIXME
	public void offerWithNoPhoto() throws Exception {
		OlxScrapper scrapper = new OlxScrapper(OlxQueryBuilder.query("sprzedam opla").toUrl());

		List<OlxOffer> offers = scrapper.getOffers();

		assertThat(offers).areAtLeastOne(hasNoPhoto());
	}

	@Test
	@Ignore("search by location is broken") // FIXME issue #27
	public void noOfferInLocation() throws Exception {
		OlxScrapper scrapper = new OlxScrapper(OlxQueryBuilder.query("kamienica").location("Kurozwęcz").toUrl());

		List<OlxOffer> offers = scrapper.getOffers();

		assertThat(offers).isEmpty();
	}

	@Test
	@Ignore("search by location is broken") // FIXME issue #27
	public void offerInLocation() throws Exception {
		OlxScrapper scrapper = new OlxScrapper(OlxQueryBuilder.query("nowy dom").location("Koszalin").toUrl());

		List<OlxOffer> offers = scrapper.getOffers();

		assertThat(offers)
				.isNotEmpty()
				.hasSize(scrapper.getOffersCount())
				.allMatch(o -> o.getCity().equals("Koszalin"));
	}

	@Test
	@Ignore("search by location is broken") // FIXME issue #27
	public void offerInRadius() throws Exception {
		OlxScrapper scrapper = new OlxScrapper(
				OlxQueryBuilder.query("nowy dom").location("Koszalin").radius(Radius._30KM).toUrl());

		List<OlxOffer> offers = scrapper.getOffers();

		assertThat(offers)
				.isNotEmpty()
				.hasSize(scrapper.getOffersCount())
				.areAtLeastOne(
						new Condition<>(o -> !o.getCity().equals("Koszalin"), "city is not %s", "Koszalin"));

		List<OlxOffer> offersInKoszalinOnly = new OlxScrapper(
				OlxQueryBuilder.query("nowy dom").location("Koszalin").toUrl()).getOffers();
		assertThat(offers).hasSizeGreaterThan(offersInKoszalinOnly.size()).containsAll(offersInKoszalinOnly);
	}

	@Test
	public void offerInPriceRange() throws Exception {
		OlxScrapper scrapper = new OlxScrapper(OlxQueryBuilder.query("czesci fiata").minPrice(2).maxPrice(9).toUrl());

		List<OlxOffer> offers = scrapper.getOffers();

		// price from 2 to 9, including fractions e.g. 3,50 zł
		assertThat(offers).isNotEmpty().allMatch(o -> o.getPrice().matches("[23456789](,\\d{2})? zł( do negocjacji)?"));
	}

	@Test
	public void offerInMinimumPriceRange() throws Exception {
		OlxScrapper scrapper = new OlxScrapper(OlxQueryBuilder.query("maskotka").minPrice(0).maxPrice(1).toUrl());

		List<OlxOffer> offers = scrapper.getOffers();

		assertThat(offers).isNotEmpty().allMatch(o -> o.getPrice().matches("Za darmo")
				|| o.getPrice().matches("1 zł( do negocjacji)?") || o.getPrice().isEmpty());
	}

	@Test
	public void offerForFree() throws Exception {
		OlxScrapper scrapper = new OlxScrapper(OlxQueryBuilder.query("maskotka").minPrice(0).toUrl());

		List<OlxOffer> offers = scrapper.getOffers();

		assertThat(offers).isNotEmpty().allMatch(o -> o.getPrice().matches("Za darmo"));
	}

	private Element getElement() throws IOException {
		String tag = readFile(testName.getMethodName() + ".htm");
		return Jsoup.parse(tag, "", Parser.xmlParser());
	}

	private String readFile(String name) throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(name).getFile());
		return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
	}

	private Condition<OlxOffer> hasNoPhoto() {
		return new Condition<>((o) -> !o.hasPhoto(), "has no photo");
	}
}
