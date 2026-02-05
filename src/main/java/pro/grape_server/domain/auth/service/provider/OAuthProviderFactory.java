package pro.grape_server.domain.auth.service.provider;

import org.springframework.stereotype.Component;
import pro.grape_server.global.exception.AuthException;
import pro.grape_server.model.entity.enums.Provider;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OAuthProviderFactory {

    private final Map<Provider, OAuthProvider> providers;

    public OAuthProviderFactory(List<OAuthProvider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(OAuthProvider::getProviderType, Function.identity()));
    }

    public OAuthProvider getProvider(Provider provider) {
        OAuthProvider oAuthProvider = providers.get(provider);
        if (oAuthProvider == null) {
            throw new AuthException("Unsupported OAuth provider: " + provider);
        }
        return oAuthProvider;
    }

    public OAuthProvider getProvider(String providerName) {
        try {
            Provider provider = Provider.valueOf(providerName.toUpperCase());
            return getProvider(provider);
        } catch (IllegalArgumentException e) {
            throw new AuthException("Unknown OAuth provider: " + providerName);
        }
    }
}
