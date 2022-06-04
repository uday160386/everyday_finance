package com.swotitup.secops;

import com.swotitup.secops.model.Customers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Map;

@RestController
@Validated
public class CustomerController {

    private static final String baseURL = "http://swotitup-secops-svc:8080/api";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private CustomerRepository repository;

    @RestController
    public class compare {
        // Find
        @GetMapping("/customers")
        String findAll() {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(baseURL, String.class);

            String response = String.valueOf(repository.findAll());
            logger.info("Value Received in Request - ");
            logger.info("Node Service Response - " + response);
            return  response;
        }

        // Save
        @PostMapping("/customers")
        @ResponseStatus(HttpStatus.CREATED)
        Customers newCustomer(@Valid @RequestBody Customers newCustomer) {
            return repository.save(newCustomer);
        }

        // Find
        @GetMapping("/customers/{id}")
        Customers findOne(@PathVariable @Min(1) Long id) {
            return repository.findById(id)
                    .orElseThrow(() -> new CustomerNotFoundException(id));
        }

        // Save or update
        @PutMapping("/customers/{id}")
        Customers saveOrUpdate(@RequestBody Customers newCustomer, @PathVariable Long id) {

            return repository.findById(id)
                    .map(x -> {
                        x.setFirstName(newCustomer.getFirstName());
                        x.setLastName(newCustomer.getLastName());
                        x.setAge(newCustomer.getAge());
                        return repository.save(x);
                    })
                    .orElseGet(() -> {
                        newCustomer.setId(id);
                        return repository.save(newCustomer);
                    });
        }

        // update author only
        @PatchMapping("/customers/{id}")
        Customers patch(@RequestBody Map<String, String> update, @PathVariable Long id) {

            return repository.findById(id)
                    .map(x -> {

                        String customer = update.get("customer");
                        if (!StringUtils.isEmpty(customer)) {
                            x.setFirstName(customer);

                            // better create a custom method to update a value = :newValue where id = :id
                            return repository.save(x);
                        } else {
                            throw new CustomerUnSupportedFieldPatchException(update.keySet());
                        }

                    })
                    .orElseGet(() -> {
                        throw new CustomerNotFoundException(id);
                    });

        }

        @DeleteMapping("/customers/{id}")
        void deleteBook(@PathVariable Long id) {
            repository.deleteById(id);
        }
    }
}
