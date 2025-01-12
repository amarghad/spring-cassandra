package ma.amarghad.springcassandra.web;


import lombok.RequiredArgsConstructor;
import ma.amarghad.springcassandra.dtos.ProductDTO;
import ma.amarghad.springcassandra.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    protected final ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAll());
    }


    @GetMapping(path = "{id}")
    public ResponseEntity<?> getProduct(@PathVariable("id") UUID id) {
        return  ResponseEntity.ok(productService.get(id));
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody ProductDTO productDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(productDTO));
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") UUID id, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.update(id, productDTO));
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
