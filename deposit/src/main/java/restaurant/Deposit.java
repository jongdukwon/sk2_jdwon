package restaurant;

import javax.activation.MimeType;
import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name="Deposit_table")
public class Deposit {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long Id;
    private Long reservationNo;
    private String restaurantNo;
    private String day;
    private String status;


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

    @PostPersist
    public void onPostPersist(){
        
        if(this.getStatus().equals("PayCompleted")){
            System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: Deposit : PayCompleted");
            //DepositPayed
            PayCompleted payCompleted = new PayCompleted();
            BeanUtils.copyProperties(this, payCompleted);
            payCompleted.publishAfterCommit();

            //Following code causes dependency to external APIs
            // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

            restaurant.external.Recommendation recommendation = new restaurant.external.Recommendation();
            // mappings goes here
            recommendation.setReservationNo(payCompleted.getId());
            recommendation.setRestaurantNo(payCompleted.getRestaurantNo());

            DepositApplication.applicationContext.getBean(restaurant.external.RecommendationService.class)
                .recCreate(recommendation);


        }else if(this.getStatus().equals("PayCanceled")){
            System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: Deposit : PayCanceled");
            PayCanceled payCanceled = new PayCanceled();
            BeanUtils.copyProperties(this, payCanceled);
            payCanceled.publishAfterCommit();

        }

    }


    public Long getId() {
        return Id;
    }

    public Long getReservationNo() {
        return reservationNo;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public void setReservationNo(Long reservationNo) {
        this.reservationNo = reservationNo;
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
