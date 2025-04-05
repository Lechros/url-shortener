package com.lechros.urlshortener.config

import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableTransactionManagement(order = 0)
@Configuration
class JpaConfig {
}
