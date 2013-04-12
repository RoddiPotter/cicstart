/* ============================================================
 * UrlParser.java
 * ============================================================
 * Copyright 2013 University of Alberta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============================================================ 
 */
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
