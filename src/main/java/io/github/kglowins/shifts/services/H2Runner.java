package io.github.kglowins.shifts.services;

import io.jenetics.facilejdbc.UncheckedSQLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Singleton;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.h2.tools.Server;

@Singleton
@Slf4j
public class H2Runner {

    public static final String H2_URL = "jdbc:h2:mem:shifts;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;";
    public static final String H2_USER = "sa";
    private static final String TCP_PORT = "8082";
    private static final String WEB_PORT = "8083";
    private static final List<String> SCRIPTS = List.of(
        "0000-wipe.sql",
        "0001-initial-schema.sql"
    );
    private static final String SCRIPT_PATH_TEMPLATE = "/sql/%s";

    private static Boolean isUp = false;

    public H2Runner() {
        try {
            startH2WithSchema();
        } catch (SQLException e) {
            log.error("Failed to start H2", e);
            isUp = false;
            throw new UncheckedSQLException(e);
        }
    }

    private void startH2WithSchema() throws SQLException {
        if (!isUp) {
            log.info("Starting H2");
            Server h2TcpServer = Server.createTcpServer("-tcpPort", TCP_PORT, "-tcpAllowOthers");
            h2TcpServer.start();
            Server h2WebServer = Server.createWebServer("-webPort", WEB_PORT, "-webAllowOthers");
            h2WebServer.start();
            isUp = true;
        }
        SCRIPTS.forEach(this::runSQLScript);
    }

    public DataSource getDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(H2_URL);
        dataSource.setUser(H2_USER);
        return dataSource;
    }

    private void runSQLScript(String scriptName) {
        log.info("Running SQL script {}", scriptName);
        String scriptResourcePath = String.format(SCRIPT_PATH_TEMPLATE, scriptName);
        log.debug("scriptResourcePath={}", scriptResourcePath);
        InputStream script = H2Runner.class.getResourceAsStream(scriptResourcePath);
        try (InputStreamReader scriptReader = new InputStreamReader(script)) {
            RunScript.execute(getDataSource().getConnection(), scriptReader);
        } catch (IOException e) {
            log.error("Failed to read SQL script {}", scriptResourcePath, e);
            throw new UncheckedIOException(e);
        } catch (SQLException e) {
            log.error("Failed to run SQL script {}", scriptResourcePath, e);
            throw new UncheckedSQLException(e);
        }
    }
}
