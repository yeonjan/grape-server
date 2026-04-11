package pro.grape_server.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),

    // Grape
    GRAPE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 포도입니다."),
    GRAPE_IN_PROGRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "진행 중인 포도가 없습니다."),
    GRAPE_ALREADY_IN_PROGRESS(HttpStatus.CONFLICT, "이미 진행 중인 포도가 있습니다."),
    GRAPE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "본인의 포도만 조회할 수 있습니다."),
    GRAPE_UPDATE_DENIED(HttpStatus.FORBIDDEN, "본인의 포도만 수정할 수 있습니다."),
    INVALID_TARGET_COUNT(HttpStatus.BAD_REQUEST, "허용되지 않은 targetCount입니다."),

    // Record
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 기록입니다."),
    RECORD_CREATE_DENIED(HttpStatus.FORBIDDEN, "본인의 포도에만 기록을 추가할 수 있습니다."),
    RECORD_UPDATE_DENIED(HttpStatus.FORBIDDEN, "본인의 기록만 수정할 수 있습니다."),
    RECORD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "본인의 기록만 조회할 수 있습니다."),
    RECORD_DELETE_DENIED(HttpStatus.FORBIDDEN, "본인의 기록만 삭제할 수 있습니다."),
    RECORD_DUPLICATE(HttpStatus.CONFLICT, "해당 날짜에 이미 기록이 존재합니다."),

    // Auth
    DEVICE_ALREADY_REGISTERED(HttpStatus.CONFLICT, "Already registered device"),
    GUEST_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Guest user not found"),
    TOKEN_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "User not found"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid refresh token"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh token has expired"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token has expired"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid token"),
    UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "Unsupported OAuth provider"),
    UNKNOWN_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "Unknown OAuth provider"),
    KAKAO_USER_INFO_FAILED(HttpStatus.BAD_GATEWAY, "Failed to get user info from Kakao"),
    KAKAO_AUTH_FAILED(HttpStatus.BAD_GATEWAY, "Failed to authenticate with Kakao"),

    // Not Implemented
    GOOGLE_OAUTH_NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "Google OAuth is not implemented yet"),
    NAVER_OAUTH_NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED, "Naver OAuth is not implemented yet");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
