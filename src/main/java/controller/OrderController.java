package controller;

import enums.OrderStatus;
import model.Order;
import model.Table;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import repository.OrderRepository;
import repository.TableRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class OrderController {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;

    public OrderController(OrderRepository orderRepository, TableRepository tableRepository) {
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
    }

    @GetMapping("/orders")
    public List<Order> allOrders(){
        return orderRepository.findAll();
    }

    @PostMapping("/addOrder/{idTable}")
    public Order addOrder(
        @PathVariable int idTable,
        @RequestBody
        Order orderToBeAdd
    ){
        Table table = tableRepository.findById(idTable).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada."));
        if(!table.isAvailable()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A mesa não está disponível.");
        }
        orderToBeAdd.setTable(table);
        orderToBeAdd.setOrderDate(LocalDateTime.now());
        orderToBeAdd.setOrderStatus(OrderStatus.OPEN);
        table.setAvailable(false);
        tableRepository.save(table);
        orderRepository.save(orderToBeAdd);
        return orderToBeAdd;
    }

    @DeleteMapping("/removeOrder/{idToBeRemoved}")
        public void removeOrder(@PathVariable int idToBeRemoved){
        if(orderRepository.existsById(idToBeRemoved)){
            orderRepository.deleteById(idToBeRemoved);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Pedido não existe");
        }
        System.out.println("O Pedido " + idToBeRemoved + " foi removido!");
    }


}
