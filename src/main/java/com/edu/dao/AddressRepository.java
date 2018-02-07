package com.edu.dao;

import com.edu.domain.Address;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<Address, Long> {
    Address findFirstByOrderByIdAsc();
}
