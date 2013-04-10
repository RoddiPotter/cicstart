package ca.ualberta.physics.cssdp.vfs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ualberta.physics.cssdp.configuration.Common;

import com.coderod.db.migrations.Migrator;
import com.google.common.base.Throwables;

public class DatabaseInitializer {

	private static final Logger logger = LoggerFactory
			.getLogger(DatabaseInitializer.class);

	public static void testIfDbIsInitialized() {

		Connection conn = null;

		try {
			Class.forName(Common.properties().getString("hibernate.connection.driver_class"));
		} catch (ClassNotFoundException e2) {
			throw Throwables.propagate(e2);
		}
		String dbUrl = Common.properties().getString("hibernate.connection.url");
		String dbUser = Common.properties().getString("hibernate.connection.username");
		String dbPassword = Common.properties().getString("hibernate.connection.password");
		try {
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			conn.createStatement().executeQuery("select 1 from vfs_user;");
		} catch (SQLException e) {

			throw new RuntimeException("This database is not yet initialized!");

		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// ignore.
			}
		}

	}

	public static void initIfNecessary() {

		Connection conn = null;

		String driver = Common.properties().getString("hibernate.connection.driver_class");
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e2) {
			throw Throwables.propagate(e2);
		}
		String dbUrl = Common.properties().getString("hibernate.connection.url");
		String dbUser = Common.properties().getString("hibernate.connection.username");
		String dbPassword = Common.properties().getString("hibernate.connection.password");
		try {
			conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			conn.createStatement().executeQuery("select 1 from vfs_user");
			logger.info("VFS database exists, no need to create it.");
		} catch (SQLException e) {

			logger.warn("No user table, try initializing the database.");

			String migrationScriptDir = VfsServer.properties().getString(
					"db-migrator.migrations.dir");
			try {
				Migrator dbMigrator = new Migrator(dbUrl, driver, dbUser, dbPassword, migrationScriptDir);
				dbMigrator.initDb();
				dbMigrator.migrateUpAll();
			} catch (Exception e1) {
				throw Throwables.propagate(e1);
			}

		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				// ignore.
			}
		}

	}

}
