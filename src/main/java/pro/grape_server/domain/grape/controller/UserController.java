package pro.grape_server.domain.grape.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.grape_server.domain.grape.controller.dto.response.GetUserEmailResponse;
import pro.grape_server.domain.grape.controller.dto.response.GetUserNicknameResponse;
import pro.grape_server.global.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {


    @GetMapping("/me/nickname")
    public ResponseEntity<GetUserNicknameResponse> getUserNickname(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String nickname = userDetails.getUser().getNickname();
        return ResponseEntity.ok(GetUserNicknameResponse.from(nickname));
    }
}
