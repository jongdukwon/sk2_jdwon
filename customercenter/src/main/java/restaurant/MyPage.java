package restaurant;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="MyPage_table")
public class MyPage {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long Id;
        private String restaurantNo;
        private String day;
        private String status;
        private Long reservationNo;


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
