/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.kent.connectfour;

import java.io.Serializable;

/**
 *
 * @author kent
 */
public class Message implements Serializable{
    String code = "";
    int player = 0;
    String message = "";
    private static final long serialVersionUID = -1;
    
    public Message (String _code,int _player, String _message){
        code = _code;
        player = _player;
        message = _message;
    }
    
    public String getCode(){
        return code;
    }
    
    public int getPlayer(){
        return player;
    }
    
    public String getMessage(){
        return message;
    }
}
