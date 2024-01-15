package com.github.zaza.olx;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class SearchResult {

	private final URL url;
	private final String filterDescription;
	private final List<OlxOffer> items;

}
