package ma.amarghad.springcassandra.repositories;

import ma.amarghad.springcassandra.models.Product;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface ProductRepository extends CassandraRepository<Product, UUID> {
}
