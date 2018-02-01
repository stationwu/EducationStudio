package com.edu.domain.dto;

import com.edu.domain.Order;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class OrderContainer {

    @JsonIgnore
    private Order order;

    @JsonProperty("items")
    private List<ProductContainer> productContainers;

    public OrderContainer(Order order) {
        this.order = order;
//this.productContainers = Stream.of(order.getCourseProductsMap().keySet().stream()).flatMap(i -> i).collect(Collectors.toCollection(ArrayList::new));

        Stream<ProductContainer> courseStream = order.getCourseProductsMap().entrySet().stream()
                .map(x -> new ProductContainer(x.getKey(), x.getValue()));

        this.productContainers = courseStream.collect(Collectors.toCollection(ArrayList::new));
    }

    public String getId() {
        return order.getId();
    }

    public Order.Status getStatus() {
        return order.getStatus();
    }

    public String getStatusText() {
        return order.getStatusText();
    }

    public BigDecimal getTotalAmount() {
        return order.getTotalAmount();
    }

    public String getCreationDate() {
        return order.getDate();
    }

    public int getItemCount() {
        return productContainers.size();
    }
}
