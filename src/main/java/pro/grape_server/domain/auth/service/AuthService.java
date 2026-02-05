package pro.grape_server.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.grape_server.domain.auth.controller.dto.response.LoginResponse;
import pro.grape_server.domain.auth.repository.RefreshTokenRepository;
import pro.grape_server.domain.auth.repository.UserRepository;
import pro.grape_server.domain.auth.service.provider.OAuthProvider;
import pro.grape_server.domain.auth.service.provider.OAuthProviderFactory;
import pro.grape_server.domain.auth.service.provider.OAuthUserInfo;
import pro.grape_server.global.exception.AuthException;
import pro.grape_server.model.entity.RefreshToken;
import pro.grape_server.model.entity.User;
import pro.grape_server.model.entity.enums.Provider;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuthProviderFactory oAuthProviderFactory;
    private final JwtService jwtService;

    public LoginResponse login(String providerName, String accessToken) {
        OAuthProvider oAuthProvider = oAuthProviderFactory.getProvider(providerName);
        Provider provider = oAuthProvider.getProviderType();

        OAuthUserInfo userInfo = oAuthProvider.getUserInfo(accessToken);

        User user = userRepository.findByProviderAndProviderUserId(provider, userInfo.providerUserId())
                .orElseGet(() -> createUser(provider, userInfo));

        return issueTokens(user);
    }

    public LoginResponse refresh(String refreshToken) {
        jwtService.validateToken(refreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthException("Invalid refresh token"));

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new AuthException("Refresh token has expired");
        }

        User user = storedToken.getUser();

        refreshTokenRepository.delete(storedToken);

        return issueTokens(user);
    }

    private User createUser(Provider provider, OAuthUserInfo userInfo) {
        User newUser = User.create(
                provider,
                userInfo.providerUserId(),
                userInfo.name(),
                userInfo.email()
        );
        return userRepository.save(newUser);
    }

    private LoginResponse issueTokens(User user) {
        String accessJwt = jwtService.createAccessToken(user.getId());
        String refreshJwt = jwtService.createRefreshToken(user.getId());

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();

        RefreshToken newRefreshToken = RefreshToken.create(
                user,
                refreshJwt,
                jwtService.getRefreshTokenExpirationTime()
        );
        refreshTokenRepository.save(newRefreshToken);

        return LoginResponse.of(accessJwt, refreshJwt);
    }
}
