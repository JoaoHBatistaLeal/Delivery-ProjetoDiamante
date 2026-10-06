package br.com.fiap.delivery.order.controller;

import br.com.fiap.delivery.order.entity.Dish;
import br.com.fiap.delivery.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dishes")
@CrossOrigin(origins = "*")
public class DishController {

    private final OrderService orderService;

    public DishController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Dish>> getDishes() {
        return ResponseEntity.ok(orderService.getAllDishes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getDishById(id));
    }
}
