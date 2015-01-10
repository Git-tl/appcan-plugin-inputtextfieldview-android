package org.zywx.wbpalmstar.plugin.uexinput;

import java.util.ArrayList;
import java.util.List;

public class Emotion {

	public static List<Emotion> getSystemEmotionList() {
		List<Emotion> list = new ArrayList<Emotion>();
		for (int i = 0; i < 36; i++) {
			Emotion emotion = new Emotion();
			emotion.resName = "plugin_input_emotion_" + i;
			emotion.code = "[dh_" + i + "]";
			list.add(emotion);
		}
		return list;
	}

	private String resName;
	private String code;

	public String getResName() {
		return resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
