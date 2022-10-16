package com.kanni.springCache.controller;

import com.kanni.springCache.exception.HomeApplicationError;
import com.kanni.springCache.model.HomeWages;
import com.kanni.springCache.model.ResponseObject;
import com.kanni.springCache.service.HomeService;
import com.kanni.springCache.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    HomeService homeService;

    @GetMapping
    public ResponseObject getAllHomeWages(@RequestParam(value = "wageId" ,required = false) Optional<Integer> wageId){
        List<HomeWages> responseList=new ArrayList<>();
        if(wageId.isPresent())
            responseList=homeService.getHomeWageById(wageId.get());
        else
            responseList=homeService.getAllWages();

        return Optional.ofNullable(responseList).isPresent()?
         ResponseUtils.getResult(HomeWages.class,responseList): ResponseUtils.noDataFound();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('Admin')")
    public ResponseObject insertHomeWages(@RequestBody List<HomeWages> homeWagesList){
        List<HomeWages> responseList=homeService.insertWages(homeWagesList);
        return Optional.ofNullable(responseList).isPresent()?
                ResponseUtils.getResult(HomeWages.class,responseList): ResponseUtils.noDataFound();
    }

    @PostMapping("/{wageId}")
    public ResponseObject updateHomeWages(@RequestBody HomeWages homeWages,@PathVariable Optional<Integer> wageId) {

        if(!wageId.isPresent()){
            throw new HomeApplicationError("wageId must not be null!!!");
        }

        List<HomeWages> responseList=homeService.updateWages(homeWages,wageId.get());
        return Optional.ofNullable(responseList).isPresent()?
                ResponseUtils.getResult(HomeWages.class,responseList): ResponseUtils.noDataFound();

    }
    @PutMapping("/{wageId}/{amount}")
    public ResponseObject updatePartiallyHomeWages(@PathVariable Optional<Integer> wageId , @PathVariable Optional<Double> amount) {

        if(!wageId.isPresent()){
            throw new HomeApplicationError("wageId must not be null!!!");
        }
        if(!amount.isPresent()){
            throw new HomeApplicationError("amount must not be null!!!");
        }
        List<HomeWages> responseList=homeService.updateWagesAmount(amount.get(),wageId.get());
            return Optional.ofNullable(responseList).isPresent()?
                    ResponseUtils.getResult(HomeWages.class,responseList): ResponseUtils.noDataFound();
    }

    @DeleteMapping("/{wageId}")
    public ResponseObject deleteWages(@PathVariable Optional<Integer> wageId){
        if(!wageId.isPresent()){
            throw new HomeApplicationError("wageId must not be null!!!");
        }
        if(homeService.deleteWages(wageId.get()))
        return ResponseUtils.successMessage(wageId.get()+ " wages deleted successfully!!!");

        return ResponseUtils.errorMessage("Something went wrong!!!!");
    }



}
