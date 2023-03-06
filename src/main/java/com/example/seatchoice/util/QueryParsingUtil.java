package com.example.seatchoice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class QueryParsingUtil {

	public String escape(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			// These characters are part of the query syntax and must be escaped
			if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
				|| c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
				|| c == '*' || c == '?' || c == '|' || c == '&' || c == '/' || c == '.' || c == ',')
			{
				sb.append("*").append('\"').append(c).append('\"').append("*");
			} else {
				sb.append(c);
			}
		}
		System.out.println(sb);
		return sb.toString();
	}

}
