package com.lotfizad.expreiment.backender;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class OrderRepositoryTest {
    @Test
    void findAll() {
        List<Order> orders = new OrderRepository().findAll();

        assertFalse(orders.isEmpty());

        Order expected = new Order().withId("order-110")
                .withDescription("yes distance, yes food, no vip")
                .withFood(true)
                .withVip(false)
                .withPickup(new Location(41.3965463, 2.1963997))
                .withDelivery(new Location(41.407834, 2.1675979));

        assertThat(orders, hasItems(expected));

    }
}