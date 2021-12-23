package com.example.jankenfuuchan;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import io.realm.RealmResults;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainJanken extends AppCompatActivity {

    //各部品の変数
    Spinner spinnerPlayer1;
    Spinner spinnerPlayer2;
    Spinner spinnerMaxTimes;

    ImageView imagePlayer1;
    ImageView imagePlayer2;

    TextView textComment;
    TextView textGameCount;
    TextView textWinCount1;
    TextView textWinCount2;
    TextView textJanken1;
    TextView textJanken2;

    ImageButton buttonGuu;
    ImageButton buttonChoki;
    ImageButton buttonPaa;

    Button buttonStart;

    Realm realm;

    PlayerBmp player1;
    PlayerBmp player2;
    List<PlayerBmp> listPlayer;

    final int GUU =0;
    final int CHOKI = 1;
    final int PAA = 2;
    final int JANKEN = 0;
    final int AIKO = 1;
    final int RESULT = 2;

    int winCount1 = 0;
    int winCount2 = 0;
    int gameCount = 0;
    int gameflag;
    int maxGameCount;



    class PlayerBmp{
        private long id;
        private String name;
        private Bitmap bitmapReady;
        private Bitmap bitmapGuu;
        private Bitmap bitmapChoki;
        private Bitmap bitmapPaa;
        private Bitmap bitmapWin;
        private Bitmap bitmapLose;

        public PlayerBmp(Player player) {
            this.id = player.id;
            this.name = player.name;
            this.bitmapReady = MyUtils.getImageFromByte(player.byteReady);
            this.bitmapGuu = MyUtils.getImageFromByte(player.byteGuu);;
            this.bitmapChoki = MyUtils.getImageFromByte(player.byteChoki);;
            this.bitmapPaa = MyUtils.getImageFromByte(player.bytePaa);;
            this.bitmapWin = MyUtils.getImageFromByte(player.byteWin);;
            this.bitmapLose = MyUtils.getImageFromByte(player.byteLose);;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_janken);
        findViewById(R.id.layoutMain).getBackground().setAlpha(120);

        realm = Realm.getDefaultInstance();

        //各部品のオブジェクト取得
        spinnerPlayer1 = findViewById(R.id.spinnerPlayer1);
        spinnerPlayer2 = findViewById(R.id.spinnerPlayer2);
        spinnerMaxTimes = findViewById(R.id.spinnerMaxTimes);

        imagePlayer1 = findViewById(R.id.imagePlayer1);
        imagePlayer2 = findViewById(R.id.imagePlayer2);

        textComment = findViewById(R.id.textComent);
        textGameCount = findViewById(R.id.textGameTimes);
        textWinCount1 = findViewById(R.id.textWinTimes1);
        textWinCount2 = findViewById(R.id.textWinTimes2);
        textJanken1 = findViewById(R.id.textJanken1);
        textJanken2 = findViewById(R.id.textJanken2);

        buttonGuu = findViewById(R.id.buttonGuu);
        buttonChoki = findViewById(R.id.buttonChoki);
        buttonPaa = findViewById(R.id.buttonPaa);

        buttonStart = findViewById(R.id.buttonStart);



        //spinnerPlayer1.setEnabled(true);


        //初期化メソッドの呼び出し
        init();




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    //初期化メソッド
    private void init(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Player player = realm.where(Player.class).equalTo("id",0).findFirst();
                //ワンワンの画像がなければRealmに登録してプレイヤー１にセット
                if(player == null){
                    player = realm.createObject(Player.class,0);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.wanwanready);
                    byte[] bytearray = MyUtils.getByteFromImage(bitmap);
                    player.name = "ワンワン";
                    player.byteReady = bytearray;
                    bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.wanwanguu);
                    bytearray = MyUtils.getByteFromImage(bitmap);
                    player.byteGuu = bytearray;
                    bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.wanwanchoki);
                    bytearray = MyUtils.getByteFromImage(bitmap);
                    player.byteChoki = bytearray;
                    bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.wanwanper2);
                    bytearray = MyUtils.getByteFromImage(bitmap);
                    player.bytePaa = bytearray;
                    bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.wanwanwin);
                    bytearray = MyUtils.getByteFromImage(bitmap);
                    player.byteWin = bytearray;
                    bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.wanwanlose);
                    bytearray = MyUtils.getByteFromImage(bitmap);
                    player.byteLose = bytearray;
                }
                player1 = new PlayerBmp(player);

                //うーたんの画像がなければRealmに登録してプレイヤー２にセット
                player2 = new PlayerBmp(player);


                //プレイヤーリストとスピナーの選択リストを取得
                RealmResults results = realm.where(Player.class).findAll();
                listPlayer = new ArrayList<>();
                List<String> listPlayerName =  new ArrayList<>();
                for(Object players:results){
                    listPlayer.add(new PlayerBmp((Player)players));
                    listPlayerName.add(((Player) players).name);
                }

                //スピナー作成とアダプターをセット
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainJanken.this,R.layout.spinner3,listPlayerName);
                spinnerPlayer1.setAdapter(adapter);
                spinnerPlayer2.setAdapter(adapter);

                String[] array = {"1","3","5","7","10","15","20"};
                adapter = new ArrayAdapter<>(MainJanken.this,R.layout.spinner2,array);
                spinnerMaxTimes.setAdapter(adapter);
                spinnerMaxTimes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        maxGameCount = Integer.parseInt(((Spinner)adapterView).getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                reset();
            }
        });

    }
    //リセット処理
    void reset(){
        player1 = listPlayer.get(0);
        player2 = listPlayer.get(0);
        imagePlayer1.setImageBitmap(player1.bitmapReady);
        imagePlayer2.setImageBitmap(player2.bitmapReady);

        spinnerMaxTimes.setSelection(0);
        spinnerPlayer1.setSelection(0);
        spinnerPlayer2.setSelection(0);

        textJanken1.setText("   ");
        textJanken2.setText("   ");
        winCount1 = 0;
        winCount2 = 0;
        textWinCount1.setText("０");
        textWinCount2.setText("０");
        gameCount = 1;
        textGameCount.setText(gameCount + "かいめ");

        textComment.setText(player1.name + "と\n" + player2.name + "の\n" + "じゃんけん！");

        buttonGuu.setEnabled(false);
        buttonChoki.setEnabled(false);
        buttonPaa.setEnabled(false);
        buttonStart.setEnabled(true);
        buttonStart.setText("じゃんけん");

    }
    public void onButtonReset(View view) {
        reset();
    }

    public void onButtonStart(View view) {
        buttonGuu.setEnabled(true);
        buttonChoki.setEnabled(true);
        buttonPaa.setEnabled(true);
        buttonStart.setEnabled(false);
        imagePlayer1.setImageBitmap(player1.bitmapReady);
        imagePlayer2.setImageBitmap(player2.bitmapReady);
        textJanken1.setText("");
        textJanken2.setText("");
        if(gameflag == AIKO){
            textComment.setText("あいこで・・・");
        }
        else
        textComment.setText("じゃんけん・・・");
    }

    public void onButtonPaa(View view) {
        imagePlayer1.setImageBitmap(player1.bitmapPaa);
        textJanken1.setText("パー");
        janken();
        gameResult(PAA);
    }
    public void onButtonChoki(View view) {
        imagePlayer1.setImageBitmap(player1.bitmapChoki);
        textJanken1.setText("チョキ");
        janken();
        gameResult(CHOKI);
    }
    public void onButtonGuu(View view) {
        imagePlayer1.setImageBitmap(player1.bitmapGuu);
        textJanken1.setText("グー");
        janken();
        gameResult(GUU);
    }
    void janken(){
        buttonPaa.setEnabled(false);
        buttonGuu.setEnabled(false);
        buttonChoki.setEnabled(false);
        buttonStart.setEnabled(true);
        if(gameflag == AIKO)
            textComment.setText("し　ょ　！");
        else
            textComment.setText("ぽ　ん　！");
    }


    void gameResult(int myHand){

        //プレイヤー２（コンピューター）の手を決める
        int comHand = (int)(Math.random() * 3);
        switch (comHand){
            case GUU :
                imagePlayer2.setImageBitmap(player2.bitmapGuu);
                textJanken2.setText("グー");
                break;
            case CHOKI :
                imagePlayer2.setImageBitmap(player2.bitmapChoki);
                textJanken2.setText("チョキ");
                break;
            case PAA :
                imagePlayer2.setImageBitmap(player2.bitmapPaa);
                textJanken2.setText("パー");
                break;
        }
        //勝敗判定
        int gameResult = (comHand - myHand + 3) % 3;
        switch (gameResult){
            //あいこの場合
            case 0 :
                gameflag = AIKO;
                buttonStart.setText("あいこ");

                break;
            //勝った場合
            case 1 :
                gameflag = JANKEN;
                buttonStart.setText("じゃんけん");
                winCount1++;
                break;
            //負けた場合
            case 2 :
                gameflag = JANKEN;
                buttonStart.setText("じゃんけん");
                winCount2++;
                break;
        }
        textWinCount1.setText("" + winCount1);
        textWinCount2.setText("" + winCount2);

        if(gameCount == maxGameCount){
            gameflag = RESULT;
            buttonStart.setText("けっか");}
        if(gameflag == JANKEN){
        gameCount++;
        textGameCount.setText( gameCount + "かいめ");}



       // if(gmaxGameCount)

    }
}