package com.example.seatchoice.service.auth;

import static com.example.seatchoice.type.LoginType.KAKAO;
import static com.example.seatchoice.type.RequestMethod.GET;
import static com.example.seatchoice.type.RequestMethod.POST;

import com.example.seatchoice.dto.auth.OauthAttribute;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoService {

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String clientSecret;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String redirectUri;

	@Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
	private String grantType;

	@Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
	private String tokenUri;

	@Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
	private String userInfoUri;

	@Value("${spring.security.oauth2.client.provider.kakao.logout-uri}")
	private String logoutUri;

	public String getToken(String code) throws IOException, ParseException {
		URL url = new URL(tokenUri);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod(POST.name());
		urlConnection.setDoOutput(true);

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
		StringBuilder sb = new StringBuilder();
		sb.append("grant_type=" + grantType);
		sb.append("&client_id=" + clientId);
		sb.append("&redirect_uri=" + redirectUri);
		sb.append("&code=" + code);
		sb.append("&client_secret=" + clientSecret);

		bw.write(sb.toString());
		bw.flush();

		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String line = "";
		String result = "";
		while ((line = br.readLine()) != null) {
			result += line;
		}

		JSONParser parser = new JSONParser();
		JSONObject tokenInfo = (JSONObject) parser.parse(result);
		String accessToken = "Bearer " + tokenInfo.get("access_token").toString();

		br.close();
		bw.close();

		return accessToken;
	}


	public OauthAttribute getUserInfo(String accessToken) throws IOException, ParseException {
		URL url = new URL(userInfoUri);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestProperty("Authorization", accessToken);
		urlConnection.setRequestMethod(GET.name());

		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		String line = "";
		String result = "";
		while((line = br.readLine()) != null)
		{
			result += line;
		}

		JSONParser parser = new JSONParser();
		JSONObject userInfo = (JSONObject) parser.parse(result);
		JSONObject kakaoAccount = (JSONObject) userInfo.get("kakao_account");
		JSONObject profile = (JSONObject) kakaoAccount.get("profile");

		String oauthId = userInfo.get("id").toString();
		String nickname = profile.get("nickname").toString();
		String email;
		try {
			email = kakaoAccount.get("email").toString();
		} catch (NullPointerException e) {
			email = null;
		}

		br.close();

		return OauthAttribute.builder()
			.oauthId(oauthId)
			.nickname(nickname)
			.email(email)
			.loginType(KAKAO)
			.build();
	}

	public void logout(String accessToken) throws IOException {
		URL url = new URL(logoutUri);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestProperty("Authorization", accessToken);
		urlConnection.setRequestMethod(POST.name());
	}
}
