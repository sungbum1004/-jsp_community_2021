package com.jhs.exam.exam2.app;

import com.jhs.exam.exam2.container.Container;
import com.jhs.exam.exam2.container.ContainerComponent;
import com.jhs.exam.exam2.util.Ut;
import com.jhs.mysqliutil.MysqlUtil;

import lombok.Getter;

public class App implements ContainerComponent {
	@Getter
	private boolean ready = false;
	private String smtpGmailPw;

	@Override
	public void init() {
		// 필수적으로 로딩되어야 하는 내용 불러오기
		smtpGmailPw = Ut.getFileContents("c:/work/jsb/SmtpGmailPw.txt");

		if (smtpGmailPw != null && smtpGmailPw.trim().length() > 0) {
			ready = true;
		}
	}

	public static boolean isDevMode() {
		// 이 부분을 false로 바꾸면 production 모드 이다.
		return true;
	}

	// 서버 배포 여부를 리턴해주는 함수
	private static boolean isProductMode() {
		return isDevMode() == false;
	}

	// 정적 요소 세팅
	public static void start() {
		// DB 세팅
		MysqlUtil.setDBInfo("localhost", "sbsst", "sbs123414", "jsp_board");
		MysqlUtil.setDevMode(isDevMode());

		// 공용 객체 세팅
		Container.init();

	}

	// 발송 할 메일 주소를 리턴하는 함수
	public String getSmtpGmailId() {
		return "tlsqkfka74@gmail.com";
	}

	// 발송 할 메일 비밀번호(파일) 리턴하는 함수
	public String getSmtpGmailPw() {
		return smtpGmailPw;
	}

	// 커뮤니티 이름을 리턴하는 함수
	public String getSiteName() {
		return "레몬 커뮤니티";
	}

	// 베이스 Uri를 리턴하는 함수
	public String getBaseUri() {
		String appUri = getSiteProtocol() + "://" + getSiteDomain();

		if (getSitePort() != 80 && getSitePort() != 443) {
			appUri += ":" + getSitePort();
		}

		if (getContextName().length() > 0) {
			appUri += "/" + getContextName();
		}

		return appUri;
	}

	// 커뮤니티 이름을 리턴하는 함수
	private String getContextName() {
		if (isProductMode()) {
			return "";
		}

		return "2021_jsp_board";
	}

	// 사이트 포트번호를 리턴하는 함수
	private int getSitePort() {
		return 8081;
	}

	// 사이트 ㄷ메인을 리턴하는 함수
	private String getSiteDomain() {
		return "localhost";
	}

	// 사이트의 프로토콜을 리턴하는 함수
	private String getSiteProtocol() {
		return "http";
	}

	// 베이스 Uri에 로그인페이지를 더해 리턴 하는 함수
	public String getLoginUri() {
		return getBaseUri() + "/usr/member/login";
	}

	// 메일 작성시 작성자 이름을 리턴하는 함수
	public String getNotifyEmailFromName() {
		return "레몬 커뮤니티 알림봇";
	}
}
