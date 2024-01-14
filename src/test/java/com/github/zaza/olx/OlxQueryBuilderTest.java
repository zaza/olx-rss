package com.github.zaza.olx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

	@Test
	public void whenSpecifyingRadiusLocationMustBeProvidedFirst() {
		OlxQueryBuilder builder = OlxQueryBuilder.query("foo");

		assertThatThrownBy(() -> builder.radius(Radius._0KM))
				.isInstanceOf(IllegalStateException.class).hasMessage("Location must be provided first.");
	}

	@Test
	public void minPriceMustBeZeroAtLeast() {
		OlxQueryBuilder builder = OlxQueryBuilder.query("foo");

		assertThatThrownBy(() -> builder.minPrice(-1)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Min price must be 0 at least.");
	}

	@Test
	public void maxPriceMustBeZeroAtLeast() {
		OlxQueryBuilder builder = OlxQueryBuilder.query("foo");

		assertThatThrownBy(() -> builder.maxPrice(-1)).isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Max price must be 0 at least.");
	}

	@Test
	public void minPriceMustLessThenMaxPrice() {
		OlxQueryBuilder builder = OlxQueryBuilder.query("foo").maxPrice(1);

		assertThatThrownBy(() -> builder.minPrice(2))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Min price must be less than max price.");
	}

	@Test
	public void maxPriceMustMoreThenMinPrice() {
		OlxQueryBuilder builder = OlxQueryBuilder.query("foo").minPrice(2);

		assertThatThrownBy(() -> builder.maxPrice(1))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("Max price must be more than max price.");
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
