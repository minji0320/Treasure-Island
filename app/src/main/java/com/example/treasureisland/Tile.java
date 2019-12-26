package com.example.treasureisland;

import android.graphics.*;

public class Tile {
    public int x, y;                // 위치
    public Bitmap image;           // 타일 이미지
    public boolean isMe;
    public boolean isFood;
    public boolean isAnimal;
    public boolean isTreasure;
    public boolean isEmpty;
    public boolean isRevealed;

    public Tile (Bitmap image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.isMe = false;
        this.isFood = false;
        this.isAnimal = false;
        this.isTreasure = false;
        this.isEmpty = true;
        this.isRevealed = false;
    }


    public void reveal() {
        this.isRevealed = true;
        this.isMe = true;
        this.isEmpty = false;

    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

}
