package com.test.page;

import java.util.UUID;

import android.R.integer;

public class Word {
	private String mContext;
	private int mId;
	
	

	public Word(String mContext, int mId) {
		super();
		this.mContext = mContext;
		this.mId = mId;
	}

	public int getmId() {
		return mId;
	}

	public String getmContext() {
		return mContext;
	}

	public void setmContext(String mContext) {
		this.mContext = mContext;
	}


	
	
}
