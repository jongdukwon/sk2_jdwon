package restaurant;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Recommendation_table")
public class Recommendation {

    private Long id;
    private Long reservationNo;
    private String restaurantNo;
    private Long point;

    @PostPersist
    public void onPostPersist(){
        RecChecked recChecked = new RecChecked();
        BeanUtils.copyProperties(this, recChecked);
        recChecked.publishAfterCommit();


        RecCreated recCreated = new RecCreated();
        BeanUtils.copyProperties(this, recCreated);
        recCreated.publishAfterCommit();


    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getReservationNo() {
        return reservationNo;
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
    public Long getPoint() {
        return point;
    }

    public void setPoint(Long point) {
        this.point = point;
    }




}
