package com.re.back.configurations

import com.re.back.repositories.AppUsersRepository
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class ApplicationConfiguration(private val usersRepository: AppUsersRepository) {

    @Bean
    fun userDetailsService(): UserDetailsService? {
        return UserDetailsService { userName: String ->
            usersRepository
                .findByUserName(userName)
                .orElse(null)
        }
    }
}