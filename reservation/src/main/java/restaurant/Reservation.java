package restaurant;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.actuate.beans.BeansEndpoint.ApplicationBeans;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

@Entity
@Table(name="Reservation_table")
public class Reservation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long Id;
    private String restaurantNo;
    private String day;
    private String status;

    @PostPersist
    public void onPostPersist(){
        Reserved reserved = new Reserved();
        BeanUtils.copyProperties(this, reserved);
        reserved.publishAfterCommit();
        
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        restaurant.external.Deposit deposit = new restaurant.external.Deposit();

        // mappings goes here
        deposit.setReservationNo(this.getId());
        deposit.setRestaurantNo(this.getRestaurantNo());
        deposit.setDay(this.getDay());
        deposit.setStatus("PayCompleted");
        ReservationApplication.applicationContext.getBean(restaurant.external.DepositService.class)
            .pay(deposit);

    }
/*
    @PrePersist
    public void onPrePersist(){ 
        try {
            Thread.currentThread().sleep((long) (1000 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
*/
    @PreRemove
    public void onPreRemove(){
        
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: status="+this.getStatus());
        //if(!"Reserved".equals(this.getStatus())){
            Canceled canceled = new Canceled();
            BeanUtils.copyProperties(this, canceled);
            canceled.publishAfterCommit();
       // }
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }
    public String getRestaurantNo() {
        return restaurantNo;
    }

    public void setRestaurantNo(String restaurantNo) {
        this.restaurantNo = restaurantNo;
    }
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
