package com.fastcampus.pass.adapter.message;

import com.fastcampus.pass.config.KakaoTalkMessageConfig;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KakaoTalkMessageAdapter {
    private final WebClient webClient;

    public KakaoTalkMessageAdapter(KakaoTalkMessageConfig config) {
        webClient = WebClient.builder()
                .baseUrl(config.getHost())                  // 호스트 셋팅
                .defaultHeaders(h -> {
                    h.setBearerAuth(config.getToken());     // 인증 토큰 셋팅
                    h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);    // 컨텐츠 타입 조정
                }).build();

    }

    public boolean sendKakaoTalkMessage(final String uuid, final String text) {
        KakaoTalkMessageResponse response = webClient.post().uri("/v1/api/talk/friends/message/default/send")
                .body(BodyInserters.fromValue(new KakaoTalkMessageRequest(uuid, text)))
                .retrieve()
                .bodyToMono(KakaoTalkMessageResponse.class)
                .block();

        if (response == null || response.getSuccessfulReceiverUuids() == null) {
            return false;

        }
        return !response.getSuccessfulReceiverUuids().isEmpty();

    }

}
