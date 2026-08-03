package controller;

import enums.OrderStatus;
import model.Dish;
import model.Order;
import model.Table;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import repository.DishRepository;
import repository.OrderRepository;
import repository.TableRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class OrderController {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final DishRepository dishRepository;

    public OrderController(OrderRepository orderRepository, TableRepository tableRepository, DishRepository dishRepository) {
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.dishRepository = dishRepository;
    }

    @GetMapping("/orders")
    public List<Order> allOrders() {
        return orderRepository.findAll();
    }

    @PostMapping("/orders/addOrder/{idTable}")
    public Order addOrder(@PathVariable int idTable, @RequestBody Order orderToBeAdd) {
        Table table = tableRepository.findById(idTable).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada."));
        if (!table.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A mesa não está disponível.");
        }
        orderToBeAdd.setTable(table);
        orderToBeAdd.setOrderDate(LocalDateTime.now());
        orderToBeAdd.setOrderStatus(OrderStatus.OPEN);
        table.setAvailable(false);
        tableRepository.save(table);
        return orderRepository.save(orderToBeAdd);
    }

    @PostMapping("/orders/{idOrder}/dishes/{idDish}")
    public Order addDishToOrder(@PathVariable int idOrder, @PathVariable int idDish) {
        Order order = orderRepository.findById(idOrder).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
        Dish dish = dishRepository.findById(idDish).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado."));
        if(!dish.isAvailable()){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O prato não está disponível."
            );
        }
        if(order.getOrderStatus() != OrderStatus.OPEN){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Não é possível alterar esse pedido.");
        }
        order.getDishes().add(dish);
        orderRepository.save(order);
        return order;
    }

    @GetMapping("/orders/{idOrder}")
    public Order findByOrder(@PathVariable int idOrder) {
        return orderRepository.findById(idOrder).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
    }

    @GetMapping("/orders/status/{status}")
    public List<Order> findByStatus(@PathVariable OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }

    @GetMapping("/orders/total")
    public BigDecimal getTotalPrice(@PathVariable int idOrder){
        Order order = orderRepository.findById(idOrder).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
        return order.totalPrice();
    }

    @PutMapping("/orders/{idOrder}/ready")
    public Order readyOrder(@PathVariable int idOrder){
        Order order = orderRepository.findById(idOrder).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
        if(order.getOrderStatus() != OrderStatus.OPEN){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apenas pedidos abertos podem ser marcadados como prontos.");
        }
        order.setOrderStatus(OrderStatus.READY);
        return orderRepository.save(order);
    }

    @PutMapping("/orders/{idOrder}/close")
    public Order closedOrder(@PathVariable int idOrder){
        Order order = orderRepository.findById(idOrder).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
        if (order.getOrderStatus() != OrderStatus.READY){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apenas pedidos prontos podem ser fechados.");
        }
        Table table = order.getTable();
        table.setAvailable(true);
        order.setOrderStatus(OrderStatus.CLOSED);
        tableRepository.save(table);
        return orderRepository.save(order);
    }

    @DeleteMapping("/orders/removeOrder/{idToBeRemoved}")
    public void removeOrder(@PathVariable int idToBeRemoved) {
        if (orderRepository.existsById(idToBeRemoved)) {
            orderRepository.deleteById(idToBeRemoved);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não existe");
        }
        System.out.println("O Pedido " + idToBeRemoved + " foi removido!");
    }
}