package ma.amarghad.springcassandra.services;

import lombok.RequiredArgsConstructor;
import ma.amarghad.springcassandra.dtos.ProductDTO;
import ma.amarghad.springcassandra.models.Product;
import ma.amarghad.springcassandra.repositories.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
