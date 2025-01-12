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
