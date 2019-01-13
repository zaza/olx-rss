package com.github.zaza.olx;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OlxQueryBuilderTest {
	
	@Rule
    public ExpectedException thrown= ExpectedException.none();
	
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

	@Test
	public void whenSpecifiyingRadiusLocationMustBeProvidedFirst() {
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("Location must be provided first.");
		OlxQueryBuilder.query("foo").radius(Radius._0KM);
	}
}
