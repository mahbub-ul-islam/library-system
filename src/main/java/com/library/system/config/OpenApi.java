package com.library.system.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * Created by mahbub.islam on 18/5/2024.
 */
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Mahbub Ul Islam",
                        email = "mahbub_ul_islam@outlookcom",
                        url = "https://github.com/mahbub-ul-islam"
                ),
                description = "OpenApi documentation for Spring Security",
                title = "OpenApi specification - Mahbub",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Local environment",
                        url = "http://localhost:8080"
                )
        }
)
public class OpenApi {
}
