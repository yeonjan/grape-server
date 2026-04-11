package pro.grape_server.domain.auth.service.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pro.grape_server.global.exception.BusinessException;
import pro.grape_server.global.exception.ErrorCode;
import pro.grape_server.model.entity.enums.Provider;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class AppleOAuthProvider implements OAuthProvider {

    private static final String APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestClient restClient = RestClient.create();

    @Value("${apple.bundle-id}")
    private String bundleId;

    @Override
    public Provider getProviderType() {
        return Provider.APPLE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public OAuthUserInfo getUserInfo(String identityToken) {
        try {
            // 1. JWT header에서 kid 추출
            String[] parts = identityToken.split("\\.");
            if (parts.length != 3) {
                throw new BusinessException(ErrorCode.INVALID_TOKEN);
            }

            byte[] headerBytes = Base64.getUrlDecoder().decode(parts[0]);
            Map<String, String> header = objectMapper.readValue(headerBytes, Map.class);
            String kid = header.get("kid");

            // 2. Apple 공개키 목록 조회
            Map<String, Object> jwksResponse = restClient.get()
                    .uri(APPLE_KEYS_URL)
                    .retrieve()
                    .body(Map.class);

            List<Map<String, String>> keys = (List<Map<String, String>>) jwksResponse.get("keys");

            // 3. kid 일치하는 공개키 찾기
            Map<String, String> matchingKey = keys.stream()
                    .filter(key -> kid.equals(key.get("kid")))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

            // 4. JWK → RSAPublicKey 변환
            RSAPublicKey publicKey = buildPublicKey(matchingKey);

            // 5. JWT 서명 검증 및 클레임 파싱 (exp 자동 검증 포함)
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(identityToken)
                    .getPayload();

            // 6. iss, aud 검증
            if (!APPLE_ISSUER.equals(claims.getIssuer())) {
                throw new BusinessException(ErrorCode.INVALID_TOKEN);
            }
            if (!claims.getAudience().contains(bundleId)) {
                throw new BusinessException(ErrorCode.INVALID_TOKEN);
            }

            String sub = claims.getSubject();
            if (sub == null) {
                throw new BusinessException(ErrorCode.INVALID_TOKEN);
            }

            return new OAuthUserInfo(sub);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.APPLE_AUTH_FAILED);
        }
    }

    private RSAPublicKey buildPublicKey(Map<String, String> jwk) throws Exception {
        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(jwk.get("n")));
        BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(jwk.get("e")));

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }
}
