package com.pesu.canteen.service.impl;

import com.pesu.canteen.model.entity.MenuItem;
import com.pesu.canteen.repository.MenuRepository;
import com.pesu.canteen.service.interfaces.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepository menuRepository;

    @Override
    public MenuItem addMenuItem(MenuItem item) {
        item.setAvailable(true); 
        return menuRepository.save(item);
    }

    @Override
    public List<MenuItem> getAvailableMenu() {
        return menuRepository.findByAvailableTrue();
    }
}