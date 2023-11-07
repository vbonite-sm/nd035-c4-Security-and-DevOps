package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	private Logger log = LoggerFactory.getLogger(CartController.class);
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		log.debug("Add item with id {} to Cart of user {}", request.getItemId(), request.getUsername());
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.error("[ADD TO CART] -> {FAIL} for username : " + request.getUsername() +", Cannot find user.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.error("[ADD TO CART] -> {FAIL} for itemid: "+ request.getItemId() +", Item not found");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);
		log.info("[ADD TO CART] -> {SUCCESS} save the new item for user : "+ user.getUsername());
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		log.debug("Remove item with id {} from Cart of user {}", request.getItemId(), request.getUsername());
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.error("[REMOVE FROM CART] -> {FAIL} username -> "+ request.getUsername() + " cannot found from db.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.error("[REMOVE FROM CART] -> {FAIL} cartId -> "+ request.getItemId() +" cannot found cartId in db.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);
		log.info("[REMOVE FROM USER] -> {SUCCESS} REMOVE cartid -> " + cart.getId() + ", quantity ->" + request.getQuantity() + ", from user ->" + user.getUsername());
		return ResponseEntity.ok(cart);
	}
		
}
