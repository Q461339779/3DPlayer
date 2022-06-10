package xyz.doikki.videocontroller.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import xyz.doikki.videocontroller.component.RightControlView;
import xyz.doikki.videocontroller.eventbus.MessageEventPostion;
import xyz.doikki.videoplayer.R;


public class SpeedHolder extends BaseViewHolder<SpeedBean> {
    public SpeedHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindViewData(final SpeedBean module, final int position, final Context context, MultiTypeAdapter adapter) {
        TextView textView = getView(R.id.speed_item);
        textView.setText(module.name);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RightControlView.setSelectSpeedPosition(position);
                MessageEventPostion messageEventPostion = new MessageEventPostion();
                messageEventPostion.setFlag("Speed");
                messageEventPostion.setPostion(position);
                messageEventPostion.setSpeed(module.num);

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