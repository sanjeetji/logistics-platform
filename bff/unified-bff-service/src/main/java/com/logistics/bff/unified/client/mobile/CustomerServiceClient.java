package com.logistics.bff.unified.client.mobile;

import com.logistics.platform.dto.customer.AddressDTO;
import com.logistics.platform.dto.customer.CustomerDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "customer-service")
public interface CustomerServiceClient {
    @GetMapping("/api/v1/customers/{id}")
    CustomerDTO getCustomerById(@PathVariable("id") String id);

    @PutMapping("/api/v1/customers/{id}")
    CustomerDTO updateCustomer(@PathVariable("id") String id, @RequestBody CustomerDTO customer);

    @GetMapping("/api/v1/customers/{id}/addresses")
    List<AddressDTO> getAddresses(@PathVariable("id") String id);

    @PostMapping("/api/v1/customers/{id}/addresses")
    AddressDTO addAddress(@PathVariable("id") String id, @RequestBody AddressDTO address);
}
