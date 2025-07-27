//package com.tanishka.ecommerce.controller;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import com.tanishka.ecommerce.serviceimpl.KafkaProducerService;
//
//@RestController
//@RequestMapping("/kafka")
//public class KafkaController {
//
//    @Autowired
//    private KafkaProducerService producerService;
//
//    @PostMapping("/publish")
//    public String publishMessage(@RequestParam String message) {
//        producerService.sendMessage(message);
//        return "Message sent: " + message;
//    }
//}
//
