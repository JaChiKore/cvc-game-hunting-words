package edu.uab.cvc.huntingwords.screens.games;

import android.graphics.RectF;


@SuppressWarnings("WeakerAccess")
public class Button {
    private RectF rect;

    private String text;

    private boolean isPressed = false;

    public Button(float pos_x, float pos_y, float wid, float hei, String text) {

        this.rect = new RectF(pos_x, pos_y, pos_x + wid, pos_y + hei);

        this.text = text;
    }

    public RectF getRect() {
        return rect;
    }

    public String getText() {
        return text;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void inverseIsPressed() {
        isPressed = !isPressed;
    }

    public void putText(String t) {
        if(!(this.text.length() > 20 /*MAX_LENGTH*/)) {
            this.text = this.text + t;
        }
    }

    public void deleteText() {
        if(this.text.length() > 0) {
            this.text = this.text.substring(0, this.text.length() - 1);
        }
    }

    public void setText(String t) {
        this.text = t;
    }

    public void resetText() {
        this.text = "";
    }
}
