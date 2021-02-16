package restaurant;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Recommendation_table")
public class Recommendation {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long Id;
    private Long reservationNo;
    private String restaurantNo;
    private Long point;

    @PostPersist
    public void onPostPersist(){
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: RecCreated Call");
        RecCreated recCreated = new RecCreated();
        BeanUtils.copyProperties(this, recCreated);
        recCreated.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate(){
        System.out.println("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: RecChecked Call");
        RecChecked recChecked = new RecChecked();
        BeanUtils.copyProperties(this, recChecked);
        recChecked.publishAfterCommit();
    }


    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
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
