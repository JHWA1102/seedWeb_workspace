package com.example.demo.user.Controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.demo.user.Config.SecurityUtil;
import com.example.demo.user.Config.jwt.JwtToken;
import com.example.demo.user.Dto.MemberDto;
import com.example.demo.user.Dto.SignInDto;
import com.example.demo.user.Dto.SignUpDto;
import com.example.demo.user.Repository.MemberRepository;
import com.example.demo.user.Service.MemberService;
import com.example.demo.user.Vo.GoogleLoginResponse;
import com.example.demo.user.Vo.GoogleOAuthRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
	
	private final MemberService memberService;
	private final MemberRepository memberRepository;
	
	@Value("${google.auth.url}")
	private String googleAuthUrl;
	
	@Value("${google.login.url}")
	private String googleLoginUrl;
	
	@Value("${google.client.id}")
	private String googleClientId;
	
	@Value("${google.redirect.url}")
	private String googleRedirectUrl;
	
	@Value("${google.secret}")
	private String googleClientSecret;
	
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
	
	/* ==========================================================  
	 * 2024.05.05 SJH
	 * 구글 로그인 / 회원가입 개발
	 * ==========================================================
	 */
	
	// 구글 로그인창 호출
	// http://localhost:8080/members/getGoogleAuthUrl
	@GetMapping(value = "/getGoogleAuthUrl")
	public ResponseEntity<?> getGoogleAuthUrl(HttpServletRequest request) throws Exception {
		
		String reqUrl = googleLoginUrl + "/o/oauth2/v2/auth?client_id=" + googleClientId + "&redirect_uri=" + googleRedirectUrl 
				+ "&response_type=code&scope=email%20profile%20openid&access_type=offline";
		log.info("myLog-LoginUrl : {}" + googleLoginUrl);
		log.info("myLog-ClientId : {}" + googleClientId);
		log.info("myLog-RedirectUrl : {}" + googleRedirectUrl);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(reqUrl));
		
		// 1. reqUrl 구글로그인 창을 띄우고, 로그인 후 /members/oauth_google_check으로 리다이렉션하게 한다.
		return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
	}
	
	// 구글에서 리다이렉션
	@GetMapping(value = "/oauth_google_check")
	public String oauth_google_check(HttpServletRequest request, @RequestParam(value = "code") String authCode
			, HttpServletResponse response) throws Exception {
		
		// 2. 구글에 등록된 레드망고 설정정보를 보내어 약속된 토큰을 받기위한 객체 생성
		GoogleOAuthRequest googleOAuthRequest = GoogleOAuthRequest
													.builder()
													.clientId(googleClientId)
													.clientSecret(googleClientSecret)
													.code(authCode)
													.redirectUri(googleRedirectUrl)
													.grantType("authorization_code")
													.build();
		
		RestTemplate restTemplate = new RestTemplate();
		
		// 3. 토큰요청을 한다.
		ResponseEntity<GoogleLoginResponse> apiResponse = restTemplate.postForEntity(googleAuthUrl + "/token", googleOAuthRequest, GoogleLoginResponse.class);
		
		// 4.받은 토큰을 토큰 객체에 저장
		GoogleLoginResponse googleLoginResponse = apiResponse.getBody();
		
		log.info("responseBody {}", googleLoginResponse.toString());
		
		String googleToken = googleLoginResponse.getId_token();
		
		// 5. 받은 토큰을 구글에 보내 유저정보를 얻는다.
		String requestUrl = UriComponentsBuilder.fromHttpUrl(googleAuthUrl + "/tokeninfo").queryParam("id_token", googleToken).toUriString();
		
		// 6. 허가된 토큰의 유저정보를 결과로 받는다.
		String resultJson = restTemplate.getForObject(requestUrl, String.class);
		
		try {
			// 7-1. ObjectMapper를 생성한다.
			ObjectMapper objectMapper = new ObjectMapper();
			
			// 7-2. JSON 문자열을 JsonNode로 파싱한다.
			JsonNode jsonNode = objectMapper.readTree(resultJson);
			
			// 7-3. 필요한 데이터를 가져온다.
			String username = jsonNode.get("email").asText();
			
//			MemberDto memberDto = new MemberDto(null, username, "nickname1", "address1", "phone1", "profileImg1");
			
			// 7-4. email을 확인하여 기존 회원이면 로그인 시키고 신규 회원이면 회원 가입을 시킨다.
			if(memberRepository.existsByUsername(username)) {
				System.out.println("기존 회원!");
			} else {
				System.out.println("신규 회원!");
				SignUpDto signUpDto = new SignUpDto(username, "1234", "nickname1", "address1", "phone1", "profileImg1", "Google", null);
				signUp(signUpDto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultJson;
	}
	
	
}
