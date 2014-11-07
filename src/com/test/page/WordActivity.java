package com.test.page;

import java.io.IOException;
import java.util.ArrayList;












import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseIntArray;


public class WordActivity extends FragmentActivity {
	
	private static final String PREFER_NAME = "com.iflytek.setting";;
	protected AudioManager mAudioManager;
	protected static SoundPool mSoundPool;
	protected static SparseIntArray mSoundMap;
	protected AssetManager manager;
	private ViewPager mViewPager;
	protected static ArrayList<Word> mWords;
	private static int wordId = 2;
	protected static float volume;
	private SharedPreferences mSharedPreferences;
	private int nowPos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word);
		
		
		mSharedPreferences = getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
		

		// 得到AudioManager系统服务
		mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		// 设置声音池通过标准的扬声器每次只播放一个音频
		mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		
		manager = getAssets();
		//加载每个音频并把它们的streamId保存到一个map中
				mSoundMap = new SparseIntArray();
		mWords = WordLab.get(this).getWords();

		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.viewPager);

		setContentView(mViewPager);
		try {
			inSoundPool(wordId);					
		} catch (Exception e) {
			e.printStackTrace();
		}
		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {

			@Override
			public int getCount() {
				return mWords.size();
			}

			@Override
			public Fragment getItem(int pos) {
				nowPos = pos;
				Word word = mWords.get(pos);
				return WordFragment.newInstance(word.getmId());
			}

		});

		for (int i = 0; i < mWords.size(); i++) {
			if (mWords.get(i).getmId() == wordId) {
				mViewPager.setCurrentItem(i);
				break;
			}
		}

		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pos) {	
				System.out.println("onPageSelected");
				//play(pos);			
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				//System.out.println("onPageScrolled");
				play(arg0);		
			}
			
			@Override
			public void onPageScrollStateChanged(int pos) {
				System.out.println("onPageScrollStateChanged"+pos);
				try {
					inSoundPool(nowPos);					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
		//
		// if (fragment==null) {
		// fragment = new WordFragment();
		// fm.beginTransaction().add(R.id.fragmentContainer, fragment)
		// .commit();
		// }

	}
	
	public static void play(int wordId) {
		int streamId = mSoundMap.get(wordId);
	
		//使用指定的音量播放音频，不循环播放并且使用标准的播放速度
		mSoundPool.play(streamId, volume, volume, 1, 0, 1.0f);
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mSoundPool.release();
		mSoundPool = null;
	}

	public void inSoundPool(int pos) throws IOException{
		String fileName = WordActivity.mWords.get(pos).getmContext()+".mp3";
		System.out.println(fileName);
		int streamId = mSoundPool.load(manager.openFd(fileName), 1);
		System.out.println("SoundPool load:"+"pos--"+pos+"--streamId"+streamId);
		mSoundMap.put(pos, streamId);
		float streamVolumeCurrent = 
				mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = 
				mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volume = streamVolumeCurrent / streamVolumeMax;
	}
}
