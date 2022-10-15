package com.kanni.springCache.service;

import com.kanni.springCache.model.HomeWages;

import java.util.List;
import java.util.Optional;

public interface HomeService {

    List<HomeWages> getAllWages();

    List<HomeWages>  insertWages(List<HomeWages> homeWagesList);

    List<HomeWages> updateWages(HomeWages homeWages,Integer wageId);

    List<HomeWages> getHomeWageById(Integer wageId);

    List<HomeWages> updateWagesAmount(Double amount, Integer wageId);

    boolean deleteWages(Integer wageId);
}
