package com.EagleKing.EagleKingTourApp.services;

import com.EagleKing.EagleKingTourApp.entities.Product;
import com.EagleKing.EagleKingTourApp.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    //Need to fix same products with different price

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(String productId) {
        return productRepository.findById(productId);
    }

    public Product createProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        System.out.println("Creating product: " + product.getName());

        //1. Validate inputs
        validateProductInputs(product);

        //2. Check if name exist
        Optional<Product> existingProduct = productRepository.findByNameIgnoreCase(product.getName());

        if (existingProduct.isPresent()) {
            Product existing = existingProduct.get();

            // If same product with same prices, add to quantity
            if (existing.getRetailPrice() == product.getRetailPrice() &&
                    existing.getResellPrice() == product.getResellPrice() &&
                    existing.getUnit().equalsIgnoreCase(product.getUnit()) &&
                    existing.getType().equalsIgnoreCase(product.getType())) {

                existing.setQuantity(existing.getQuantity() + product.getQuantity());
                System.out.println("Updated quantity for existing product: " + existing.getName() +
                        " to " + existing.getQuantity());
                return productRepository.save(existing);
            } else {
                // Same name but different prices/unit/type - this is a conflict
                System.out.println("Product conflict: " + product.getName() +
                        " already exists with different specifications");
                throw new IllegalArgumentException(
                        "Product '" + product.getName() + "' already exists with different specifications. " +
                                "Use a different name or update the existing product."
                );
            }
        }
        else {
            System.out.println("Saving new product: " + product.getName());
            return productRepository.save(product);
        }
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

    private void validateProductInputs(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (product.getRetailPrice() < 0) {
            throw new IllegalArgumentException("Retail price must be greater than 0");
        }
        if (product.getResellPrice() < 0) {
            throw new IllegalArgumentException("Resell price must be greater than 0");
        }
        if (product.getUnit() == null || product.getUnit().trim().isEmpty()) {
            throw new IllegalArgumentException("Product unit is required");
        }
        if (product.getType() == null || product.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Product type is required");
        }
        if (product.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (product.getResellPrice() < product.getRetailPrice()) {
            throw new IllegalArgumentException("Resell price is lower than retail price");
        }
    }
}
