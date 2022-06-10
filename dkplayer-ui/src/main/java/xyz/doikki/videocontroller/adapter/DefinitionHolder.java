package xyz.doikki.videocontroller.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import xyz.doikki.videocontroller.component.RightControlView;
import xyz.doikki.videocontroller.eventbus.MessageEventPostion;
import xyz.doikki.videocontroller.videoinfo.M3U8Seg;
import xyz.doikki.videoplayer.R;


public class DefinitionHolder extends BaseViewHolder<M3U8Seg> {
    public DefinitionHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindViewData(final M3U8Seg module, final int position, Context context, MultiTypeAdapter adapter) {
        TextView textView = getView(R.id.definition_item);
        textView.setText(module.getResolution());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RightControlView.setSelectResolutionPosition(position);
                MessageEventPostion messageEventPostion = new MessageEventPostion();
                messageEventPostion.setFlag("Resolution");
                messageEventPostion.setmResolutionUrl(module.getUrl());
                EventBus.getDefault().post(messageEventPostion);
            }
        });

        if (module.isIschecked()) {
            textView.setSelected(true);
            RightControlView.setSelectSpeedPrePosition(position);//选中之前的位置
        } else {
            textView.setSelected(false);
        }
    }


}