package com.lotfizad.expreiment.backender;

import java.util.Objects;

/**
 * To be used for exposing order information through the API
 */
public class OrderVM {
    private String id;
    private String description;

    public OrderVM(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public OrderVM(Order ordr) {
        this.id = ordr.getId();
        this.description = ordr.getDescription();
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderVM orderVM = (OrderVM) o;
        return Objects.equals(id, orderVM.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
