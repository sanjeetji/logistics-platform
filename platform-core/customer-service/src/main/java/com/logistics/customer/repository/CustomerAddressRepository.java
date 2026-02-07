package com.logistics.customer.repository;

import com.logistics.customer.model.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Long> {
    
    List<CustomerAddress> findByCustomerIdAndActive(Long customerId, Boolean active);
    
    Optional<CustomerAddress> findByCustomerIdAndIsDefaultAndActive(Long customerId, Boolean isDefault, Boolean active);
    
    List<CustomerAddress> findByCustomerId(Long customerId);
}
