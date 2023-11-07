package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.demo.TestUtils.CreateItem;
import static com.example.demo.TestUtils.CreateItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;
    @Mock
    private ItemRepository itemRepository;

    @Before
    public void setup(){
        when(itemRepository.findById(1L)).thenReturn(Optional.of(CreateItem(1)));
        when(itemRepository.findAll()).thenReturn(CreateItems());
        when(itemRepository.findByName("item")).thenReturn(Arrays.asList(CreateItem(1), CreateItem(2), CreateItem(3), CreateItem(4), CreateItem(5)));
    }

    @Test
    public void getItemsByAll(){
        ResponseEntity<List<Item>> itemResponse = itemController.getItems();
        assertNotNull(itemResponse);
        assertEquals(200, itemResponse.getStatusCodeValue());
        List<Item> itemList =itemResponse.getBody();
        assertNotNull(itemList);
        assertEquals(CreateItems(), itemList);
        assertEquals(5, itemList.size());
        verify(itemRepository, times(1)).findAll();
    }
    @Test
    public void getItemById(){
        ResponseEntity<Item> itemResponse = itemController.getItemById(1L);
        assertNotNull(itemResponse);
        assertEquals(200,itemResponse.getStatusCodeValue());
        Item item= itemResponse.getBody();
        assertNotNull(item);
        assertEquals(CreateItem(1L), item);
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    public void getItemsByName(){
        ResponseEntity<List<Item>> itemResponse = itemController.getItemsByName("item");
        assertNotNull(itemResponse);
        assertEquals(200,itemResponse.getStatusCodeValue());
        List<Item> itemList= itemResponse.getBody();
        assertNotNull(itemList);
        assertEquals(CreateItems(), itemList);
        assertEquals(5, itemList.size());
        verify(itemRepository, times(1)).findByName("item");
    }
}
