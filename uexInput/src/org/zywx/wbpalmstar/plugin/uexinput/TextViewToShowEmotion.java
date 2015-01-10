package org.zywx.wbpalmstar.plugin.uexinput;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewToShowEmotion extends TextView {

	public TextViewToShowEmotion(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextViewToShowEmotion(Context context) {
		super(context);
	}

	public TextViewToShowEmotion(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public static String getPicText(String picPath) {
		return "[@" + picPath + "@]";
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(InputUtility.formatSmiles(text, getContext()), type);
	}

}
