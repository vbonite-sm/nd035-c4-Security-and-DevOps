package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.demo.TestUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {
    @InjectMocks
    private CartController cartController;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ItemRepository itemRepository;

    @Before
    public void setUp(){
        when(userRepository.findByUsername("user")).thenReturn(CreateUser());
        when(itemRepository.findById(any())).thenReturn(Optional.of(CreateItem(1L)));
    }

    @Test
    public void AddToCart(){
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("user");
        cartRequest.setItemId(1L);
        cartRequest.setQuantity(15);

        ResponseEntity<Cart> responseEntity = cartController.addTocart(cartRequest);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        Cart cart = responseEntity.getBody();
        assertNotNull(cart);
        assertEquals("user", cart.getUser().getUsername());
        assertEquals(CreateItem(1L), cart.getItems().get(0));

        Cart NewCart = CreateCart(CreateUser());
        assertEquals(NewCart.getItems().size() + cartRequest.getQuantity(), cart.getItems().size());

        Item item = CreateItem(cartRequest.getItemId());
        BigDecimal itemPrice = item.getPrice();
        assertEquals(item.getPrice().multiply(BigDecimal.valueOf(cartRequest.getQuantity())).add(NewCart.getTotal()), cart.getTotal());
    }

    @Test
    public void RemoveItemFromCart(){
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("user");
        cartRequest.setItemId(1);
        cartRequest.setQuantity(1);

        ResponseEntity<Cart> cartResponseEntity = cartController.removeFromcart(cartRequest);
        assertNotNull(cartResponseEntity);
        assertEquals(200, cartResponseEntity.getStatusCodeValue());

        Cart cart = cartResponseEntity.getBody();
        Cart CompareCart = CreateCart(CreateUser());

        assertNotNull(cart);
        Item item = CreateItem(cartRequest.getItemId());
        BigDecimal itemPrice =item.getPrice();
        BigDecimal expectTotal = CompareCart.getTotal().subtract(itemPrice.multiply(BigDecimal.valueOf(cartRequest.getQuantity())));

        assertEquals("user", cart.getUser().getUsername());
        assertEquals(CompareCart.getItems().size() - cartRequest.getQuantity(), cart.getItems().size());
        assertEquals(CreateItem(2), cart.getItems().get(0));
        assertEquals(expectTotal, cart.getTotal());

        verify(cartRepository, times(1)).save(cart);
    }
}
