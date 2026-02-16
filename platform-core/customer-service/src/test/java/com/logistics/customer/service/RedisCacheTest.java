package com.logistics.customer.service;

import com.logistics.customer.model.Customer;
import com.logistics.customer.repository.CustomerRepository;
import com.logistics.customer.repository.CustomerAddressRepository;
import com.logistics.platform.utils.config.RedisConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@org.springframework.test.context.TestPropertySource(properties = "spring.main.allow-bean-definition-overriding=true")
public class RedisCacheTest {

    @Autowired
    private CustomerService customerService;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private CustomerAddressRepository addressRepository;

    @Autowired
    private CacheManager cacheManager;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @org.springframework.context.annotation.Bean
        public CacheManager cacheManager() {
            return new org.springframework.cache.concurrent.ConcurrentMapCacheManager("customers", "customers_id",
                    "customer_addresses");
        }
    }

    @Test
    public void testCustomerCache() {
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setName("Test User");
        customer.setEmail("test@example.com");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // First call - should hit DB
        Customer result1 = customerService.getCustomerById(customerId);
        verify(customerRepository, times(1)).findById(customerId);
        assertNotNull(result1);

        // Second call - should hit Cache (no DB call)
        Customer result2 = customerService.getCustomerById(customerId);
        verify(customerRepository, times(1)).findById(customerId); // Still times(1)
        assertEquals(result1.getName(), result2.getName());

        // Verify cache keys
        assertNotNull(cacheManager.getCache("customers_id").get(customerId));
    }
}
