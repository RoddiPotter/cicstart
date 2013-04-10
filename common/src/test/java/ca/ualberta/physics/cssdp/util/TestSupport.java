package ca.ualberta.physics.cssdp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.BeforeClass;

import ca.ualberta.physics.cssdp.configuration.ApplicationProperties;
import ca.ualberta.physics.cssdp.configuration.Common;

import com.coderod.db.migrations.Migrator;

public class TestSupport {

	private static boolean databaseIsSetup = false;

	@BeforeClass
	public static void setupDatabase() {

		if (!databaseIsSetup) {
			Common commonProperties = Common.properties();

			Properties overrides = new Properties();
			overrides.put("common.logback.configuration.xml",
					"src/test/resources/logback-test.xml");
			overrides.setProperty("common.hibernate.connection.url",
					"jdbc:postgresql://localhost/cssdp_test");
			ApplicationProperties.overrideDefaults(overrides);

			String url = commonProperties.getString("hibernate.connection.url");
			String driver = commonProperties
					.getString("hibernate.connection.driver_class");
			String user = commonProperties
					.getString("hibernate.connection.username");
			String password = commonProperties
					.getString("hibernate.connection.password");
			String scripts = "../database/migrations";

			recreateTestDatabase(driver, user, password);
			Migrator migrator = new Migrator(url, driver, user, password,
					scripts);
			migrator.initDb();
			migrator.migrateUpAll();
			databaseIsSetup = true;
		}
	}

	private static void recreateTestDatabase(String driver, String user,
			String password) {

		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(driver);

			conn = DriverManager.getConnection("jdbc:postgresql://localhost/"
					+ System.getProperty("user.name"), user, password);
			stmt = conn.createStatement();
			String sql = "DROP DATABASE if exists cssdp_test ";
			stmt.executeUpdate(sql);
			sql = "create DATABASE cssdp_test";
			stmt.executeUpdate(sql);
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException ignore) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}

	}

}
