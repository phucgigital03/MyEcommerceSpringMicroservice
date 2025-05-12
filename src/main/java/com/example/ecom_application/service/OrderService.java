package com.example.ecom_application.service;

import com.example.ecom_application.dto.OrderResponse;
import com.example.ecom_application.model.*;
import com.example.ecom_application.repository.OrderRepository;
import com.example.ecom_application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    public Optional<OrderResponse> createOrder(String userId) {
        // Validate for cart items
        List<CartItem> cartItems = cartService.getCart(userId);
        if (cartItems.isEmpty()) {
            return Optional.empty(); // No items in cart
        }
        // Validate for user
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();

        // Calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(cartItem ->{
                    BigDecimal price = cartItem.getPrice();
                    return price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(
                        null,
                        item.getQuantity(),
                        item.getPrice(),
                        order,
                        item.getProduct()
                ))
                .toList();

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        // Clear cart items
        cartService.clearCart(userId);

        // Create OrderResponse
        OrderResponse orderResponse;
        orderResponse = modelMapper.map(savedOrder, OrderResponse.class);
        orderResponse.getItems().forEach(item -> {
            item.setSubTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        });
        return Optional.of(orderResponse);
    }
}
