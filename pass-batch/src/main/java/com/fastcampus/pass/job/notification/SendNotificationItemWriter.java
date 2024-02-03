package com.fastcampus.pass.job.notification;

import com.fastcampus.pass.adapter.message.KakaoTalkMessageAdapter;
import com.fastcampus.pass.repository.notification.NotificationEntity;
import com.fastcampus.pass.repository.notification.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SendNotificationItemWriter implements ItemWriter<NotificationEntity> { // ItemWriter 상속 받음
    private final NotificationRepository notificationRepository;
    private final KakaoTalkMessageAdapter kakaoTalkMessageAdapter;

    // 생성자
    public SendNotificationItemWriter(NotificationRepository notificationRepository, KakaoTalkMessageAdapter kakaoTalkMessageAdapter) {
        this.notificationRepository = notificationRepository;
        this.kakaoTalkMessageAdapter = kakaoTalkMessageAdapter;
    }

    @Override
    public void write(Chunk<? extends NotificationEntity> notificationEntities) {
        int count = 0; //메세지 건수를 세기 위함

        for (NotificationEntity notificationEntity : notificationEntities) {
            // notificationEntity의 정보와  kakaoTalkMessageAdapter를 이용해 메세지를 보내고, 그 결과를 반환받음
            boolean successful = kakaoTalkMessageAdapter.sendKakaoTalkMessage(notificationEntity.getUuid(), notificationEntity.getText());
            // 성공한다면, notificationEntitydml sent정보와 sentAt정보를 바꾸고 저장.
            if (successful) {
                notificationEntity.setSent(true);
                notificationEntity.setSentAt(LocalDateTime.now());
                notificationRepository.save(notificationEntity);
                count++;
            }

        }
        log.info("SendNotificationItemWriter - write: 수업 전 알람 {}/{}건 전송 성공", count, notificationEntities.size());
    }
}
