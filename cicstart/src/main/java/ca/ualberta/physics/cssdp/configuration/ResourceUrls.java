package ca.ualberta.physics.cssdp.configuration;

public class ResourceUrls {

	public static String _API = Common.properties().getString("api.url");

	public static String AUTH = _API + "/auth";
	public static String USER = AUTH + "/user";
	public static String SESSION = AUTH + "/session";

	public static String CATALOGUE = _API + "/catalogue";
	public static String PROJECT = CATALOGUE + "/project";

	public static String _MACRO = _API + "/macro";
	public static String MACRO = _MACRO + "/macro";

	public static String FILE = _API + "/file";
	public static String CACHE = FILE + "/cache";
	public static String HOST = FILE + "/host";

	public static String VFS = _API + "/vfs";
	public static String FILESYSTEM = VFS + "/filesystem";

}
