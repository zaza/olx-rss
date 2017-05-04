package com.github.zaza.olx;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class OlxQueryBuilderTest {
	
	@Test
	public void getDescription() throws Exception {
		OlxQueryBuilder builder = OlxQueryBuilder.query("foo");

		assertThat(builder.getDescription()).isEqualTo("'foo'");
	}
	
	@Test
	public void getDescriptionWithPhotoOnly() throws Exception {
		OlxQueryBuilder builder = OlxQueryBuilder.query("foo").withPhotoOnly();

		assertThat(builder.getDescription()).isEqualTo("'foo' with photo");
	}
}
