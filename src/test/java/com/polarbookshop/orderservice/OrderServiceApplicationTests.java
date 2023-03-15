package com.polarbookshop.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
class OrderServiceApplicationTests {

  @Container
  static PostgreSQLContainer<?> postgresql =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:14.4"));

  @DynamicPropertySource
  static void postgresqlProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.r2dbc.url", OrderServiceApplicationTests::r2dbcUrl);
    registry.add("spring.r2dbc.username", postgresql::getUsername);
    registry.add("spring.r2dbc.password", postgresql::getPassword);
    registry.add("spring.flyway.url", postgresql::getJdbcUrl);
  }

  private static String r2dbcUrl() {
    return String.format("r2dbc:postgresql://%s:%s/%s", postgresql.getHost(),
        postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
        postgresql.getDatabaseName());
  }

  @Test
  void contextLoads() {
  }

}
