
package restaurant.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@FeignClient(name="deposit", url="${api.deposit.url}")
public interface DepositService {

    @RequestMapping(method= RequestMethod.POST, path="/deposits")
    public void pay(@RequestBody Deposit deposit);

}
