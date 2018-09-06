package com.lotfizad.expreiment.backender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@ComponentScan("com.lotfizad.backender")
@EnableAutoConfiguration
@PropertySource("classpath:messages.properties")
class API {
    private final String welcomeMessage;
    private final OrderRepository orderRepository;
    private final Offeror offeror;
    Logger logger = LoggerFactory.getLogger(API.class);


    @Autowired
    API(@Value("${backender.welcome_message}") String welcomeMessage, OrderRepository orderRepository, Offeror offeror) {
        this.welcomeMessage = welcomeMessage;
        this.orderRepository = orderRepository;
        this.offeror = offeror;
    }

    @RequestMapping("/")
    @ResponseBody
    String root() {
        return welcomeMessage;
    }

    @RequestMapping("/orders")
    @ResponseBody
    List<OrderVM> orders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> new OrderVM(order.getId(), order.getDescription()))
                .collect(Collectors.toList());
    }


    @RequestMapping(
            value = "/orders/{courierId}",
            produces = "application/json"
    )
    @ResponseBody
    List<OrderVM> ordersForcourier(@PathVariable("courierId") String courierId) {
        logger.info("accessing to orders of: " + courierId);
        return offeror.offerOrdes(courierId);
    }


    public static void main(String[] args) {
        SpringApplication.run(API.class);
    }
}
