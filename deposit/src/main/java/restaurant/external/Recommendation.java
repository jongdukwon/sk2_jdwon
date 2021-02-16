package restaurant.external;

public class Recommendation {

    private Long id;
    private Long reservationNo;
    private String restaurantNo;
    private Long point;

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
