package com.mltcode.android.stopwatch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewHolder {
    private View mConvertView;
    private SparseArray<View> mViews = new SparseArray<>();

    public ViewHolder(Context context, int i, ViewGroup viewGroup, int i2) {
        this.mConvertView = LayoutInflater.from(context).inflate(i, (ViewGroup) null);
        this.mConvertView.setTag(this);
    }

    public static ViewHolder get(Context context, View view, int i, ViewGroup viewGroup, int i2) {
        if (view == null) {
            return new ViewHolder(context, i, viewGroup, i2);
        }
        return (ViewHolder) view.getTag();
    }

    public <T extends View> View getView(int i) {
        View t = (View) this.mViews.get(i);
        if (t != null) {
            return t;
        }
        T findViewById = this.mConvertView.findViewById(i);
        this.mViews.put(i, findViewById);
        return findViewById;
    }

    public View getConvertView() {
        return this.mConvertView;
    }

    public TextView getTextView(int i) {
        return (TextView) getView(i);
    }

    public Button getButton(int i) {
        return (Button) getView(i);
    }

    public ImageView getImageView(int i) {
        return (ImageView) getView(i);
    }

    public CheckBox getCheckBox(int i) {
        return (CheckBox) getView(i);
    }

    public ViewHolder setTextView(int i, String str) {
        ((TextView) getView(i)).setText(str);
        return this;
    }

    public ViewHolder setTextView(int i, int i2) {
        ((TextView) getView(i)).setText(i2);
        return this;
    }

    public ViewHolder setButton(int i, String str) {
        ((Button) getView(i)).setText(str);
        return this;
    }

    public ViewHolder setButton(int i, int i2) {
        ((Button) getView(i)).setText(i2);
        return this;
    }

    public ViewHolder setImageView(int i, int i2) {
        ((ImageView) getView(i)).setImageResource(i2);
        return this;
    }

    public ViewHolder setImageView(int i, Drawable drawable) {
        ((ImageView) getView(i)).setImageDrawable(drawable);
        return this;
    }

    public ViewHolder setImageView(int i, Bitmap bitmap) {
        ((ImageView) getView(i)).setImageBitmap(bitmap);
        return this;
    }

    public ViewHolder setProgress(int i, int i2) {
        ((ProgressBar) getView(i)).setProgress(i2);
        return this;
    }
}
