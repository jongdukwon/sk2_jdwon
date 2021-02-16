package restaurant;

public class RecChecked extends AbstractEvent {

    private Long Id;
    private Long reservationNo;
    private String status;

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
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}