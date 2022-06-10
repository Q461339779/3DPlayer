package xyz.doikki.videocontroller.adapter;

import android.view.View;

import xyz.doikki.videocontroller.videoinfo.M3U8Seg;
import xyz.doikki.videoplayer.R;


public class TypeFactoryList implements TypeFactory {
    public static final int ANTHOLOGY_LAYOUT = R.layout.dkplayer_layout_anthology_item;
    public static final int DEFINITION_LAYOUT = R.layout.dkplayer_layout_definition_item;
    public static final int SPEED_LAYOUT = R.layout.dkplayer_layout_speed_item;
    @Override
    public int type(AnthologyBean androidData) {
        return ANTHOLOGY_LAYOUT;
    }

    @Override
    public int type(M3U8Seg m3U8Seg) {
        return DEFINITION_LAYOUT;
    }

    @Override
    public int type(SpeedBean speedBean) {
        return SPEED_LAYOUT;
    }

    @Override
    public BaseViewHolder createViewHolder(int type, View itemView) {
        if (type == ANTHOLOGY_LAYOUT){
            return new AnthologyHolder(itemView);
        }
        else if (type == DEFINITION_LAYOUT){
            return new DefinitionHolder(itemView);
        }
        else if (type == SPEED_LAYOUT){
            return new SpeedHolder(itemView);
        }else {
            return null;
        }

    }
}