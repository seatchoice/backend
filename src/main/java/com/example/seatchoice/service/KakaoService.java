package com.example.seatchoice.service;

import com.example.seatchoice.dto.auth.UserInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KakaoService {

	@Value("${oauth.kakao.client-id}")
	private String clientId;

	@Value("${oauth.kakao.redirect-uri}")
	private String redirectUri;

	@Value("${oauth.kakao.token-uri}")
	private String tokenUrI;

	@Value("${oauth.kakao.user-info-uri}")
	private String userInfoUri;

	public String getToken(String code) throws IOException, ParseException {
		URL url = new URL(tokenUrI);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
		StringBuilder sb = new StringBuilder();
		sb.append("grant_type=authorization_code");
		sb.append("&client_id=" + clientId);
		sb.append("&redirect_uri=" + redirectUri);
		sb.append("&code=" + code);

		bw.write(sb.toString());
		bw.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String line = "";
		String tokenInfo = "";
		while ((line = br.readLine()) != null) {
			tokenInfo += line;
		}

		JSONParser parser = new JSONParser();
		JSONObject elem = (JSONObject) parser.parse(tokenInfo);
		String accessToken = elem.get("access_token").toString();

		br.close();
		bw.close();

		return accessToken;
	}

	public UserInfo getUserInfo(String access_token) throws IOException, ParseException {
		URL url = new URL(userInfoUri);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Bearer " + access_token);
		urlConnection.setRequestMethod("GET");

		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String line = "";
		String userInfo = "";
		while((line = br.readLine()) != null)
		{
			userInfo += line;
		}

		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(userInfo);
		JSONObject kakaoAccount = (JSONObject) obj.get("kakao_account");
		JSONObject profile = (JSONObject) kakaoAccount.get("profile");

		String oauthId = obj.get("id").toString();
		String nickname = profile.get("nickname").toString();
		String email = null;

		try {
			email = kakaoAccount.get("email").toString();
		} catch (NullPointerException e) {

		}

		br.close();

		return UserInfo.builder()
			.oauthId(oauthId)
			.nickname(nickname)
			.email(email)
			.build();
	}
}
