package com.example.kent.connectfour;

import android.os.*;
import android.os.Message;
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.Socket;


/**
 * Created by kent on 15-12-10.
 */
public class ReadThread extends Thread {
    DataInputStream read;
    Socket server;
    GameMain main;
    Handler handle;
    Boolean running = true;

    public ReadThread(Socket _server, GameMain _main, DataInputStream in, Handler _handle){
        server = _server;
        main = _main;
        read = in;
        handle = _handle;
    }

    @Override
    public void run() {
        while(running){
            try {
                Message msg = handle.obtainMessage();
                String mes = read.readUTF();
                Bundle b  = new Bundle();
                b.putString("code", mes);
                msg.setData(b);
                handle.sendMessage(msg);
            } catch (IOException e){

            }
        }
    }

    public void kill(){
        running = false;
    }
}
