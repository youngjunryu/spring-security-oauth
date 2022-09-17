package com.SpringSecurityOAuth.SpringSecurityOAuth.global.exception;

public enum ErrorCode {
	AUTH_ERROR(400, "AU_001", "인증 관련 오류가 발생했습니다."),
	DUPLICATED_EMAIL(400, "AU_002", "이미 존재하는 E-mail입니다."),
	UNAUTHORIZED_REDIRECT_URI(400, "AU_003", "인증되지 않은 REDIRECT_URI입니다."),
	BAD_LOGIN(400, "AU_004", "잘못된 아이디 또는 패스워드입니다.");

	private final String code;
	private final String message;
	private final int status;

	ErrorCode(int status, String code, String message) {
		this.status = status;
		this.message = message;
		this.code = code;
	}

	public String getMessage() {
		return this.message;
	}

	public String getCode() {
		return code;
	}

	public int getStatus() {
		return status;
	}
}