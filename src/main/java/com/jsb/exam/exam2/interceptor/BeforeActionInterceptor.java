package com.jsb.exam.exam2.interceptor;

import com.jsb.exam.exam2.container.Container;
import com.jsb.exam.exam2.dto.Member;
import com.jsb.exam.exam2.http.Rq;
import com.jsb.exam.exam2.service.MemberService;
import com.jsb.exam.exam2.util.Ut;

public class BeforeActionInterceptor extends Interceptor {

	private MemberService memberService;

	public void init() {
		memberService = Container.memberService;
	}
	
	// 로그인 회원정보를 저장하는 메서드
	@Override
	public boolean runBeforeAction(Rq rq) {
		// 로그인 회원정보를 json 형식으로 저장
		String loginedMemberJson = rq.getSessionAttr("loginedMemberJson", "");

		// 저장된 회원의 정보(json)를 각각 로그인 변수에 저장
		if (loginedMemberJson.length() > 0) {
			rq.setLogined(true);
			rq.setLoginedMember(Ut.toObjFromJson(loginedMemberJson, Member.class));
			rq.setLoginedMemberId(rq.getLoginedMember().getId());
			rq.setAdmin(memberService.isAdmin(rq.getLoginedMember()));
			if(rq.getLoginedMember() != null) {

			}
		}
		
		rq.setAttr("rq", rq);

		return true;
	}

}
