package com.example.seatchoice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class QueryParsingUtil {

	public String escape(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			// These characters are part of the query syntax and must be escaped
			if (String.valueOf(c).matches("[^a-zA-Z0-9 ㄱ-ㅎㅏ-ㅣ가-힣\\s]")) {
				sb.append("*").append('\"').append(c).append('\"').append("*");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

}
