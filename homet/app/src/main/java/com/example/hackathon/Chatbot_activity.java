package com.example.hackathon;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class Chatbot_activity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private final  String TAG = getClass().getSimpleName();
    Intent intent;
    SpeechRecognizer speechRecognizer;
    final int PERMISSION = 1;	//permission 변수

    private final String BASE_URL = "https://5450-2001-e60-8753-a52f-45ad-2ab8-d938-98d4.jp.ngrok.io";
    private Interface mMyAPI;

    boolean recording = true;  //현재 녹음중인지 여부
    TextView recordTextView;
    Button bt_TTS;


    private String tts_text;
    private String stt_Text;
    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        initMyAPI(BASE_URL);

        CheckPermission();  //녹음 퍼미션 체크


        //UI
        recordTextView=findViewById(R.id.recordTextView);

        StartRecord();
        speechInit();
    }
    void CheckPermission() {
        //안드로이드 버전이 6.0 이상
        if ( Build.VERSION.SDK_INT >= 23 ){
            //인터넷이나 녹음 권한이 없으면 권한 요청
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED ) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET,
                                Manifest.permission.RECORD_AUDIO},PERMISSION);
            }
        }
    }
    private void initMyAPI(String baseUrl){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mMyAPI = retrofit.create(Interface.class);
    }

    private void speechInit() {

        // tts 객체 생성, 초기화
        tts = new TextToSpeech(Chatbot_activity.this, this);
    }
    //녹음 시작
    void StartRecord() {
        // RecognizerIntent 생성
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName()); // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR"); // 언어 설정
        recording = true;

        if (recording){
            Toast.makeText(getApplicationContext(), "음성 기록을 시작합니다.", Toast.LENGTH_SHORT).show();
        }
        //마이크 이미지와 텍스트 변경
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(intent);


    }

    //녹음 중지
    void StopRecord() {
        recording = false;

        speechRecognizer.stopListening();   //녹음 중지
        Toast.makeText(getApplicationContext(), "음성 기록을 중지합니다.", Toast.LENGTH_SHORT).show();

        speakOut(stt_Text);
    }
    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {
            //사용자가 말하기 시작
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {
            speechRecognizer.stopListening();
            //사용자가 말을 멈추면 호출
            //인식 결과에 따라 onError나 onResults가 호출됨
        }

        @Override
        public void onError(int error) {    //토스트 메세지로 에러 출력
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    //message = "클라이언트 에러";
                    //speechRecognizer.stopListening()을 호출하면 발생하는 에러
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    //message = "찾을 수 없음";
                    //녹음을 오래하거나 speechRecognizer.stopListening()을 호출하면 발생하는 에러
                    //speechRecognizer를 다시 생성하여 녹음 재개
                    if (recording)
                        StartRecord();
                    return; //토스트 메세지 출력 X
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            String guideStr = "에러가 발생했습니다. :";
            Toast.makeText(getApplicationContext(),  guideStr + message, Toast.LENGTH_SHORT).show();
        }

        //인식 결과가 준비되면 호출
        @Override
        public void onResults(Bundle bundle) {

            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);	//인식 결과를 담은 ArrayList

            //인식 결과
            stt_Text="";
            for (int i = 0; i < matches.size() ; i++) {
                stt_Text += matches.get(i);
            }
            /* GetPost stt_item = new GetPost();
            stt_item.stt_Text(stt_Text);

            Call<GetPost> stt_Call = mMyAPI.stt_Text(stt_item);

            stt_Call.enqueue(new Callback<GetPost>() {
                @Override
                public void onResponse(Call<GetPost> call, Response<GetPost> response) {
                    if (response.isSuccessful()){
                        Log.d(TAG, String.format("난 위너야: {%s}", response));
                        speakOut();
                    }
                    else{

                        Log.d(TAG, String.format("난 루저야: {%s}", response));
                    }
                }

                @Override
                public void onFailure(Call<GetPost> call, Throwable t) {
                    Log.d(TAG, String.format("난 텅비었어"));
                }
            }); */
               //녹음버튼을 누를 때까지 계속 녹음해야 하므로 녹음 재개

        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };
    private void postText(){

    }
    private void getText(){

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakOut(String stt_Text){
        tts_text = stt_Text;
        CharSequence text = tts_text;
        //tts_text = response;
        if (recording = true){
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
        }
        speechRecognizer.startListening(intent);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            tts.setLanguage(Locale.KOREAN);
            tts.setPitch(1);
            tts.setSpeechRate(1); // 음성 속도 지정
        } else {
            Log.e("TTS", "초기화 실패");
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if(speechRecognizer!=null){
            speechRecognizer.destroy();
            speechRecognizer.cancel();
            speechRecognizer=null;
        }
        super.onDestroy();
    }
}