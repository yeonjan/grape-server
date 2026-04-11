package pro.grape_server.domain.auth.service.provider;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pro.grape_server.global.exception.BusinessException;
import pro.grape_server.global.exception.ErrorCode;
import pro.grape_server.model.entity.enums.Provider;

import java.util.Map;

@Component
public class KakaoOAuthProvider implements OAuthProvider {

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private final RestClient restClient;

    public KakaoOAuthProvider() {
        this.restClient = RestClient.builder().build();
    }

    @Override
    public Provider getProviderType() {
        return Provider.KAKAO;
    }

    @Override
    @SuppressWarnings("unchecked")
    public OAuthUserInfo getUserInfo(String accessToken) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri(KAKAO_USER_INFO_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new BusinessException(ErrorCode.KAKAO_USER_INFO_FAILED);
            }

            String providerUserId = String.valueOf(response.get("id"));
            return new OAuthUserInfo(providerUserId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.KAKAO_AUTH_FAILED);
        }
    }
}
