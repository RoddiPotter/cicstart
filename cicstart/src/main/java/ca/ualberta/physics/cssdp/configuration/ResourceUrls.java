package ca.ualberta.physics.cssdp.configuration;

public class ResourceUrls {

	public static String _API = Common.properties().getString("api.url");

	private static String _AUTH = _API + "/auth";
	public static String USER = _AUTH + "/user";
	public static String SESSION = _AUTH + "/session";

	private static String _CATALOGUE = _API + "/catalogue";
	public static String PROJECT = _CATALOGUE + "/project";

	private static String _MACRO = _API + "/macro";
	public static String MACRO = _MACRO + "/macro";

	private static String _FILE = _API + "/file";
	public static String CACHE = _FILE + "/cache";
	public static String HOST = _FILE + "/host";

	private static String _VFS = _API + "/vfs";
	public static String FILESYSTEM = _VFS + "/filesystem";

}
