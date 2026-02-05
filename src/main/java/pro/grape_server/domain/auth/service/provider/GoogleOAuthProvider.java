package pro.grape_server.domain.auth.service.provider;

import org.springframework.stereotype.Component;
import pro.grape_server.model.entity.enums.Provider;

@Component
public class GoogleOAuthProvider implements OAuthProvider {

    @Override
    public Provider getProviderType() {
        return Provider.GOOGLE;
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        throw new UnsupportedOperationException("Google OAuth is not implemented yet");
    }
}
