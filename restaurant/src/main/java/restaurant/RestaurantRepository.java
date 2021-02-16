package restaurant;

import org.springframework.data.repository.PagingAndSortingRepository;
 import java.util.List;

public interface RestaurantRepository extends PagingAndSortingRepository<Restaurant, Long>{

 List<Restaurant> findByReservationNo(Long reservationNo);
 
}
