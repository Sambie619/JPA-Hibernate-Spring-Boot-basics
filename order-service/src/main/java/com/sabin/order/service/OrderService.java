package com.sabin.order.service;

import com.sabin.order.dto.CreateOrderRequest;
import com.sabin.order.dto.ProductResponse;
import com.sabin.order.entity.Order;
import com.sabin.order.entity.OrderItem;
import com.sabin.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    public Order createOrder(CreateOrderRequest request) {

        Order order = new Order();
        order.setCustomerName(request.getCustomerName());

        double total = 0.0;

        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {

            String url = productServiceUrl + "/api/products/" + itemReq.getProductId();

            // Pull the latest product details so the order stores a snapshot of name and price.
            ProductResponse product = restTemplate.getForObject(url, ProductResponse.class);

            if (product == null) {
                throw new RuntimeException("Product not found: " + itemReq.getProductId());
            }

            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductPrice(product.getPrice());
            item.setQuantity(itemReq.getQuantity());

            double lineTotal = product.getPrice() * itemReq.getQuantity();
            item.setLineTotal(lineTotal);

            // Maintain both sides of the relationship so JPA can cascade the child rows on save.
            item.setOrder(order);
            order.getItems().add(item);

            total += lineTotal;
        }

        // Persist the already-computed total instead of recalculating it every time the order is read.
        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }
}

