package com.github.zaza;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.before;
import static spark.Spark.halt;

import com.github.zaza.olx.OlxClient;
import com.google.common.base.Strings;

import spark.FilterImpl;
import spark.Request;
import spark.Response;

public class OlxRss {

	private OlxRss() {
	}

	public static void main(String[] args) {
		port(Integer.valueOf(System.getenv("PORT")));
		before(new TokenAuthenticationFilter(System.getenv("TOKEN")));
		get("/rss", (req, res) -> {
			res.type("application/rss+xml");
			return new FeedWriter().write(new OlxClient(req).search());
		});
	}

	private static class TokenAuthenticationFilter extends FilterImpl {

		private final String validToken;

		protected TokenAuthenticationFilter(String token) {
			super("/*", "*/*");
			this.validToken = token;
		}

		@Override
		public void handle(Request req, Response res) throws Exception {
			if (Strings.isNullOrEmpty(validToken)) {
				halt(500, "token not configured");
			}
			String token = req.queryParams("token");
			if (token == null) {
				halt(401, "missing token");
			}
			if (!validToken.equals(token)) {
				halt(401, "invalid token");
			}
		}
	}

}
