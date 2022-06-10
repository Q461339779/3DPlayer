package xyz.doikki.videocontroller.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import xyz.doikki.videocontroller.component.RightControlView;
import xyz.doikki.videocontroller.eventbus.MessageEventPostion;
import xyz.doikki.videoplayer.R;


public class AnthologyHolder extends BaseViewHolder<AnthologyBean> {
    public AnthologyHolder(View itemView) {
        super(itemView);
    }


    @Override
    public void bindViewData(final AnthologyBean module, final int position, final Context context, MultiTypeAdapter adapter) {
        TextView textView = getView(R.id.anthology_item);
        textView.setText(module.getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RightControlView.setSelectPosition(position);
                MessageEventPostion messageEventPostion = new MessageEventPostion();
                messageEventPostion.setPostion(position);
                messageEventPostion.setFlag("Anthology");
                Log.i("mCurrentVideoPosition2",position+"");
                EventBus.getDefault().post(messageEventPostion);
            }
        });
        if (module.isIschecked()) {
            textView.setSelected(true);
            RightControlView.setSelectPrePosition(position);//选中之前的位置

        } else {
            textView.setSelected(false);
        }

    }
}