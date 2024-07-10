package com.example.myapplication;

import java.io.Serializable;
import java.util.ArrayList;

public class ChessData implements Serializable {

    final ArrayList<String[][]> boardList;
    final ArrayList<String> textFieldOne;
    final ArrayList<String> textFieldTwo;
    String name;

    public ChessData(ArrayList<String[][]> board, ArrayList<String> textFieldOne, ArrayList<String> textFieldTwo){
        this.boardList = board;
        this.textFieldOne = textFieldOne;
        this.textFieldTwo = textFieldTwo;
    }

    public String[][] getBoard(int i){
        return boardList.get(i);
    }
    public String getTextFieldOne(int i){
        return textFieldOne.get(i);
    }
    public String getTextFieldTwo(int i){
        return textFieldTwo.get(i);
    }
    public void addBoard(String[][] board){
        boardList.add(board);
    }
    public void addTextOne(String text){
        textFieldOne.add(text);
    }
    public void addTextTwo(String text){
        textFieldTwo.add(text);
    }
    public void setName(String name){
        this.name = name;
    }
}
