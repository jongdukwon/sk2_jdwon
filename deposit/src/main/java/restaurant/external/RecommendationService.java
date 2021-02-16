
package restaurant.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="recommendation", url="${api.recommendation.url}")
public interface RecommendationService {

    @RequestMapping(method= RequestMethod.POST, path="/recommendations")
    public void recCreate(@RequestBody Recommendation recommendation);

}