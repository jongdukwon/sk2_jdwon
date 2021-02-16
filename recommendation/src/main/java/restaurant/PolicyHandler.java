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
    RecommendationRepository recommendationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReserveCanceled_(@Payload ReserveCanceled reserveCanceled){
        
        if(reserveCanceled.isMe()){
            System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: Handler : reserveCanceled");
            System.out.println("##### listener  : " + reserveCanceled.toJson());
            
            Optional<Recommendation> recommendationOptional = recommendationRepository.findById(reserveCanceled.getReservationNo());
            Recommendation recommendation = recommendationOptional.get();

            recommendationRepository.delete(recommendation);
        }
    }

}
