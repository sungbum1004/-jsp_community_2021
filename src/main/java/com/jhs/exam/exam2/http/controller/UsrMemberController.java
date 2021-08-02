package com.jhs.exam.exam2.http.controller;

import com.jhs.exam.exam2.container.Container;
import com.jhs.exam.exam2.dto.Member;
import com.jhs.exam.exam2.dto.ResultData;
import com.jhs.exam.exam2.http.Rq;
import com.jhs.exam.exam2.service.MemberService;
import com.jhs.exam.exam2.util.Ut;

public class UsrMemberController extends Controller {
	private MemberService memberService;

	public void init() {
		memberService = Container.memberService;
	}

	@Override
	public void performAction(Rq rq) {
		switch (rq.getActionMethodName()) {
		case "login":
			actionShowLogin(rq);
			break;
		case "doLogin":
			actionDoLogin(rq);
			break;
		case "doLogout":
			actionDoLogout(rq);
			break;
		case "join":
			actionShowJoin(rq);
			break;
		case "doJoin":
			actionDoJoin(rq);
			break;
		case "loginIdCheck":
			actionDoLoginIdCheck(rq);
			break;
		case "loginNicknameCheck":
			actionDoLoginNicknameCheck(rq);
			break;
		case "findLoginId":
			actionShowFindLoginId(rq);
			break;
		case "doFindLoginId":
			actionDoFindLoginId(rq);
			break;
		case "findLoginPw":
			actionShowFindLoginPw(rq);
			break;
		case "doFindLoginPw":
			actionDoFindLoginPw(rq);
			break;
		default:
			rq.println("존재하지 않는 페이지 입니다.");
			break;
		}
	}
	
	private void actionDoLoginNicknameCheck(Rq rq) {
		String nickname = rq.getParam("nickname", "");
		rq.write(memberService.getMemberByNickname(nickname) + "");
	}

	private void actionShowFindLoginPw(Rq rq) {
		rq.jsp("usr/member/findLoginPw");

	}

	private void actionDoFindLoginPw(Rq rq) {
		String loginId = rq.getParam("loginId", "");
		String email = rq.getParam("email", "");

		if (loginId.length() == 0) {
			rq.historyBack("loginId을(를) 입력해주세요.");
			return;
		}

		if (email.length() == 0) {
			rq.historyBack("email을(를) 입력해주세요.");
			return;
		}

		Member oldMember = memberService.getMemberByLoginId(loginId);

		if (oldMember == null) {
			rq.historyBack("일치하는 회원이 존재하지 않습니다.");
			return;
		}
		
		if (oldMember.getEmail().equals(email) == false) {
			rq.historyBack("일치하는 회원이 존재하지 않습니다.");
			return;
		}

		ResultData sendTempLoginPwToEmailRd = memberService.sendTempLoginPwToEmail(oldMember);

		if ( sendTempLoginPwToEmailRd.isFail() ) {
			rq.historyBack(sendTempLoginPwToEmailRd.getMsg());
			return;
		}
		
		rq.replace(sendTempLoginPwToEmailRd.getMsg(), "../home/main");
	}

	private void actionShowFindLoginId(Rq rq) {
		rq.jsp("usr/member/findLoginId");
	}

	private void actionDoFindLoginId(Rq rq) {
		String name = rq.getParam("name", "");
		String email = rq.getParam("email", "");

		if (name.length() == 0) {
			rq.historyBack("name을 입력해주세요.");
			return;
		}

		if (email.length() == 0) {
			rq.historyBack("email 입력해주세요.");
			return;
		}

		Member oldMember = memberService.getMemberByNameAndEmail(name, email);

		if (oldMember == null) {
			rq.historyBack("일치하는 회원이 존재하지 않습니다.");
			return;
		}

		String replaceUri = "../member/login?loginId=" + oldMember.getLoginId();
		rq.replace(Ut.f("해당 회원의 로그인아이디는 `%s` 입니다.", oldMember.getLoginId()), replaceUri);
		return;
	}

	private void actionDoLoginIdCheck(Rq rq) {
		String loginId = rq.getParam("loginId", "");
		rq.write(memberService.getMemberByLoginId(loginId) + "");
	}

	private void actionDoJoin(Rq rq) {
		String loginId = rq.getParam("loginId", "");
		String loginPw = rq.getParam("loginPw", "");
		String loginPwConfirm = rq.getParam("loginPwConfirm", "");
		String name = rq.getParam("name", "");
		String nickname = rq.getParam("nickname", "");
		String email = rq.getParam("cellphoneNo", "");
		String cellphoneNo = rq.getParam("email", "");

		if (loginId.length() == 0) {
			rq.historyBack("loginId를 입력해주세요.");
			return;
		}

		if (loginPw.length() == 0) {
			rq.historyBack("loginPw를 입력해주세요.");
			return;
		}

		if (loginPwConfirm.length() == 0) {
			rq.historyBack("loginPwConfirm를 입력해주세요.");
			return;
		}

		if (name.length() == 0) {
			rq.historyBack("name을 입력해주세요.");
			return;
		}

		if (nickname.length() == 0) {
			rq.historyBack("nickname을 입력해주세요.");
			return;
		}

		if (email.length() == 0) {
			rq.historyBack("cellphoneNo를 입력해주세요.");
			return;
		}

		if (cellphoneNo.length() == 0) {
			rq.historyBack("email을 입력해주세요.");
			return;
		}

		ResultData joinRd = memberService.join(loginId, loginPw, loginPwConfirm, name, nickname, cellphoneNo, email);

		if (joinRd.isFail()) {
			rq.historyBack(joinRd.getMsg());
			return;
		}

		String redirectUri = "../member/login";

		rq.replace(joinRd.getMsg(), redirectUri);
	}

	private void actionDoLogout(Rq rq) {
		rq.removeSessionAttr("loginedMemberJson");
		rq.replace(null, "../../");
	}

	private void actionDoLogin(Rq rq) {
		String loginId = rq.getParam("loginId", "");
		String loginPw = rq.getParam("loginPw", "");

		if (loginId.length() == 0) {
			rq.historyBack("loginId를 입력해주세요.");
			return;
		}

		if (loginPw.length() == 0) {
			rq.historyBack("loginPw를 입력해주세요.");
			return;
		}

		ResultData loginRd = memberService.login(loginId, loginPw);

		if (loginRd.isFail()) {
			rq.historyBack(loginRd.getMsg());
			return;
		}

		Member member = (Member) loginRd.getBody().get("member");

		rq.setSessionAttr("loginedMemberJson", Ut.toJson(member, ""));
		String redirectUri = rq.getParam("redirectUri", "../article/list");

		rq.replace(loginRd.getMsg(), redirectUri);
	}

	private void actionShowLogin(Rq rq) {
		rq.jsp("usr/member/login");
	}

	private void actionShowJoin(Rq rq) {
		rq.jsp("usr/member/join");
	}
}
