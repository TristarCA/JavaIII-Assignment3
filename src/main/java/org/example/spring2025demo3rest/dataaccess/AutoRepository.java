package org.example.spring2025demo3rest.dataaccess;

import org.example.spring2025demo3rest.pojos.Auto;
import org.example.spring2025demo3rest.pojos.Home;
import org.springframework.data.repository.CrudRepository;

public interface AutoRepository extends CrudRepository<Auto, Long> {
    /**
     * Get all vehicles for a user
     * @param userId
     * @return
     */
    Iterable<Auto> getAllByUserId(Long userId);
}
