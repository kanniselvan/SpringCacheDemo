package com.kanni.springCache.repository;

import com.kanni.springCache.model.HomeWages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface HomeRepository extends JpaRepository<HomeWages,Integer> {

    @Transactional
    @Modifying
    @Query("update HomeWages c set c.amount = ?1 where c.wagesId = ?2")
    int updateHomeWagesAmount(Double amount, Integer wageId);


}
