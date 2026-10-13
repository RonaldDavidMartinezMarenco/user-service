package com.foodcourt.user.domain.spi;

import java.util.Optional;

import com.foodcourt.user.domain.model.RestaurantOwnership;

/**
 * Reads restaurants from plaza-service. Empty when the restaurant does not exist.
 * Any other failure is {@link com.foodcourt.user.domain.exception.RestaurantDirectoryUnavailable}.
 */
public interface RestaurantDirectory {

    Optional<RestaurantOwnership> findById(long id);
}
