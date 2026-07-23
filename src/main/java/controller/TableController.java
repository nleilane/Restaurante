package controller;

import model.Table;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import repository.TableRepository;

import java.util.List;

@RestController

public class TableController {

    private final TableRepository tableRepository;

    public TableController(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @GetMapping("/tables")
    public List<Table> allTables(){

        return tableRepository.findAll();

    }

    @PostMapping("/addTable")
    public Table addTable(
        @RequestBody
        Table tableToBeAdd
    ){
        tableToBeAdd.setAvailable(true);
        tableRepository.save(tableToBeAdd);
        return tableToBeAdd;
    }

    @DeleteMapping("/deleteTable/{idToBeRemoved}")
    public void removeTable(@PathVariable int idToBeRemoved){
        if(tableRepository.existsById(idToBeRemoved)) {
        tableRepository.deleteById(idToBeRemoved);
        } else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mesa não encontrada.");
        }
        System.out.println("Mesa " + idToBeRemoved + " Removida!");

    }
}
