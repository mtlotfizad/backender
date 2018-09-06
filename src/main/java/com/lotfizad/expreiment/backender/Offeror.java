package com.lotfizad.expreiment.backender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@PropertySource(value = "classpath:application.properties")
public class Offeror {


    private List<String> boxRequiredList;
    private List<String> offeringPriority;
    private Integer distanceNoBike;
    private Float distanceSlot;
    private OrderRepository orderRepository;
    private CourierRepository courierRepository;
    private Map<String, TriConsumer<Offeror, Courier, List, List<OrderVM>>> methods;

    @Autowired
    public Offeror(@Value("#{'${box-required-orders}'.split(',')}") List<String> boxRequiredList,
                   @Value("#{'${offering-priority}'.split(',')}") List<String> offeringPriority,
                   @Value("${distance-no-bike}") Integer distanceNoBike,
                   @Value("${distance-slot}") Float distanceSlot,
                   OrderRepository orderRepository,
                   CourierRepository courierRepository) {
        this.boxRequiredList = boxRequiredList;
        this.offeringPriority = offeringPriority;
        this.distanceNoBike = distanceNoBike;
        this.distanceSlot = distanceSlot;
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;

        methods = new HashMap<>();
        methods.put("vipOrders", Offeror::vipOrders);
        methods.put("foodOrders", Offeror::foodOrders);
        methods.put("nearbyOrders", Offeror::nearbyOrders);
    }


    public List<OrderVM> offerOrdes(String courierId) {
        ArrayList<OrderVM> result = new ArrayList<>();
        Set<OrderVM> uniques = new HashSet<OrderVM>();

        Courier courier = courierRepository.findById(courierId);
        if (courier == null)
            return result;

        List<Order> orders = orderRepository.findAll();

        orders = refineByBox(courier, orders);
        orders = refineByVehicle(courier, orders);
        List<Order> orders2 = orders;

        offeringPriority.stream().map(i -> methods.get(i)).forEach(i -> {
            addOrders(result, uniques, i.accept(this, courier, orders2));
        });
//        addOrders(result, uniques, nearbyOrders(courier, orders));
//        addOrders(result, uniques, vipOrders(courier, orders));
//        addOrders(result, uniques, foodOrders(courier, orders));

        addOrders(result, uniques, orders2.stream().map(order -> new OrderVM(order)).collect(Collectors.toList()));

        return result;
    }

    /**
     * Drop orders at large distance if courier is not {@link Vehicle#MOTORCYCLE} or {@link Vehicle#ELECTRIC_SCOOTER}
     *
     * @param courier
     * @param orders
     * @return
     */
    List<Order> refineByVehicle(Courier courier, List<Order> orders) {
        if (courier.getVehicle().equals(Vehicle.MOTORCYCLE) || courier.getVehicle().equals(Vehicle.ELECTRIC_SCOOTER))
            return orders;
        List<Order> result = new ArrayList<Order>();
        for (Order ordr : orders)
            if (DistanceCalculator.calculateDistance(courier.getLocation(), ordr.getPickup()) < distanceNoBike)
                result.add(ordr);

        return result;
    }

    /**
     * Drop orders which need box if the given courier does not have box
     *
     * @param courier
     * @param orders
     * @return
     */
    List<Order> refineByBox(Courier courier, List<Order> orders) {
        if (courier.getBox())
            return orders;
        List<Order> result = new ArrayList<Order>();
        for (Order ordr : orders) {
            boolean contains = false;
            for (String requiredStr : boxRequiredList)
                if (ordr.getDescription().contains(requiredStr))
                    contains = true;
            if (!contains)
                result.add(ordr);
        }
        return result;
    }

    private void addOrders(ArrayList<OrderVM> result, Set<OrderVM> uniques, List<OrderVM> orderVMS) {
        for (OrderVM oVm : orderVMS)
            if (!uniques.contains(oVm)) {
                uniques.add(oVm);
                result.add(oVm);
            }
    }

    List<OrderVM> nearbyOrders(Courier courier, List<Order> orders) {
        List<OrderVM> result = new ArrayList<OrderVM>();
        for (Order order : orders)
            if (DistanceCalculator.calculateDistance(order.getPickup(), courier.getLocation()) < distanceSlot)
                result.add(new OrderVM(order));

        return result;
    }

    List<OrderVM> vipOrders(Courier courier, List<Order> orders) {
        return orders
                .stream()
                .filter(ordr -> ordr.getVip())
                .map(order -> new OrderVM(order))
                .collect(Collectors.toList());
    }

    List<OrderVM> foodOrders(Courier courier, List<Order> orders) {
        return orders
                .stream()
                .filter(ordr -> ordr.getFood())
                .map(order -> new OrderVM(order))
                .collect(Collectors.toList());
    }


}
