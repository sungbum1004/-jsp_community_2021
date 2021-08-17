package com.jhs.exam.exam2.service;

import com.jhs.exam.exam2.app.App;
import com.jhs.exam.exam2.container.Container;
import com.jhs.exam.exam2.container.ContainerComponent;
import com.jhs.exam.exam2.dto.Member;
import com.jhs.exam.exam2.dto.ResultData;
import com.jhs.exam.exam2.repository.MemberRepository;
import com.jhs.exam.exam2.util.Ut;

public class MemberService implements ContainerComponent {
	private MemberRepository memberRepository;
	private EmailService emailService;

	public void init() {
		memberRepository = Container.memberRepository;
		emailService = Container.emailService;
	}

	// 재구현 완료 21-08-15
	public ResultData login(String loginId, String loginPw) {
		// 로그인아이디로 member가 존재하는지 확인하는 함수
		Member member = getMemberByLoginId(loginId);

		// 해당 member가 존재 하지 않을시 F-1 저장 후 리턴
		if (member == null) {
			return ResultData.from("F-1", "존재하지 않는 회원의 로그인아이디 입니다.");
		}

		// 해당 member는 존재 하나 member의 비밀번호와 입력한 비밀번호가 틀릴시 F-2저장후 리턴
		if (member.getLoginPw().equals(loginPw) == false) {
			return ResultData.from("F-2", "비밀번호가 일치하지 않습니다.");
		}

		// 모두 이상 없을시 S-1, member값 저장 후 리턴
		return ResultData.from("S-1", "환영합니다.", "member", member);
	}

	public ResultData join(String loginId, String loginPw, String loginPwConfirm, String name, String nickname,
			String email, String cellphoneNo) {
		// 가입 할 로그인아이디를 받아 member 추적 
		Member oldMember = getMemberByLoginId(loginId);

		// 찾은 member가 존재할시 아이디가 중복이므로 F-1, 오류 메세지 저장 후 리턴
		if (oldMember != null) {
			return ResultData.from("F-1", Ut.f("%s(은)는 이미 사용중인 로그인아이디입니다.", loginId));
		}

		// 가입자의 이름과 이메일을 받아 member 추적
		oldMember = getMemberByNameAndEmail(name, email);

		// 가입자의 이름과 이메일로 member가 존재하면 동일 인물로 판단하여 F-2,오류 메세지 저장 후 리턴
		if (oldMember != null) {
			return ResultData.from("F-2", Ut.f("%s님은 이메일 주소 `%s`(으)로 이미 가입하셨습니다.", name, email));
		}

		// S-1, 완료 메세지 저장 후 리턴
		memberRepository.join(loginId, loginPw, loginPwConfirm, name, nickname, email, cellphoneNo);
		return ResultData.from("S-1", "환영합니다.");
	}

	public Member getMemberByNameAndEmail(String name, String email) {
		return memberRepository.getMemberByNameAndEmail(name, email);
	}

	// 재구현 완료 21-08-15
	public Member getMemberByLoginId(String loginId) {
		return memberRepository.getMemberByLoginId(loginId);
	}
	
	public Member getMemberByNickname(String nickname) {
		return memberRepository.getMemberByNickname(nickname);
	}

	public boolean isAdmin(Member member) {
		return member.getAuthLevel() >= 7;
	}

	public ResultData sendTempLoginPwToEmail(Member actor) {
		App app = Container.app;
		// 메일 제목과 내용 만들기
		String siteName = app.getSiteName(); // 사이트 이름 리턴하는 함수
		String siteLoginUrl = app.getLoginUri();
		String title = "[" + siteName + "] 임시 패스워드 발송"; // 이메일 제목
		String tempPassword = Ut.getTempPassword(6); // 임시 비밀번호 저장
		String body = "<h1>임시 패스워드 : " + tempPassword + "<h1>"; // 내용
		// 내용 + 해당 사이트 로그인페이지로 이동하는 a태그 생성
		body += "<a href=\"" + siteLoginUrl + "\" target=\"_blank\">로그인 하러가기</a>";

		if (actor.getEmail().length() == 0) {
			return ResultData.from("F-0", "해당 회원의 이메일이 없습니다.");
		}

		// 메일 발송(보내는 매개, 매개 비밀번호, 보내는사람, 사이트이름, 해당 멤버의 이메일, 제목, 내용)
		int notifyRs = emailService.notify(actor.getEmail(), title, body);

		// sendRs이 1이면 발송 성공 1이 아니면 발송실패
		if (notifyRs != 1) {
			return ResultData.from("F-1", "메일발송에 실패하였습니다.");
		}

		// 해당 멤버와 임시 비밀번호를 이용하여 해당 멤버의 비밀번호를 변경하는 함수
		setTempLoginPw(actor, tempPassword);

		// 메일 발송이 완료되면 S-1, 해당 이메일로 발송했다는 메세지 저장후 리턴
		String resultMsg = String.format("고객님의 새 임시 패스워드가 %s (으)로 발송되었습니다.", actor.getEmail());
		return ResultData.from("S-1", resultMsg, "email", actor.getEmail());
	}

	private void setTempLoginPw(Member actor, String tempLoginPw) {
		// DB에 접근하여 해당 멤버 비밀번호 변경하는 함수
		memberRepository.modifyPassword(actor.getId(), tempLoginPw);
	}

}
