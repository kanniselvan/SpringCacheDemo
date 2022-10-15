package com.kanni.springCache.service.impl;

import com.kanni.springCache.model.HomeWages;
import com.kanni.springCache.repository.HomeRepository;
import com.kanni.springCache.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    HomeRepository homeRepository;

    @Override
    public List<HomeWages> getAllWages(Optional<Integer> wageId) {
        if(wageId.isPresent())
            return homeRepository.findAllById(Collections.singleton(wageId.get()));

        return homeRepository.findAll();
    }

    @Override
    public boolean insertWages(List<HomeWages> homeWagesList) {
         homeRepository.saveAll(homeWagesList);
         return true;
    }

}
