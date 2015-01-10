package org.zywx.wbpalmstar.plugin.uexinput;

import java.util.List;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class EmotionGridAdapter extends BaseAdapter {

	private List<Emotion> list;
	private LayoutInflater inflater;

	public EmotionGridAdapter(Context context, List<Emotion> emotions) {
		inflater = LayoutInflater.from(context);
		list = emotions;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Emotion getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(EUExUtil.getResLayoutID("plugin_input_emotion_grid_item"), null);
			holder.ivFace = (ImageView) convertView.findViewById(EUExUtil.getResIdID("plugin_input_emotion_item_face"));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Emotion emotion = getItem(position);
		holder.ivFace.setImageResource(EUExUtil.getResDrawableID(emotion.getResName()));
		return convertView;
	}

	private static class ViewHolder {
		public ImageView ivFace;
	}

}
