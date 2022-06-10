package xyz.doikki.videocontroller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public abstract class BaseViewHolder<T extends Vistable> extends RecyclerView.ViewHolder {
    private SparseArray sparseArray;
    private View mItemView;

    public BaseViewHolder(View itemView) {
        super(itemView);
        sparseArray = new SparseArray<>();
        mItemView = itemView;
    }

    public <T extends View> T getView(int resId) {
        View view = (View) sparseArray.get(resId);
        if (view == null) {
            view = mItemView.findViewById(resId);
            sparseArray.put(resId, view);
        }
        return (T) view;
    }



    public abstract void bindViewData( T data, int position, Context context, MultiTypeAdapter adapter);

}
