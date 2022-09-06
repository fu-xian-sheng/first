package com.demo.test.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhigao.fu
 * @Description
 * @date 2022-09-06 14:13
 */
@Slf4j
@RestController("")
@RequestMapping("/public/info")
public class PublicController {

    @GetMapping("/getInfo")
    public String getInfo(){
        log.info("into----");
        return "welcome to test Jenkins";
    }
}
