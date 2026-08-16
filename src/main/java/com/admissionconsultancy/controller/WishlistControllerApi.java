package com.admissionconsultancy.controller;

import com.admissionconsultancy.dto.WishlistDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Wishlist Management", description = "APIs for managing user wishlists")
@RequestMapping("/api/wishlist")
public interface WishlistControllerApi {

    @GetMapping
    ResponseEntity<List<WishlistDTO>> getUserWishlist();
}
