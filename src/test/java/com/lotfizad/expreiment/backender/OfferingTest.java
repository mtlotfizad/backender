package com.lotfizad.expreiment.backender;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {API.class})
public class OfferingTest {

    //DI
    @Autowired
    Offeror offeror;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void invalidCourierTest() {
        List<OrderVM> orders = offeror.offerOrdes("");
        List<OrderVM> expected = new ArrayList<OrderVM>();
        assertEquals(expected, orders);
    }


    @Test
    public void nearbyOrders() {
        Courier courier = new CourierRepository().findById("courier-1");
        List<OrderVM> orderVMS = offeror.nearbyOrders(courier, orderRepository.findAll());

        Set<OrderVM> expected = new HashSet<OrderVM>();
        expected.add(new OrderVM("order-100", "yes distance, no food, no vip"));
        expected.add(new OrderVM("order-101", "yes distance, no food, yes vip"));
        expected.add(new OrderVM("order-110", "yes distance, yes food, no vip"));
        expected.add(new OrderVM("order-111", "yes distance, yes food, yes vip"));

        assertEquals(new HashSet<>(orderVMS), expected);
    }

    @Test
    public void foodOrders() {
        List<OrderVM> orderVMS = offeror.foodOrders(null, orderRepository.findAll());

        Set<OrderVM> expected = new HashSet<OrderVM>();
        expected.add(new OrderVM("order-110", "yes distance, yes food, no vip"));
        expected.add(new OrderVM("order-111", "yes distance, yes food, yes vip"));
        expected.add(new OrderVM("order-010", "no distance, yes food, no vip"));
        expected.add(new OrderVM("order-011", "no distance, yes food, yes vip"));

        assertEquals(new HashSet<>(orderVMS), expected);
    }

    @Test
    public void vipOrders() {
        List<OrderVM> orderVMS = offeror.vipOrders(null, orderRepository.findAll());

        Set<OrderVM> expected = new HashSet<OrderVM>();
        expected.add(new OrderVM("order-111", "yes distance, yes food, yes vip"));
        expected.add(new OrderVM("order-011", "no distance, yes food, yes vip"));
        expected.add(new OrderVM("order-101", "yes distance, no food, yes vip"));
        expected.add(new OrderVM("order-001", "no distance, no food, yes vip"));

        assertEquals(new HashSet<>(orderVMS), expected);
    }

    @Test
    public void refineByBox() {
        List<Order> orders = new ArrayList<Order>();
        Order pizza = new Order().withId("order-food")
                .withDescription("pizza")
                .withFood(true)
                .withVip(false)
                .withPickup(new Location(41.3965463, 2.1963997))
                .withDelivery(new Location(41.407834, 2.1675979));
        Order wood = new Order().withId("order-non-food")
                .withDescription("wood")
                .withFood(true)
                .withVip(false)
                .withPickup(new Location(41.3965463, 2.1963997))
                .withDelivery(new Location(41.407834, 2.1675979));
        orders.add(pizza);
        orders.add(wood);

        Courier courier = new Courier().withBox(false);
        List<Order> foodOrders = offeror.refineByBox(courier, orders);

        assertEquals(foodOrders, Arrays.asList(wood));

        courier = new Courier().withBox(true);
        foodOrders = offeror.refineByBox(courier, orders);
        assertEquals(foodOrders, Arrays.asList(pizza, wood));
    }

    @Test
    public void refineByDistance() {
        List<Order> orders = new ArrayList<Order>();
        Order far = new Order().withId("order-far")
                .withDescription("far")
                .withFood(true)
                .withVip(false)
                .withPickup(new Location(41.3965463, 2.1963997))
                .withDelivery(new Location(41.407834, 2.1675979));
        Order near = new Order().withId("order-near")
                .withDescription("near")
                .withFood(true)
                .withVip(false)
                .withPickup(new Location(41.2965363, 2.2))
                .withDelivery(new Location(41.407834, 2.1675979));
        orders.add(near);
        orders.add(far);

        Courier motorcycle = new Courier().withVehicle(Vehicle.MOTORCYCLE).withLocation(new Location(41.2965463, 2.1963997));
        assertEquals(offeror.refineByVehicle(motorcycle, orders), Arrays.asList(near, far));

        Courier bicycle = new Courier().withVehicle(Vehicle.BICYCLE).withLocation(new Location(41.2965463, 2.1963997));
        assertEquals(offeror.refineByVehicle(bicycle, orders), Arrays.asList(near));
    }

    @Test
    public void validCourierTest() {
        List<OrderVM> orders = offeror.offerOrdes("courier-1");
        List<OrderVM> expected = new ArrayList<OrderVM>();
        expected.add(new OrderVM("order-100", "yes distance, no food, no vip"));
        expected.add(new OrderVM("order-101", "yes distance, no food, yes vip"));
        expected.add(new OrderVM("order-110", "yes distance, yes food, no vip"));
        expected.add(new OrderVM("order-111", "yes distance, yes food, yes vip"));
        expected.add(new OrderVM("order-001", "no distance, no food, yes vip"));
        expected.add(new OrderVM("order-011", "no distance, yes food, yes vip"));
        expected.add(new OrderVM("order-010", "no distance, yes food, no vip"));
        expected.add(new OrderVM("order-000", "no distance, no food, no vip"));

        assertEquals(expected, orders);
    }

}