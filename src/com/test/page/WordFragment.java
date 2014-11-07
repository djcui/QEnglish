package com.test.page;

import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.RecognizerListener;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechRecognizer;
import com.test.api.JsonParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WordFragment extends Fragment implements OnClickListener{
	private static String TAG = "WordFragment";
	private SpeechRecognizer mIat;
	private SharedPreferences mSharedPreferences;
	private Toast mToast;
	private String iattext ;
	
	private Button mButton;
	private Button mButtonRecord;
	private boolean clicked=false;
	private String text;
	private int wordId;
	public static WordFragment newInstance(int id) {
		Bundle args = new Bundle();
		args.putSerializable("WORD_ID", id);
		WordFragment fragment = new WordFragment();
		fragment.setArguments(args);
		return fragment;
		
	}
	
	@SuppressLint("ShowToast")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 初始化识别对象
		mIat = new SpeechRecognizer(getActivity(), mInitListener);
		
		mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG);	
		wordId = (Integer) getArguments().getSerializable("WORD_ID");
		System.out.println("Fragment onCreate"+wordId);
		text = WordLab.get(getActivity()).getWord(wordId).getmContext();
		super.onCreate(savedInstanceState);
		
		
	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_word, parent,false);
		String PREFER_NAME = "com.iflytek.setting";
		mSharedPreferences = getActivity().getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
		mButton = (Button) v.findViewById(R.id.word_title);
		mButton.setText(text);
		mButton.setOnClickListener(this);
		mButtonRecord = (Button) v.findViewById(R.id.iat_recognize_bind);
		mButtonRecord.setText("start");
		mButtonRecord.setOnClickListener(this);

		
		return v;
	}

	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule module, int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
        	if (code == ErrorCode.SUCCESS) {
        		mButtonRecord.setEnabled(true);
        		//findViewById(R.id.iat_recognize_intent).setEnabled(true);
        	}
		}
    };
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		

		case R.id.iat_recognize_bind:
			// 转写会话
			//((EditText)findViewById(R.id.iat_text)).setText("");
			if (clicked) {
				System.out.println("clicked true:"+clicked);
				mIat.stopListening(mRecognizerListener);
				Log.d("stop_bottom", "stopListening");
				break;
			}else {
				System.out.println("clicked false:"+clicked);				
				setParam();
				mIat.startListening(mRecognizerListener);
				clicked=true;
				mButtonRecord.setText("Follow!");
				//mIat.cancel(mRecognizerListener);
				break;
				
			}
			
		case R.id.word_title:
			WordActivity.play(wordId);
			break;
		//case R.id.iat_cancel:
			//mIat.cancel(mRecognizerListener);
			//break;
		default:
			break;
		}
		
			
		
	}
	
	@Override
	public void onDestroy() {
		System.out.println("Fragment"+wordId+"onDestroy");
		mIat.cancel(mRecognizerListener);
		super.onDestroy();
	}
	
	public void setParam(){
		
		mIat.setParameter(SpeechConstant.LANGUAGE, mSharedPreferences.getString("iat_language_preference", "en_us"));
		mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
//		mIat.setParameter(SpeechConstant.ACCENT, mSharedPreferences.getString("accent_preference", "mandarin"));
//		mIat.setParameter(SpeechConstant.DOMAIN, mSharedPreferences.getString("domain_perference", "iat"));
		mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "500"));
		
		String param = null;
		param = "asr_ptt="+mSharedPreferences.getString("iat_punc_preference", "0");
		mIat.setParameter(SpeechConstant.PARAMS, param+",asr_audio_path=/sdcard/iflytek/wavaudio.pcm");

	}	
	
	private RecognizerListener mRecognizerListener = new RecognizerListener.Stub() {
        
        @Override
        public void onVolumeChanged(int v) throws RemoteException {
            //showTip("onVolumeChanged："	+ v);
        }
        
        @Override
        public void onResult(final RecognizerResult result, boolean isLast)
                throws RemoteException {
        	getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != result) {
		            	// 显示
						Log.d(TAG, "recognizer result：" + result.getResultString());
						iattext = JsonParser.parseIatResult(result.getResultString());
						System.out.println("识别结果"+iattext+"---");
						
						if (iattext.equals(text)) {
							mButtonRecord.setText(iattext);
			        		showTip("Right!");
						}else {
							mButtonRecord.setText("Try again!");
							showTip("What you say sounds like \""+iattext+"\",Please try again!");
						}
						clicked = false;
		            } else {
		            	clicked=false;
		                Log.d(TAG, "recognizer result : null");
		                showTip("无识别结果");
		            }	
				}
			});
            
        }
        
        @Override
        public void onError(int errorCode) throws RemoteException {
			showTip("onError Code："	+ errorCode);
        }
        
        @Override
        public void onEndOfSpeech() throws RemoteException {
        //	showTip();
			
        }
        
        @Override
        public void onBeginOfSpeech() throws RemoteException {
			showTip("onBeginOfSpeech");
        }
    };
    
    private void showTip(final String str)
	{
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mToast.setText(str);
				mToast.show();
			}
		});
	}
    
    
}
