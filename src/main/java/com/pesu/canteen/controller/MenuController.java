package com.pesu.canteen.controller;

import com.pesu.canteen.model.entity.MenuItem;
import com.pesu.canteen.service.interfaces.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    // POST request to /api/menu (Used by Admins to add food)
    @PostMapping
    public ResponseEntity<MenuItem> addMenuItem(@RequestBody MenuItem item) {
        MenuItem savedItem = menuService.addMenuItem(item);
        return ResponseEntity.ok(savedItem);
    }

    // GET request to /api/menu (Used by Customers to view the menu)
    @GetMapping
    public ResponseEntity<List<MenuItem>> getMenu() {
        List<MenuItem> menu = menuService.getAvailableMenu();
        return ResponseEntity.ok(menu);
    }
}