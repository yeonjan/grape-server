package pro.grape_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GrapeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrapeServerApplication.class, args);
    }

}
