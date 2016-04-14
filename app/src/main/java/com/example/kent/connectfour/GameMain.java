package com.example.kent.connectfour;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.*;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * Created by kent on 15-12-10.
 */
public class GameMain {

    MainActivity main;
    LinearLayout list;
    TextViewHolder[][] squares;
    int playerNum = 0;
    DataOutputStream out = null;
    Socket server = null;
    Boolean isTurn = false;
    Boolean playerSet = false;
    int mc;
    int oc;
    ScrollView scrollView;
    Boolean isPlayerOne = false;
    ReadThread read;
    DataInputStream in;
    MediaPlayer media;


    public GameMain(MainActivity _main){
        main = _main;
        createGame();
    }

    public void createGame(){
        media = new MediaPlayer().create(main, R.raw.sundial);
        media.setLooping(true);
        media.start();
        list = (LinearLayout) main.findViewById(R.id.list);
        scrollView = (ScrollView) main.findViewById(R.id.scroll);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            server = new Socket("ec2-52-33-195-86.us-west-2.compute.amazonaws.com", 1138);
            list.addView(createNewTextView("Connected"));
            out = new DataOutputStream(server.getOutputStream());
            in = new DataInputStream(server.getInputStream());
            Handler handle = new Handler(){
                @Override
                public void handleMessage(android.os.Message mes){
                    String str = mes.getData().getString("code");
                    handle(str);
                }
            };
            ReadThread read = new ReadThread(server, this, in, handle);
            read.start();
        } catch (IOException e){
            list.addView(createNewTextView("Server Connection Failed: " + e.getMessage()));
        }




        int count = 0;
        squares = new TextViewHolder[7][5];

        for (int i = 0; i < 7; i++){
            for (int j = 0; j < 5; j++){
                count++;
                int resID = main.getResources().getIdentifier("B"+count, "id", main.getPackageName());
                squares[i][j] = new TextViewHolder((TextView) main.findViewById(resID), j, i);
                squares[i][j].getView().setClickable(true);
                squares[i][j].getView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isTurn) {
                            isTurn = false;
                            TextViewHolder text = findTextViewHolder((TextView) v);
                            if (text != null) {
                                if (!fillColumn(text.getColumn(), mc)) {
                                    list.addView(createNewTextView("invalid move"));
                                    isTurn = true;
                                } else {
                                    send(".c"+text.getColumn());
                                    addToList("Waiting for other player...");
                                }

                            }
                        }
                    }
                });
            }
        }
    }

    public TextView createNewTextView(String text) {
        final TextView textView = new TextView(main);
        textView.setText(text);
        return textView;
    }

    private TextViewHolder findTextViewHolder (TextView v){
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                if(squares[i][j].getView().equals(v)){
                    return squares[i][j];
                }
            }
        }
        return null;
    }

    private Boolean fillColumn(int colNum, int col){
        for(int i = 4; i > -1; i--){
            if(!squares[colNum][i].isSquareClicked()){
                squares[colNum][i].getView().setBackgroundColor(col);
                return true;
            }

        }
        return false;
    }

    public void setPlayer(Boolean _player){
        isPlayerOne = _player;
        if (isPlayerOne){
            playerNum = 1;
        } else {
            playerNum = 2;
        }
    }

    public void send(String code){
        try {
            out.writeUTF(code);
        } catch (IOException e){
            addToList("write error");
        }
    }

    public void handle(String _mes) {
        if (_mes.equals(".k0")){
            isTurn = false;
            gameOverAlert("Player disconnected");
        }else if (_mes.equals(".p1")) {
            if (!playerSet) {
                setPlayer(true);
                mc = Color.RED;
                oc = Color.BLUE;
                addToList("You are connected as player one");
                playerSet = true;
            }
        } else if (_mes.equals(".p2")) {
            if (!playerSet) {
                setPlayer(false);
                mc = Color.BLUE;
                oc = Color.RED;
                addToList("You are connected as player two");
                playerSet = true;
            }
        } else if (_mes.equals(".s0")) {
            isTurn = true;
        } else if (_mes.equals(".d0")){
            isTurn = false;
            gameOverAlert("Draw");
        } else if (_mes.equals(".w0")){
            isTurn = false;
            gameOverAlert("You win");
        } else if (_mes.equals(".c0") || _mes.equals(".c0w")) {
            if (!isTurn) {
                if (!fillColumn(0, oc)) {
                    addToList("invalid other player move");
                } else {
                    if (_mes.equals(".c0")) {
                        addToList("Other Player move at column 0");
                        isTurn = true;
                        addToList("Your turn");
                    } else if (_mes.equals(".c0w")){
                        addToList("game over");
                        isTurn = false;
                        gameOverAlert("You Lose");
                    }
                }
            }
        } else if (_mes.equals(".c1") || _mes.equals(".c1w")) {
            if (!isTurn) {
                if (!fillColumn(1, oc)) {
                    addToList("invalid other player move");
                } else {
                    addToList("Other Player move at column 1");
                    if (_mes.equals(".c1")) {
                        isTurn = true;
                        addToList("Your turn");
                    } else if (_mes.equals(".c1w")){
                        isTurn = false;
                        if (playerNum == 1) {
                            gameOverAlert("Player 2 wins");
                        } else if (playerNum == 2){
                            gameOverAlert("Player 1 wins");
                        }
                    }
                }
            }
        } else if (_mes.equals(".c2") || _mes.equals(".c2w")) {
            if (!isTurn) {
                if (!fillColumn(2, oc)) {
                    addToList("invalid other player move");
                } else {
                    addToList("Other Player move at column 2");
                    if (_mes.equals(".c2")) {
                        isTurn = true;
                        addToList("Your turn");
                    } else if (_mes.equals(".c2w")){
                        isTurn = false;
                        if (playerNum == 1) {
                            gameOverAlert("Player 2 wins");
                        } else if (playerNum == 2){
                            gameOverAlert("Player 1 wins");
                        }
                    }
                }
            }
        }else if (_mes.equals(".c3") || _mes.equals(".c3w")) {
            if (!isTurn) {
                if (!fillColumn(3, oc)) {
                    addToList("invalid other player move");
                } else {
                    addToList("Other Player move at column 3");
                    if (_mes.equals(".c3")) {
                        isTurn = true;
                        addToList("Your turn");
                    } else if (_mes.equals(".c3w")){
                        isTurn = false;
                        if (playerNum == 1) {
                            gameOverAlert("Player 2 wins");
                        } else if (playerNum == 2){
                            gameOverAlert("Player 1 wins");
                        }
                    }
                }
            }
        } else if (_mes.equals(".c4") || _mes.equals(".c4w")) {
            if (!isTurn) {
                if (!fillColumn(4, oc)) {
                    addToList("invalid other player move");
                } else {
                    addToList("Other Player move at column 4");
                    if (_mes.equals(".c4")) {
                        isTurn = true;
                        addToList("Your turn");
                    } else if (_mes.equals(".c4w")){
                        isTurn = false;
                        if (playerNum == 1) {
                            gameOverAlert("Player 2 wins");
                        } else if (playerNum == 2){
                            gameOverAlert("Player 1 wins");
                        }
                    }
                }
            }
        }else if (_mes.equals(".c5") || _mes.equals(".c5w")) {
            if (!isTurn) {
                if (!fillColumn(5, oc)) {
                    addToList("invalid other player move");
                } else {
                    addToList("Other Player move at column 5");
                    if (_mes.equals(".c5")) {
                        isTurn = true;
                        addToList("Your turn");
                    } else if (_mes.equals(".c5w")){
                        isTurn = false;
                        if (playerNum == 1) {
                            gameOverAlert("Player 2 wins");
                        } else if (playerNum == 2){
                            gameOverAlert("Player 1 wins");
                        }
                    }
                }
            }
        } else if (_mes.equals(".c6") || _mes.equals(".c6w")) {
            if (!isTurn) {
                if (!fillColumn(6, oc)) {
                    addToList("invalid other player move");
                } else {
                    addToList("Other Player move at column 6");
                    if (_mes.equals(".c6")) {
                        isTurn = true;
                        addToList("Your turn");
                    } else if (_mes.equals(".c6w")){
                        isTurn = false;
                        if (playerNum == 1) {
                            gameOverAlert("Player 2 wins");
                        } else if (playerNum == 2){
                            gameOverAlert("Player 1 wins");
                        }
                    }
                }
            }
        } else {
            addToList(_mes);
        }
    }

    public void addToList(String mes){
        list.addView(createNewTextView(mes));
        scrollView.post(new Runnable() {
            @Override
            public void run() {

                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void gameOverAlert(String mes){
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder.setMessage(mes)
                .setTitle("Game Over")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        main.onDestroy();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void endgame(){
        try {
            media.stop();
            read.kill();
            in.close();
            out.close();
            server.close();

        } catch (IOException e) {
            addToList("Error closing streams");
        } catch (NullPointerException e){

        }

    }


}
