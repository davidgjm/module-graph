package com.davidgjm.oss.artifactmanagement.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by david on 2017/3/14.
 */
@RestController
public class HomeController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    public static class Greeting {
        private final long id;
        private final String message;

        public Greeting(long id, String message) {
            this.id = id;
            this.message = message;
        }

        public long getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }
    }
}
