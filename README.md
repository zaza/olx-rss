# OLX scrapper and RSS feed generator

---

The tool provides fluent API to query polish version of OLX.

Here is a sample request when looking for offers with photos, around a certain location, with a decent price:

```
URL url = OlxQueryBuilder
	.query("sprzedam opla")
	.withPhotoOnly()
	.location("Radom")
	.radius(Radius._30KM)
	.maxPrice(1000)
	.toUrl();
List<OlxOffer> offers = new OlxScrapper(url).getOffers();
```

Moreover, the tool allows to generate an RSS feed for OLX search results.