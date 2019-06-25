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

	@Test
	public void minPriceMustBeZeroAtLeast() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Min price must be 0 at least.");
		OlxQueryBuilder.query("foo").minPrice(-1);
	}

	@Test
	public void maxPriceMustBeZeroAtLeast() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Max price must be 0 at least.");
		OlxQueryBuilder.query("foo").maxPrice(-1);
	}

	@Test
	public void minPriceMustLessThenMaxPrice() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Min price must be less than max price.");
		OlxQueryBuilder.query("foo").maxPrice(1).minPrice(2);
	}

	@Test
	public void maxPriceMustMoreThenMinPrice() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Max price must be more than max price.");
		OlxQueryBuilder.query("foo").minPrice(2).maxPrice(1);
	}

	@Test
	public void minPriceDefinedTwice() {
		OlxQueryBuilder builder = OlxQueryBuilder.query("foo").minPrice(2).minPrice(1);
		
		assertThat(builder.getPrice().lowerEndpoint()).isEqualTo(1);
		assertThat(builder.getPrice().hasUpperBound()).isFalse();
	}

	@Test
	public void maxPriceDefinedTwice() {
		OlxQueryBuilder builder = OlxQueryBuilder.query("foo").maxPrice(2).maxPrice(1);
		
		assertThat(builder.getPrice().hasLowerBound()).isFalse();
		assertThat(builder.getPrice().upperEndpoint()).isEqualTo(1);
	}

	@Test
	public void minAndMaxPriceDefined() {
		OlxQueryBuilder builder = OlxQueryBuilder.query("foo").minPrice(1).maxPrice(2);
		
		assertThat(builder.getPrice().lowerEndpoint()).isEqualTo(1);
		assertThat(builder.getPrice().upperEndpoint()).isEqualTo(2);
	}

	@Test
	public void maxAndMinPriceDefined() {
		OlxQueryBuilder builder = OlxQueryBuilder.query("foo").maxPrice(2).minPrice(1);
		
		assertThat(builder.getPrice().lowerEndpoint()).isEqualTo(1);
		assertThat(builder.getPrice().upperEndpoint()).isEqualTo(2);
	}
}
