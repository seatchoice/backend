package com.example.seatchoice.controller;

import static org.springframework.http.HttpStatus.OK;

import com.example.seatchoice.dto.common.ApiResponse;
import com.example.seatchoice.dto.auth.Token;
import com.example.seatchoice.service.MemberService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/api/auth/login")
	@ResponseStatus(OK)
	public ApiResponse<Token> oAuthLogin(@RequestParam String code) throws IOException, ParseException {
		Token token = memberService.oAuthLogin(code);

		return new ApiResponse(token);
	}
}
