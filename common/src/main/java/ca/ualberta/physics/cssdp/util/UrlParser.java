package ca.ualberta.physics.cssdp.util;

public class UrlParser {

	public static String getPath(String url) {

		if (url.contains("://")) {
			int pathIndex = url.indexOf("/", url.indexOf("://") + 3);
			String path = url.substring(pathIndex);
			return path.endsWith("/") ? path : path + "/";
		}

		return url;

	}

	public static String getHostname(String url) {

		if (url.contains("://")) {
			int indexOfColonSlashSlash = url.indexOf("://") + 3;
			String hostname = url.substring(indexOfColonSlashSlash,
					url.indexOf("/", indexOfColonSlashSlash));
			hostname = hostname.replaceAll(":\\d+", "");
			if (hostname.contains(".") || hostname.equals("localhost")) {
				return hostname;
			}
		}

		return null;
	}

	/**
	 * Returns the end of the path; the part of the path that is after the last
	 * "/"
	 * 
	 * @param url
	 * @return
	 */
	public static String getLeaf(String url) {
		String[] pathParts = UrlParser.getPath(url).split("/", -1);
		String leaf = pathParts[pathParts.length - 1];
		return leaf;
	}
}
