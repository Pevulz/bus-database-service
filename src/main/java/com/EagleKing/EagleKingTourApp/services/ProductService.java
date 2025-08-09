package com.EagleKing.EagleKingTourApp.services;

import com.EagleKing.EagleKingTourApp.entities.Product;
import com.EagleKing.EagleKingTourApp.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(String productId) {
        return productRepository.findById(productId);
    }

    public Product createProduct(Product product) {
        //Check if product exist in db
        Optional<Product> existingProduct = productRepository.findByNameIgnoreCase(product.getName());

        if (existingProduct.isPresent()) {
            Product updateProduct = existingProduct.get();
            updateProduct.setQuantity(updateProduct.getQuantity() + product.getQuantity());
            return productRepository.save(updateProduct);
        }

        return productRepository.save(product);
    }

    public Product updateProduct(String productId, Product updatedProduct) {
        //Find product
        Optional<Product> existingProduct = productRepository.findById(productId);

        if(existingProduct.isPresent()) {
            Product updateProduct = existingProduct.get();
            updateProduct.setName(updatedProduct.getName());
            updateProduct.setUnit(updatedProduct.getUnit());
            updateProduct.setRetailPrice(updatedProduct.getRetailPrice());
            updateProduct.setResellPrice(updatedProduct.getResellPrice());
            updateProduct.setQuantity(updatedProduct.getQuantity());
            updateProduct.setImgPath(updatedProduct.getImgPath());
            updateProduct.setType(updatedProduct.getType());
            return productRepository.save(updateProduct);
        }

        throw new IllegalArgumentException("Product does not exist.");
    }

    public Product adjustQuantity(String productId, int quantityChange) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        int newQuantity = product.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Insufficient quantity");
        }

        product.setQuantity(newQuantity);
        return productRepository.save(product);
    }
}
