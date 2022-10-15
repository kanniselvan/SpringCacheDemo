package com.kanni.springCache.service.impl;

import com.kanni.springCache.exception.HomeApplicationError;
import com.kanni.springCache.model.HomeWages;
import com.kanni.springCache.repository.HomeRepository;
import com.kanni.springCache.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    HomeRepository homeRepository;

    @Override
    public List<HomeWages> getAllWages() {
        return homeRepository.findAll();
    }
    @Override
    @CacheEvict(cacheNames = "Homewages")
    public List<HomeWages> insertWages(List<HomeWages> homeWagesList) {
       return  homeRepository.saveAll(homeWagesList);
    }

    @Override
    @CachePut(cacheNames = "Homewages",key = "#wageId")
    public List<HomeWages> updateWages(HomeWages homeWages,Integer wageId) {
       Optional<HomeWages> homeWage=homeRepository.findById(wageId);
       if(homeWage.isPresent()){
           homeWages.setWagesId(wageId);
           homeWages.setWagesDate(LocalDateTime.now());
           homeRepository.save(homeWages);
           return getHomeWageById(wageId);
       }else
         throw new HomeApplicationError("Invalid wageId ....");
    }

    @Override
    @Cacheable(cacheNames = "Homewages",key = "#wageId")
    public List<HomeWages> getHomeWageById(Integer wageId) {
        return  homeRepository.findAllById(Collections.singleton(wageId));
    }

    @Override
    @CachePut(cacheNames = "Homewages",key = "#wageId")
    public  List<HomeWages> updateWagesAmount(Double amount, Integer wageId) {
         homeRepository.updateHomeWagesAmount(amount,wageId);
         return getHomeWageById(wageId);
    }

    @Override
    @CacheEvict(cacheNames = "Homewages", key = "#wageId")
    public boolean deleteWages(Integer wageId) {
         homeRepository.deleteById(wageId);
         return true;
    }

}
