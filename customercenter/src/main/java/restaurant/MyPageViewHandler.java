package restaurant;

import restaurant.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MyPageViewHandler {


    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenPayCompleted_then_CREATE_1 (@Payload PayCompleted payCompleted) {
        try {
            if (reserved.isMe()) {
                // view 객체 생성
                MyPage mypage  = new MyPage();
                // view 객체에 이벤트의 Value 를 set 함
                mypage.setReservationNo(payCompleted.getReservationNo());
                mypage.setRestaurantNo(payCompleted.getRestaurantNo());
                mypage.setDay(payCompleted.getDay());
                mypage.setStatus("DepositPayed");
                // view 레파지 토리에 save
                myPageRepository.save(mypage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservAccepted_then_UPDATE_1(@Payload ReservAccepted reservAccepted) {
        try {
            if (reservAccepted.isMe()) {
                // view 객체 조회
                List<MyPage> mypageList = myPageRepository.findByReservationNo(reservAccepted.getReservationNo());
                for(MyPage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setStatus("Reserved");
                    // view 레파지 토리에 save
                    myPageRepository.save(mypage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPayCanceled_then_UPDATE_2(@Payload PayCanceled payCanceled) {
        try {
            if (payCanceled.isMe()) {
                // view 객체 조회
                List<MyPage> mypageList = myPageRepository.findByReservationNo(payCanceled.getReservationNo());
                for(MyPage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setStatus("Canceled");
                    // view 레파지 토리에 save
                    myPageRepository.save(mypage);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenRecChecked_then_UPDATE_3(@Payload RecChecked recChecked) {
        try {
            if (recChecked.isMe()) {
                // view 객체 조회
                List<MyPage> mypageList = myPageRepository.findByReservationNo(recChecked.getReservationNo());
                for(MyPage mypage : mypageList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    mypage.setStatus("Recommanded");
                    // view 레파지 토리에 save
                    Repository.save();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenCanceled_then_DELETE_1(@Payload Canceled canceled) {
        try {
            if (canceled.isMe()) {
                // view 레파지 토리에 삭제 쿼리
                myPageRepository.deleteById(canceled.getId());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
