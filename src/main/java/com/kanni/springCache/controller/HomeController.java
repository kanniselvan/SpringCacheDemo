package com.kanni.springCache.controller;

import com.kanni.springCache.model.HomeWages;
import com.kanni.springCache.model.ResponseObject;
import com.kanni.springCache.service.HomeService;
import com.kanni.springCache.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    HomeService homeService;

    @GetMapping()
    public ResponseObject getAllHomeWages(@RequestParam(value = "wageId" ,required = false) Optional<Integer> wageId){
        List<HomeWages> responseList=homeService.getAllWages(wageId);
        return Optional.ofNullable(responseList).isPresent()?
         ResponseUtils.getResult(HomeWages.class,responseList): ResponseUtils.noDataFound();


    }

    @PostMapping()
    public ResponseObject insertHomeWages(@RequestBody List<HomeWages> homeWagesList){
        boolean flag=homeService.insertWages(homeWagesList);
         if(flag)
             return ResponseUtils.successMessage("Successfully inserted!!!");

         return ResponseUtils.errorMessage("Something went wrong!!!!");
    }

}
