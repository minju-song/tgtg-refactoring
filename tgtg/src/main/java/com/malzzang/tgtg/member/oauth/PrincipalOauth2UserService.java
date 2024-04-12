package com.malzzang.tgtg.member.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.malzzang.tgtg.member.domain.Member;
import com.malzzang.tgtg.member.domain.MemberRepository;


@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{
	
	@Autowired
	private MemberRepository memberRepository;
	
	// loadUser 함수에서 후처리가 일어남(후처리 되는 함수)
	// 구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수 
	// 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다. 
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		/*
		 * userRequest??
		 * 구글로그인 버튼 클릭 -> 구글 로그인창 -> 로그인 완료 -> code를 리턴(OAuth-client라이브러리가 받아줌) -> Access Token 요
		 * userRequest 정보 -> loadUser 함수 호츌 -> 구글로부터 회원 프로필 받아준다. 
		 */
		OAuth2User oauth2User = super.loadUser(userRequest);

		System.out.println("userRequest :" +userRequest);
		// registrationId로 어떤 OAuth로 로그인 했는지 확인가능 
		System.out.println("getClientRegistration :" +userRequest.getClientRegistration());
		System.out.println("getAccessToken :" +userRequest.getAccessToken().getTokenValue());
		System.out.println("getAttributes :" +super.loadUser(userRequest).getAttributes());
		System.out.println("oauth2User :" +oauth2User);
		
		
		// 회원가입 진행
		OAuth2UserInfo oauth2UserInfo = null;
		if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인 요청");
			oauth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			System.out.println("네이버 로그인 요청");
			oauth2UserInfo = new NaverUserInfo((Map)oauth2User.getAttributes().get("response"));
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")){
			System.out.println("카카오 로그인 요청");
			oauth2UserInfo = new KakaoUserInfo((Map)oauth2User.getAttributes());
		}
		
		System.out.println("==="+oauth2User.getAttributes());
		
//		String memberEmail = oauth2User.getAttribute("email");
		String memberEmail = oauth2UserInfo.getMemberEmail();
//		String memberSocial = userRequest.getClientRegistration().getRegistrationId(); // google, naver, kakao
		String memberSocial = oauth2UserInfo.getProvider();
//		String memberId = memberSocial+"_"+memberEmail; // google_123456789009876452
		String memberId = oauth2UserInfo.getProvideId();
		String memberRole = "ROLE_GUEST";
		
		Member memberEntity = memberRepository.findByMemberId(memberId);
		System.out.println("memberEntity : "+memberEntity);
		
		if(memberEntity == null) {
			memberEntity = Member.builder()
					.memberId(memberId)
					.memberEmail(memberEmail)
					.memberSocial(memberSocial)
					.memberRole(memberRole)
					.build();
			memberRepository.save(memberEntity);	
		} 
		
		if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {			
			return new PrincipalDetails(memberEntity, (Map) oauth2User.getAttributes().get("response"));
		} else if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
			return new PrincipalDetails(memberEntity, (Map) oauth2User.getAttributes().get("kakao_account"));
		} else {			
			return new PrincipalDetails(memberEntity, oauth2User.getAttributes());
		}
	}
}
