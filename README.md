# OLX scrapper and RSS feed generator

---

![Travis CI status](https://travis-ci.org/zaza/olx-rss.svg?branch=master)

The tool provides fluent API to query polish OLX page.

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

Sample RSS feed hosted on Heroku (the site might be down if it ran out of [free dyno hours](https://devcenter.heroku.com/articles/free-dyno-hours)):
* [sprzedam opla](http://olx-rss.herokuapp.com/rss?string=sprzedam%20opla)

---

Go [here](https://github.com/zaza/olx-rss/issues) for open issues and upcoming features.