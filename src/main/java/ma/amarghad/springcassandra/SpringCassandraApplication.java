package ma.amarghad.springcassandra;

import ma.amarghad.springcassandra.repositories.ProductRepository;
import ma.amarghad.springcassandra.utils.ProductFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.Stream;

@SpringBootApplication
public class SpringCassandraApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCassandraApplication.class, args);
    }


    @Bean
    CommandLineRunner start(ProductRepository repository) {

        return args -> {
            Stream.generate(ProductFactory::get)
                    .limit(10)
                    .forEach(repository::save);
        };
    }

}
