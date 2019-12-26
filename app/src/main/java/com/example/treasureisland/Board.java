package com.example.treasureisland;

import android.graphics.*;

import java.util.Random;

public class Board {
    public Tile[][] grid;
    public GameView gameView;
    private int boardSize;
    private int foodCount;
    private int animalCount;
    public int meCol, meRow;    // Me의 현재 위치
    public int trCol, trRow;    // Treasure의 위치
    private int RevealedCount;
    private int heartCount;
    public int distance;
    public int hintCount;
    Bitmap animal;
    Bitmap food;
    Bitmap map;
    Bitmap map2;
    Bitmap me;
    Bitmap treasure;
    Bitmap heart;
    Bitmap hint;
    Bitmap hint2;

    public Board(GameView gameView, int boardSize, int foodCount, int animalCount) {
        this.gameView = gameView;
        this.grid = new Tile[boardSize][boardSize];
        this.boardSize = boardSize;
        this.foodCount = foodCount;
        this.animalCount = animalCount;
        this.heartCount = 8;
        this.hintCount = 3;
        this.animal = BitmapFactory.decodeResource(this.gameView.mContext.getResources(), R.drawable.animal);
        this.food = BitmapFactory.decodeResource(this.gameView.mContext.getResources(), R.drawable.food);
        this.map = BitmapFactory.decodeResource(this.gameView.mContext.getResources(), R.drawable.map);
        this.map2 = BitmapFactory.decodeResource(this.gameView.mContext.getResources(), R.drawable.map2);
        this.me = BitmapFactory.decodeResource(this.gameView.mContext.getResources(), R.drawable.me);
        this.treasure = BitmapFactory.decodeResource(this.gameView.mContext.getResources(), R.drawable.treasure);
        this.heart = BitmapFactory.decodeResource(this.gameView.mContext.getResources(), R.drawable.heart);
        this.hint = BitmapFactory.decodeResource(this.gameView.mContext.getResources(), R.drawable.hint);
        this.hint2 = BitmapFactory.decodeResource(this.gameView.mContext.getResources(), R.drawable.hint2);
    }

    public void set() {
        this.setPositions();
        this.shuffleItems(this.foodCount, this.animalCount);
        this.setDistance();
    }

    public void setPositions() {
        int horizontalOffset = (1080-800) / 2;  // 140
        for(int i = 0; i < boardSize; i++) {
            for(int j = 0; j < boardSize; j++) {
                Bitmap img = Bitmap.createBitmap(map, i * 100,j * 100,100,100);
                this.grid[i][j] = new Tile(img, (horizontalOffset + i * 100), (200 + j * 100));
            }
        }
    }

    public void shuffleItems(int foodCount, int animalCount) {
        Random random = new Random();
        int column;
        int row;
        for(int i = 0; i < foodCount; i++) {    //  Food 위치 선정
            do {
                column = random.nextInt(boardSize);
                row = random.nextInt(boardSize);
            } while (!this.grid[column][row].isEmpty);
            this.grid[column][row].isFood = true;
            this.grid[column][row].isEmpty = false;
        }
        for(int i = 0; i < animalCount; i++) {  // Animal 위치 선정
            do {
                column = random.nextInt(boardSize);
                row = random.nextInt(boardSize);
            } while (!this.grid[column][row].isEmpty);
            this.grid[column][row].isAnimal = true;
            this.grid[column][row].isEmpty = false;
        }
        do {    // Treasure 위치 선정
            column = random.nextInt(boardSize);
            row = random.nextInt(boardSize);
        } while (!this.grid[column][row].isEmpty);
        this.grid[column][row].isTreasure = true;
        this.grid[column][row].isEmpty = false;
        this.trCol = column;
        this.trRow = row;
        do {    // Me 위치 선정
            column = random.nextInt(boardSize);
            row = random.nextInt(boardSize);
        } while (!this.grid[column][row].isEmpty);
        this.grid[column][row].isMe = true;
        this.grid[column][row].isRevealed = true;
        this.grid[column][row].isEmpty = false;
        this.grid[column][row].setImage(me);
        this.meCol = column;
        this.meRow = row;
    }

    private void setDistance() {
        this.distance = Math.abs(trCol-meCol) + Math.abs(trRow-meRow);
    }


    public void reveal(int revCol, int revRow) {
        grid[meCol][meRow].isMe = false;  // 원래 있던 Me의 위치 삭제
        Bitmap img = Bitmap.createBitmap(map2, meCol * 100,meRow * 100,100,100);
        grid[meCol][meRow].setImage(img);
        grid[revCol][revRow].reveal();
        meCol = revCol;
        meRow = revRow;
        grid[meCol][meRow].setImage(me);
        setDistance();
        this.RevealedCount++;
        if(grid[meCol][meRow].isFood){
            this.heartCount += 1;
            if(this.heartCount >= 8) {
                this.heartCount = 8;
            }
        } else if (grid[meCol][meRow].isAnimal){
            this.heartCount -= 2;
            if(heartCount <= 0)
                gameView.isGameOver = true;
        }
        if(grid[meCol][meRow].isTreasure)
            gameView.isGameOver = true;
    }

    public int getRevealedCount() {
        return this.RevealedCount;
    }

    public int getHeart() {
        return this.heartCount;
    }
}

