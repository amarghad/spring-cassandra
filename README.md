

# Spring data Cassandra

## Description
This project is a Spring Boot application designed to perform CRUD (Create, Read, Update, Delete) operations for managing products stored in a Cassandra database. It provides a robust and scalable solution for handling product data with seamless integration between Spring Boot and Cassandra.

## Cassandra cluster

### compose file

```yaml
services:
  cassandra:
    image: cassandra:latest
    container_name: cassandra
    ports:
      - "9042:9042"
    environment:
      CASSANDRA_CLUSTER_NAME: my_cluster
      CASSANDRA_DC: datacenter1
      CASSANDRA_NUM_TOKENS: 256
      CASSANDRA_SEEDS: cassandra
    volumes:
      - cassandra_data:/var/lib/cassandra
volumes:
  cassandra_data:

```


### Starting cluster

```bash
docker compose up -d
```

### Create keyspace


```bash
docker exec -it <ID> cqlsh
```


create keyspace
```bash
create keyspace store with replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
```


verification:

![keyspace](./docs/keyspace.png)


## Spring boot application


### Product

```java
package ma.amarghad.springcassandra.models;


import jakarta.annotation.PostConstruct;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
@ToString
public class Product {
    @Id
    @PrimaryKey
    private UUID id;
    private String name;
    private double price;
    private int quantity;
}

```


### ProductRepository

```java
package ma.amarghad.springcassandra.repositories;

import ma.amarghad.springcassandra.models.Product;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface ProductRepository extends CassandraRepository<Product, UUID> {
}

```


### ProductService

```java
package ma.amarghad.springcassandra.services;

import ma.amarghad.springcassandra.dtos.ProductDTO;
import ma.amarghad.springcassandra.models.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Product create(ProductDTO productDTO);
    Product update(UUID id, ProductDTO product);
    void delete(UUID id);
    Product get(UUID id);
    List<Product> getAll();

}

```


```java
@Service
@Transactional
@RequiredArgsConstructor
public class RepositoryProductService implements ProductService {

    private final ProductRepository productRepository;


    @Override
    public Product create(ProductDTO productDTO) {

        if(productDTO.name() == null || productDTO.name().isBlank())
            throw new IllegalArgumentException("Name cannot be null or empty");

        if(productDTO.price() < 0)
            throw new IllegalArgumentException("Price cannot be negative");

        if(productDTO.quantity() < 0)
            throw new IllegalArgumentException("Quantity cannot be negative");

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name(productDTO.name())
                .price(productDTO.price())
                .quantity(productDTO.quantity())
                .build();

        return productRepository.save(product);
    }

    @Override
    public Product update(UUID id, ProductDTO product) {

        Product current = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));


        if (product.name() != null && !product.name().isBlank())
            current.setName(product.name());

        if (product.price() > 0)
            current.setPrice(product.price());
        if (product.quantity() > 0)
            current.setQuantity(product.quantity());

        productRepository.save(current);

        return current;
    }



    @Override
    public void delete(UUID id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product get(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }
}
```


### ProductDTO

```java
package ma.amarghad.springcassandra.dtos;

public record ProductDTO(
        String name,
        double price,
        int quantity
) {
}

```


### ProductController

```java
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    protected final ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAll());
    }


    @GetMapping("{id}")
    public ResponseEntity<?> getProduct(@PathVariable("id") UUID id) {
        return  ResponseEntity.ok(productService.get(id));
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody ProductDTO productDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(productDTO));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") UUID id, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.update(id, productDTO));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

```


### Testing

```java
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

```


#### Application.properties file

```properties
spring.application.name=spring-cassandra
spring.cassandra.contact-points=localhost
spring.cassandra.port=9042
spring.cassandra.keyspace-name=store
spring.cassandra.local-datacenter=datacenter1
spring.cassandra.schema-action=recreate_drop_unused

```



### Results

#### Checking product table in store keyspace


![product-table](./docs/product_table.png)

#### Checking swagger documentation


![swagger-doc](./docs/swagger.png)


#### Download Api documentation

![download-doc](./docs/generated_api_doc.png)


#### Import Api documentation on Postman

![postman](./docs/import_api_post_man.png)


#### Get all

![get-all](./docs/all.png)


#### Create

![create](./docs/create.png)


#### Get By ID

![get-by-id](./docs/getByID.png)


#### Update

![update](./docs/update.png)


#### Delete

![delete](./docs/delete.png)

