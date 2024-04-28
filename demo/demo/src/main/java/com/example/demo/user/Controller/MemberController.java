package com.example.demo.user.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.user.Config.SecurityUtil;
import com.example.demo.user.Config.jwt.JwtToken;
import com.example.demo.user.Dto.MemberDto;
import com.example.demo.user.Dto.SignInDto;
import com.example.demo.user.Dto.SignUpDto;
import com.example.demo.user.Service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
	
	private final MemberService memberService;
	
	@PostMapping("/sign-in")
	public JwtToken signIn(@RequestBody SignInDto signInDto) {
		System.out.println(">>>>> 값 확인!" + signInDto);
		System.out.println(">>>>> 통과!");
		String username = signInDto.getUsername().toString();
		String password = signInDto.getPassword().toString();
		log.info("request username = {}, password = {}", username, password);
		JwtToken jwtToken = memberService.signIn(username, password);
		log.info("jwtToken accessToke = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
		return jwtToken;
	}
	
	@PostMapping("/test")
	public String test() {
		return SecurityUtil.getCurrentUsername();
	}
	
	@PostMapping("/sign-up")
	public ResponseEntity<MemberDto> signUp(@RequestBody SignUpDto signUpDto) {
		MemberDto savedMemberDto = memberService.signUp(signUpDto);
		return ResponseEntity.ok(savedMemberDto);
		
	}

}
