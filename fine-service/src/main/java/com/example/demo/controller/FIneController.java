package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.FineService;

@RestController
@RequestMapping("/api/fines")
public class FIneController {
	@Autowired
	private FineService fineService;

}
