package com.web.controllers;

import com.ecommerce.data.entities.Category;
import com.web.services.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/common")
public class CommonRestController extends BasicController {

    private final DataService dataService;

    @Autowired
    public CommonRestController(DataService dataService){
        this.dataService = dataService;
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories(){
        return dataService.getAllCategories();
    }
}
