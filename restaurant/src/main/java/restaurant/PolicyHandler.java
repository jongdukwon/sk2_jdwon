package restaurant;

import restaurant.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PolicyHandler{
    @Autowired
    RestaurantRepository restaurantRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPayCompleted_(@Payload PayCompleted payCompleted){
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: payCompleted");
        if(payCompleted.isMe()){
            System.out.println("#### listener : " + payCompleted.toJson());

            Restaurant restaurant = new Restaurant();
            restaurant.setReservationNo(payCompleted.getReservationNo());
            restaurant.setRestaurantNo(payCompleted.getRestaurantNo());
            restaurant.setDay(payCompleted.getDay());
            restaurant.setStatus("Reserved");

            restaurantRepository.save(restaurant);
            
        }
    }
    
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPayCanceled_(@Payload PayCanceled payCanceled){
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: PayCanceled");
        if(payCanceled.isMe()){
            System.out.println("##### listener  : " + payCanceled.toJson());
            
            List<Restaurant> restaurantlist = restaurantRepository.findByReservationNo(payCanceled.getReservationNo());

            for(Restaurant restaurant : restaurantlist){
               // view 객체에 이벤트의 eventDirectValue 를 set 함
               restaurant.setStatus("Canceled");
               // view 레파지 토리에 save
               restaurantRepository.save(restaurant);
            }
            
        }
    }

}
