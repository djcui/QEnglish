package com.test.util;

public class ShowGrade {

	public static int grade(String word, String text){
		char[] words = word.toCharArray();
		char[] texts = text.toCharArray();
		double sum = words.length;
		double point = 0;
		if(texts.length>0){
			for(int i = 0;i<sum&&i<texts.length;i++){
				if(words[i]==texts[i]){
					point++;
				}
			}
		}
		int result = (int) (point/sum*100);
		return result;
	}
}
