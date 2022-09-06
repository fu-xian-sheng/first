package com.demo.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhigao.fu
 * @Description
 * @date 2022-09-06 14:13
 */
@RestController("")
@RequestMapping("/public/info")
public class PublicController {

    @GetMapping("/getInfo")
    public String getInfo(){
        System.out.println("进来了");
        return "成功";
    }
}
