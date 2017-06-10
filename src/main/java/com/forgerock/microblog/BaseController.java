package com.forgerock.microblog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles request to /
 */
@RestController
public class BaseController {

    private final static Logger LOG = LoggerFactory.getLogger(BaseController.class);

    @RequestMapping("/")
    public String home() {

        return "Hello World";
    }

    @GetMapping("/hello")
    public Greeting get() {

        LOG.info("/blog");
        return new Greeting();
    }

    private static class Greeting {

        String hello = "Hello";
    }

}
