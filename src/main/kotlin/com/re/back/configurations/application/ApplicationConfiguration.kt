package com.re.back.configurations.application

import com.re.back.configurations.properties.JwtProperties
import com.re.back.repositories.auth.AppUsersRepository
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class ApplicationConfiguration(private val usersRepository: AppUsersRepository) {

    @Bean
    fun userDetailsService(): UserDetailsService? {
        return UserDetailsService { userName: String ->
            usersRepository.findByUserName(userName)
                .orElseThrow { UsernameNotFoundException("User not found") }
        }
    }

}