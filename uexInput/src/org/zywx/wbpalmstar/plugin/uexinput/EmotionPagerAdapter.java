package org.zywx.wbpalmstar.plugin.uexinput;

import java.util.List;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;

public class EmotionPagerAdapter extends PagerAdapter {

	private LayoutInflater inflater;
	private List<Emotion> list;
	private static final int PAGE_SIZE = 24;
	private OnEmotionClickedCallback callback;
	private LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

	public EmotionPagerAdapter(Context context, List<Emotion> emotions) {
		inflater = LayoutInflater.from(context);
		list = emotions;
	}

	public void setOnEmotionClickedCallback(OnEmotionClickedCallback callback) {
		this.callback = callback;
	}

	@Override
	public int getCount() {
		int totalSize = list.size();
		if (totalSize % PAGE_SIZE == 0) {
			return totalSize / PAGE_SIZE;
		} else {
			return totalSize / PAGE_SIZE + 1;
		}
	}

	private List<Emotion> getPageData(int postion) {
		int start = postion * PAGE_SIZE;
		int end = (postion + 1) * PAGE_SIZE;
		if (end > list.size()) {
			end = list.size();
		}
		return list.subList(start, end);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((GridView) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		GridView gridView = (GridView) inflater.inflate(EUExUtil.getResLayoutID("plugin_input_emotion_page_item"), null);
		final EmotionGridAdapter adapter = new EmotionGridAdapter(inflater.getContext(), getPageData(position));
		gridView.setAdapter(adapter);
		((ViewPager) container).addView(gridView, lp);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Emotion emotion = adapter.getItem(position);
				if (callback != null) {
					callback.OnEmotionClicked(emotion);
				}
			}
		});
		return gridView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		GridView gridView = (GridView) object;
		container.removeView(gridView);
	}

	public static interface OnEmotionClickedCallback {
		void OnEmotionClicked(Emotion emotion);
	}

}
