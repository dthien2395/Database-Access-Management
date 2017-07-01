package com.MyDAM;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dthien on 6/30/2017.
 */
@NoRepositoryBean
public interface Repository<T, ID extends Serializable> {
    List<T> findAll();

    T findById(Long id);
}
