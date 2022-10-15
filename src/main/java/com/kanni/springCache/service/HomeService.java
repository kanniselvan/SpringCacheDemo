package com.kanni.springCache.service;

import com.kanni.springCache.model.HomeWages;

import java.util.List;
import java.util.Optional;

public interface HomeService {

    List<HomeWages> getAllWages(Optional<Integer> wageId);

    boolean insertWages(List<HomeWages> homeWagesList);
}
