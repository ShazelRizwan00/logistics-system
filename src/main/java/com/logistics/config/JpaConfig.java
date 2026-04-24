package com.logistics.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** hello
 * JPA / Hibernate configuration.
 *
 * @EnableJpaAuditing: activates Spring Data's auditing support so that
 *   @CreatedDate and @LastModifiedDate fields are auto-populated.
 *   Requires @EntityListeners(AuditingEntityListener.class) on each entity.
 *
 * @EnableTransactionManagement: enables @Transactional processing via AOP.
 *   Spring Boot auto-enables this, but declaring it explicitly documents intent.
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.logistics.repository")
@EnableTransactionManagement
public class JpaConfig {
    // No additional beans needed – all configuration lives in application.yml
}
