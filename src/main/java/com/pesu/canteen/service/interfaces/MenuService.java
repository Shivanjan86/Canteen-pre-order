package com.pesu.canteen.service.interfaces;

import com.pesu.canteen.model.entity.MenuItem;
import java.util.List;

public interface MenuService {
    MenuItem addMenuItem(MenuItem item);
    List<MenuItem> getAvailableMenu();
}