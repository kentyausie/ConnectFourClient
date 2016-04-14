package com.example.kent.connectfour;

import android.widget.TextView;

/**
 * Created by kent on 15-12-10.
 */
public class TextViewHolder {

    TextView view;
    int row = 0;
    int column = 0;
    Boolean isClicked = false;

    public TextViewHolder(TextView v, int r, int c){
        view = v;
        row = r;
        column = c;
    }

    public TextView getView (){
        return view;
    }

    public int getRow(){
        return row;
    }

    public int getColumn(){
        return column;
    }

    public Boolean isSquareClicked(){
        if(!isClicked){
            isClicked = true;
            return false;
        }
        return true;
    }


}
