package controller;

import model.Dish;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import repository.DishRepository;

import java.util.List;

@RestController
public class DishController {
    private final DishRepository dishRepository;


    public DishController(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @GetMapping("/dishes")
    public List<Dish> allDishes(){
        return dishRepository.findAll();
    }

    @PostMapping("/addDish")
    public Dish addDish(
            @RequestBody
            Dish dishToBeAdd
    ){
        dishToBeAdd.setAvailable(true);
        dishRepository.save(dishToBeAdd);
        return dishToBeAdd;

    }

    @GetMapping("/findDish/{idDish}")
    public Dish findDish(
        @PathVariable int idDish

    ){
        return dishRepository.findById(idDish).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prato não encontrado"));
    }

    @DeleteMapping("/deleteDish/{idToBeRemoved}")
    public void removeDish(@PathVariable int idToBeRemoved){
        if(dishRepository.existsById(idToBeRemoved)){
            dishRepository.deleteById(idToBeRemoved);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"O prato não existe");
        }
        System.out.println("Prato " + idToBeRemoved + "removido!");
    }

}
