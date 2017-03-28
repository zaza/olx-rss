package com.github.zaza;

import static spark.Spark.get;
import static spark.SparkBase.port;

import com.github.zaza.olx.OlxClient;

public class OlxRss {

	public static void main(String[] args) {
		port(Integer.valueOf(System.getenv("PORT")));
		get("/rss", (req, res) -> new FeedWriter()
				.write(new OlxClient(req).search()));
	}

}
