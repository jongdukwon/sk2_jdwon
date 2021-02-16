package restaurant;

import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;

public interface RecommendationRepository extends PagingAndSortingRepository<Recommendation, Long>{

    List<Recommendation> findByReservationNo(Long reservationNo);
}