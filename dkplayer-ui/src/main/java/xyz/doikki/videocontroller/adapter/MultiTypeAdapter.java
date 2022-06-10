package xyz.doikki.videocontroller.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xyz.doikki.videoplayer.R;

public class MultiTypeAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<Vistable> modules;
    private TypeFactory factory;
    protected Context mContext;
    public MultiTypeAdapter(List<Vistable> modules, Context context){
        mContext = context;
        this.modules = modules;
        factory = new TypeFactoryList();
    }
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return factory.createViewHolder(viewType, view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.bindViewData(modules.get(position),position,mContext,this);

    }

    @Override
    public int getItemCount() {
        return modules == null? 0:modules.size();
    }

    @Override
    public int getItemViewType(int position) {
        return modules.get(position).type(factory);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        //setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false));
        if (manager == null || !(manager instanceof GridLayoutManager)) return;
        ((GridLayoutManager) manager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (getItemViewType(position)== R.layout.dkplayer_layout_anthology_item){
                    return 2;
                }else if (getItemViewType(position) == R.layout.dkplayer_layout_speed_item){
                    return 2;
                }else
                    return 1;
            }
        });
    }




}