package pro.grape_server.domain.auth.service.provider;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import pro.grape_server.global.exception.AuthException;
import pro.grape_server.model.entity.enums.Provider;

import java.net.URI;
import java.util.Map;

@Component
public class KakaoOAuthProvider implements OAuthProvider {

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String PROPERTY_KEYS = "[\"kakao_account.profile\"]";

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
            URI uri = UriComponentsBuilder.fromUriString(KAKAO_USER_INFO_URL)
                    .queryParam("property_keys", PROPERTY_KEYS)
                    .build()
                    .toUri();

            Map<String, Object> response = restClient.get()
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new AuthException("Failed to get user info from Kakao");
            }

            String providerUserId = String.valueOf(response.get("id"));

            Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
            Map<String, Object> profile = kakaoAccount != null
                    ? (Map<String, Object>) kakaoAccount.get("profile")
                    : null;

            String name = profile != null ? (String) profile.get("nickname") : "Unknown";
            String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

            return new OAuthUserInfo(providerUserId, name, email);
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException("Failed to authenticate with Kakao: " + e.getMessage());
        }
    }
}
