package com.re.back

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.CrossOrigin

@SpringBootApplication
@OpenAPIDefinition(
    servers = [
        Server(
            url = "http://localhost:9898",
            description = "Local Project for ReShare"
        )
    ]
)
@CrossOrigin(origins = ["*"], maxAge = 3600, allowedHeaders = ["*"])
class BackApplication

fun main(args: Array<String>) {
    runApplication<BackApplication>(*args)
}
