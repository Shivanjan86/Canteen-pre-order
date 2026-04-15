package com.pesu.canteen.repository;

import com.pesu.canteen.model.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByAvailableTrue();
}