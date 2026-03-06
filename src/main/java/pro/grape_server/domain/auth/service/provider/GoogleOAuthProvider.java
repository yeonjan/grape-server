package pro.grape_server.domain.auth.service.provider;

import org.springframework.stereotype.Component;
import pro.grape_server.global.exception.BusinessException;
import pro.grape_server.global.exception.ErrorCode;
import pro.grape_server.model.entity.enums.Provider;

@Component
public class GoogleOAuthProvider implements OAuthProvider {

    @Override
    public Provider getProviderType() {
        return Provider.GOOGLE;
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        throw new BusinessException(ErrorCode.GOOGLE_OAUTH_NOT_IMPLEMENTED);
    }
}
