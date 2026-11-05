package pe.vichaval.gestionriego.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class PostgreSqlIntegrationBase {

    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:18-alpine")
                    .withDatabaseName("gestion_riego_test")
                    .withUsername("gestion_test")
                    .withPassword("gestion_test")
                    .withReuse(false);

    static {
        POSTGRES.start();

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    if (POSTGRES.isRunning()) {
                        POSTGRES.stop();
                    }
                }, "postgres-testcontainers-shutdown")
        );
    }

    @DynamicPropertySource
    static void databaseProperties(
            DynamicPropertyRegistry registry) {

        registry.add(
                "spring.datasource.url",
                POSTGRES::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                POSTGRES::getUsername
        );

        registry.add(
                "spring.datasource.password",
                POSTGRES::getPassword
        );

        registry.add(
                "spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver"
        );

        registry.add(
                "spring.datasource.hikari.maximum-pool-size",
                () -> "5"
        );

        registry.add(
                "spring.datasource.hikari.connection-timeout",
                () -> "10000"
        );
    }
}
