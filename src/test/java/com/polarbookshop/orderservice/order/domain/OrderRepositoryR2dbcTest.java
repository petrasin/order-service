package com.polarbookshop.orderservice.order.domain;

import com.polarbookshop.orderservice.config.DataConfig;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;


@DataR2dbcTest
@Import(DataConfig.class)
@Testcontainers
class OrderRepositoryR2dbcTest {

  @Container
  static PostgreSQLContainer<?> postgresql =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:14.4"));

  @Autowired
  private OrderRepository orderRepository;

  @DynamicPropertySource
  static void postgresqlProperties(DynamicPropertyRegistry registry) {
    System.err.println(postgresql.getJdbcUrl());


    registry.add("spring.r2dbc.url", OrderRepositoryR2dbcTest::r2dbcUrl);
    registry.add("spring.r2dbc.username", postgresql::getUsername);
    registry.add("spring.r2dbc.password", postgresql::getPassword);
    registry.add("spring.flyway.url", postgresql::getJdbcUrl);
  }

  private static String r2dbcUrl() {
    return "r2dbc:postgresql://%s:%s/%s".formatted(
        postgresql.getHost(),
        postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT),
        postgresql.getDatabaseName()
    );
  }

  @Test
  void createRejectedOrder() {
    var rejectedOrder = OrderService.buildRejectedOrder("1234567890", 3);

    StepVerifier.create(orderRepository.save(rejectedOrder))
                .expectNextMatches(order -> order.status()
                                                 .equals(OrderStatus.REJECTED))
                .verifyComplete();
  }

  @Test
  void whenCreateOrderNotAuthenticatedThenNoAuditMetadata() {
    var rejectedOrder = OrderService.buildRejectedOrder( "1234567890", 3);
    StepVerifier.create(orderRepository.save(rejectedOrder))
      .expectNextMatches(order -> Objects.isNull(order.createdBy()) && Objects.isNull(order.lastModifiedBy()))
    .verifyComplete();

  }

  @Test
  @WithMockUser("bjorn")
  void whenCreateOrderAuthenticatedThenNoAuditMetadata() {
    var rejectedOrder = OrderService.buildRejectedOrder( "1234567890", 3);
    StepVerifier.create(orderRepository.save(rejectedOrder))
      .expectNextMatches(order -> order.createdBy().equals("bjorn") && order.lastModifiedBy().equals("bjorn"))
    .verifyComplete();

  }

}