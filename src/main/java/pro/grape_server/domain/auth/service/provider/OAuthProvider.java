package pro.grape_server.domain.auth.service.provider;

import pro.grape_server.model.entity.enums.Provider;

public interface OAuthProvider {

    Provider getProviderType();

    OAuthUserInfo getUserInfo(String accessToken);
}
