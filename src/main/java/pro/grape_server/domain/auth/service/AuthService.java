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
import pro.grape_server.domain.grape.repository.GrapeRepository;
import pro.grape_server.global.exception.BusinessException;
import pro.grape_server.global.exception.ErrorCode;
import pro.grape_server.model.entity.Grape;
import pro.grape_server.model.entity.RefreshToken;
import pro.grape_server.model.entity.User;
import pro.grape_server.model.entity.enums.Provider;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuthProviderFactory oAuthProviderFactory;
    private final JwtService jwtService;
    private final GrapeRepository grapeRepository;

    public LoginResponse guestSignUp(String deviceId) {
        if (userRepository.findByProviderAndProviderUserId(Provider.GUEST, deviceId).isPresent()) {
            throw new BusinessException(ErrorCode.DEVICE_ALREADY_REGISTERED);
        }
        User user = userRepository.save(User.createGuest(deviceId));
        return issueTokens(user);
    }

    public LoginResponse guestLogin(String deviceId) {
        User user = userRepository.findByProviderAndProviderUserId(Provider.GUEST, deviceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GUEST_USER_NOT_FOUND));
        return issueTokens(user);
    }

    public LoginResponse login(String providerName, String accessToken, Long guestUserId) {
        OAuthProvider oAuthProvider = oAuthProviderFactory.getProvider(providerName);
        Provider provider = oAuthProvider.getProviderType();
        OAuthUserInfo userInfo = oAuthProvider.getUserInfo(accessToken);

        Optional<User> existingSocialUser = userRepository.findByProviderAndProviderUserId(provider, userInfo.providerUserId());

        User user;
        if (guestUserId != null) {
            User guestUser = userRepository.findById(guestUserId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.GUEST_USER_NOT_FOUND));

            if (existingSocialUser.isPresent()) {
                User socialUser = existingSocialUser.get();
                migrateGrapes(guestUser, socialUser);
                refreshTokenRepository.deleteByUser(guestUser);
                refreshTokenRepository.flush();
                userRepository.delete(guestUser);
                user = socialUser;
            } else {
                guestUser.convertToSocialAccount(provider, userInfo.providerUserId());
                user = guestUser;
            }
        } else {
            user = existingSocialUser.orElseGet(() -> createUser(provider, userInfo));
        }

        return issueTokens(user);
    }

    public LoginResponse refresh(String refreshToken) {
        jwtService.validateToken(refreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (storedToken.isExpired()) {
            refreshTokenRepository.delete(storedToken);
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        User user = storedToken.getUser();

        refreshTokenRepository.delete(storedToken);

        return issueTokens(user);
    }

    private void migrateGrapes(User guestUser, User socialUser) {
        List<Grape> guestGrapes = grapeRepository.findAllByUser(guestUser);
        for (Grape grape : guestGrapes) {
            grape.assignToUser(socialUser);
        }
    }

    private User createUser(Provider provider, OAuthUserInfo userInfo) {
        return userRepository.save(User.create(provider, userInfo.providerUserId()));
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
