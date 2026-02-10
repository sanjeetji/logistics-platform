package com.logistics.bff.b2c.client;

import com.logistics.platform.dto.customer.AddressDTO;
import com.logistics.platform.dto.customer.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "customer-service")
public interface CustomerServiceClient {
    
    @GetMapping("/api/v1/customers/{id}")
    CustomerDTO getCustomer(@PathVariable("id") String id);
    
    @PutMapping("/api/v1/customers/{id}")
    CustomerDTO updateCustomer(@PathVariable("id") String id, @RequestBody CustomerDTO customer);
    
    @GetMapping("/api/v1/customers/{id}/addresses")
    List<AddressDTO> getCustomerAddresses(@PathVariable("id") String id);
    
    @PostMapping("/api/v1/customers/{id}/addresses")
    AddressDTO addCustomerAddress(@PathVariable("id") String id, @RequestBody AddressDTO address);
}
