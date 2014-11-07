package com.test.page;

import java.util.ArrayList;

import android.content.Context;

public class WordLab {
	private ArrayList<Word> mWords;
	
	
	private static WordLab sWordLab;
	private Context mAppContext;
	
	private WordLab(Context appContext){
		mAppContext = appContext;
		mWords = new ArrayList<Word>();
		Word w1 = new Word("Hello", 0);
		Word w2 = new Word("World", 1);
		Word w3 = new Word("Bye", 2);
		Word w4 = new Word("Admit", 3);
		Word w5 = new Word("Engineer", 4);
		Word w6 = new Word("Focus", 5);
		Word w7 = new Word("Drink", 6);
		Word w8 = new Word("Solution", 7);
		Word w9 = new Word("World", 8);
		mWords.add(w1);
		mWords.add(w2);
		mWords.add(w3);
		mWords.add(w4);
		mWords.add(w5);
		mWords.add(w6);
		mWords.add(w7);
		mWords.add(w8);
		mWords.add(w9);
	}
	
	public static WordLab get(Context c) {
		if (sWordLab == null) {
			sWordLab = new WordLab(c.getApplicationContext());
		}
		return sWordLab;
	}
	
	public ArrayList<Word> getWords(){
		return mWords;
	}
	
	public Word getWord(int id) {
		for (Word c : mWords) {
			if (c.getmId()==id) {
				return c;
			}
		}
		return null;
	}
}
