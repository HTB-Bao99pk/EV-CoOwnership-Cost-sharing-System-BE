package swp302.topic6.evcoownership.utils;



import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger{

    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EV Sharing Platform API")
                        .description("API cho hệ thống chia sẻ xe điện - Dự án sinh viên")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nhóm EV Sharing")
                                .email("evsharing@example.com")
                                .url("http://localhost:8080")
                        )
                );
    }
}
