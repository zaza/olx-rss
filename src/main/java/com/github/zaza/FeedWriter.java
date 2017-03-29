package com.github.zaza;

import static java.lang.String.format;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.github.zaza.olx.OlxOffer;
import com.github.zaza.olx.SearchResult;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.feed.synd.SyndImageImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

public class FeedWriter {

	public String write(SearchResult result) throws IOException, FeedException {
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("rss_2.0");

		feed.setTitle(format("OLX.pl \"%s\"", result.getFilterDescription()));
		feed.setLink(result.getQueryUrl());
		feed.setDescription("Ogłoszenia spełniające Twoje kryteria wyszukiwania");

		SyndImage image = new SyndImageImpl();
		image.setTitle("OLX.pl");
		image.setUrl("https://www.olx.pl/favicon.ico");
		image.setLink("https://www.olx.pl");
		feed.setImage(image);

		List<SyndEntry> entries = result.getItems().stream().map(i -> feedEntry(i)).collect(Collectors.toList());
		feed.setEntries(entries);
		return write(feed);
	}

	private SyndEntry feedEntry(OlxOffer item) {
		SyndEntry entry = new SyndEntryImpl();
		entry.setTitle(item.getTitle());
		entry.setLink(item.getUri().toString());
		entry.setPublishedDate(new Date());
		entry.setDescription(createDescription(item));
		return entry;
	}

	private SyndContent createDescription(OlxOffer item) {
		SyndContent description = new SyndContentImpl();
		description.setType("text/html");
		StringBuilder sb = new StringBuilder();
		sb.append(format("Cena: %s<br />", item.getPrice()));
		sb.append(format("Lokalizacja: %s<br />", item.getCity()));
		sb.append(formatPhoto(item.getPhoto()));
		description.setValue(sb.toString());
		return description;
	}

	private String formatPhoto(URI photo) {
		return photo != null ? asImg(photo) : "";
	}
	
	private String asImg(URI photo) {
		return format("<img src=\"%s\" width=\"128\" height=\"96\" alt=\"\" ><br />", photo.toString());
	}

	private String write(SyndFeed feed) throws IOException, FeedException {
		Writer writer = new StringWriter();
		SyndFeedOutput output = new SyndFeedOutput();
		output.output(feed, writer);
		writer.close();
		return writer.toString();
	}
}
