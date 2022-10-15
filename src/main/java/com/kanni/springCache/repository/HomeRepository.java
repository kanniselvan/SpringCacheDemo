package com.kanni.springCache.repository;

import com.kanni.springCache.model.HomeWages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeRepository extends JpaRepository<HomeWages,Integer> {

}
