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
    ReservationRepository reservationRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservAccepted_(@Payload ReservAccepted reservAccepted){

        if(reservAccepted.isMe()){
            System.out.println("#### listener  : " + reservAccepted.toJson());

            Optional<Reservation> reservationOptional = reservationRepository.findById(reservAccepted.getReservationNo());
            Reservation reservation = reservationOptional.get();
            reservation.setStatus(reservAccepted.getStatus());
            reservation.setStatus("Reserved");
            reservationRepository.save(reservation);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRecChecked_(@Payload RecChecked recChecked){

        if(recChecked.isMe()){
            System.out.println("##### listener  : " + recChecked.toJson());

            Optional<Reservation> reservationOptional = reservationRepository.findById(reservAccepted.getReservationNo());
            Reservation reservation = reservationOptional.get();
            reservation.setStatus(reservAccepted.getStatus());
            reservation.setStatus("Recommanded");
            reservationRepository.save(reservation);

        }
    }

}
