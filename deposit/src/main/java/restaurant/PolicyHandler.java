package restaurant;

import restaurant.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @Autowired
    DepositRepository depositRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReserved_(@Payload Canceled cancened){

        if(cancened.isMe()){
            System.out.println("##### listener  : " + cancened.toJson());

            Deposit deposit = new Deposit();
            deposit.setReservationNo(cancened.getId());
            deposit.setRestaurantNo(cancened.getRestaurantNo());
            deposit.setDay(cancened.getDay());
            deposit.setStatus("PayCanceled");
            depositRepository.save(deposit);
            
        }
    }

}
