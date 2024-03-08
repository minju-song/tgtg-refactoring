package com.malzzang.tgtg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.malzzang.tgtg.member.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
//	@Autowired
//	AuthenticationFailureHandler customFailureHandler;
	@Autowired
	PrincipalOauth2UserService principalOauth2UserService;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http	
			.csrf().disable()
			
			//경로 권한 설정
			.authorizeHttpRequests()				
			.requestMatchers("/mypage", "/user/waitChatroom").authenticated()
			.requestMatchers("/admin/**").hasRole("ADMIN")	
			//권한 없는 접근 처리
			.anyRequest().permitAll()
			
			//로그인 설정
			.and()
			.formLogin()	// 로그인 페이지와 기타 로그인 처리 및 성공 실패 처리를 사용하겠다 
			.loginPage("/login")	// Spring에서 제공하는 login페이지가 아니라 사용자가 커스텀한 로그인 페이지를 사용할 
			.loginProcessingUrl("/loginProc")	// 인증처리 하는 URL 설정하며, "/loginProcess"가 호출되면 인증처리를 수행하는 필터 호출
												// 이는 프론트 단의 로그인 form 속성 action과 동일한 값을 가져야 함. 이는 스프링 시큐리티에서 내부적으로 인증 프로세스 진
//			.usernameParameter("memberId")
//			.passwordParameter("pwd")
//			.failureHandler(customFailureHandler)	// 인증 실패 후 별도의 처리가 필요한 경우 커스텀 핸들러 생성하여 등
			.defaultSuccessUrl("/")		// 정상적으로 인증 성공 시 이동하는 페이지 
			
			//로그아웃 설정
			.and()
			.logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/")		
			
			.and()
			.oauth2Login()
			.loginPage("/login")
			.userInfoEndpoint()
			.userService(principalOauth2UserService) // 구글 로그인이 완료된 뒤의 후처리 필요. Tip 코트X, (엑세스토큰+사용자프로필 정보)
			.and()
			.defaultSuccessUrl("/oauth2/check"); // 소셜 로그인 후 후처리 
					
		return http.build();
		
	}
}
