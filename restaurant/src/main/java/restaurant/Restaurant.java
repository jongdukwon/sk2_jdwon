package restaurant;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Restaurant_table")
public class Restaurant {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long Id;
    private Long reservationNo;
    private String restaurantNo;
    private String day;
    private String status;

    @PostPersist
    public void onPostPersist(){
        ReservAccepted reservAccepted = new ReservAccepted();
        BeanUtils.copyProperties(this, reservAccepted);
        reservAccepted.publishAfterCommit();


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
    public Long getReservationNo() {
        return reservationNo;
    }

    public void setReservationNo(Long reservationNo) {
        this.reservationNo = reservationNo;
    }




}
