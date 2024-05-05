package com.example.demo.user.Dto;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.user.Entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpDto {

	private String username;
	private String password;
	private String nickname;
	private String address;
	private String phone;
	private String profileImg;
	private String socialType;
	private List<String> roles = new ArrayList<>();
	
	public Member toEntity(String encodedPassword, List<String> roles) {
		
		return Member.builder()
				.username(username)
				.password(encodedPassword)
				.nickname(nickname)
				.address(address)
				.phone(phone)
				.profileImg(profileImg)
				.socialType(socialType)
				.roles(roles)
				.build();
	}
}
