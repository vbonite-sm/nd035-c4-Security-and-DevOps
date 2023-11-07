package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class TestUtils {
    public static void injectObject(Object target, String fieldName, Object toInject){
        boolean wasPrivate = false;
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            if(!f.isAccessible()){
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);
            if(wasPrivate){
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static User CreateUser(){
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("user123");
        user.setCart(CreateCart(user));
        return user;
    }
    public static Cart CreateCart(User user){
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);
        List<Item> items = CreateItems();
        cart.setItems(CreateItems());
        cart.setTotal(items.stream().map(Item::getPrice).reduce(BigDecimal::add).get());
        return cart;
    }
    public static List<Item> CreateItems() {
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            items.add(CreateItem(i));
        }
        return items;
    }
    public static Item CreateItem(long id){
        Item item = new Item();
        item.setId(id);
        item.setPrice(BigDecimal.valueOf(50));
        item.setName("Item " + item.getId());
        item.setDescription("test description");
        return item;
    }
    public static List<UserOrder> CreateOrders(){
        List<UserOrder> orders = new ArrayList<>();
        for(int i = 0; i <=2; i++){
            UserOrder userOrder = new UserOrder();
            Cart cart = CreateCart(CreateUser());
            userOrder.setUser(CreateUser());
            userOrder.setId((long) i);
            userOrder.setItems(cart.getItems());
            userOrder.setTotal(cart.getTotal());
            orders.add(userOrder);
        }
        return orders;
    }
}
