package com.mltcode.android.stopwatch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {
    private Context context;
    private int layoutId;
    private List<T> list;

    public abstract void convert(ViewHolder viewHolder, T t);

    public long getItemId(int i) {
        return (long) i;
    }

    public CommonAdapter(Context context2, List<T> list2, int i) {
        this.context = context2;
        this.list = list2;
        this.layoutId = i;
    }

    public int getCount() {
        if (this.list != null) {
            return this.list.size();
        }
        return 0;
    }

    public T getItem(int i) {
        if (this.list != null) {
            return this.list.get(i);
        }
        return null;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = ViewHolder.get(this.context, view, this.layoutId, viewGroup, i);
        convert(viewHolder, getItem(i));
        return viewHolder.getConvertView();
    }
}
