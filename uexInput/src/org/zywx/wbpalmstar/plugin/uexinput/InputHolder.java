package org.zywx.wbpalmstar.plugin.uexinput;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Parcel;
import android.os.Parcelable;

public class InputHolder implements Parcelable {
	public static final int INPUT_TYPE_NORMAL = 0;
	public static final int INPUT_TYPE_DIGITAL = 1;
	public static final int INPUT_TYPE_EMAIL = 2;
	public static final int INPUT_TYPE_URL = 3;
	public static final int INPUT_TYPE_PWD = 4;
	
	public static final String JK_BACKGROUND = "background";
	public static final String JK_INPUT_TYPE = "inputType";
	public static final String JK_ITEMS = "items";
	public static final String JK_TYPE = "type";
	public static final String JK_NAME = "name";
	public static final String JK_EMOTION = "emotion";
	public static final String JK_NORMAL = "normal";
	public static final String JK_PRESSED = "pressed";
	public static final String JK_DEFAULT = "default";
	public static final String JK_KEYBOARD = "keyboard";
	public static final String JK_DEF_TEXT = "defText";
	public static final String JK_DEF_HINT = "defHint";
	public static final String JK_BG = "bg";
	public static final String JK_AUTOFILL = "autofill";
	public static final String JK_LABEL = "label";

	public static final int JV_KEYBOARD_TYPE_STD = 0;
	public static final int JV_KEYBOARD_TYPE_NUMBER = 1;
	public static final int JV_KEYBOARD_TYPE_EMAIL = 2;
	public static final int JV_KEYBOARD_TYPE_URL = 3;
	public static final int JV_KEYBOARD_TYPE_PWD = 4;

	public static final int JV_ITEM_TYPE_EMOTION = 0;
	public static final int JV_ITEM_TYPE_INPUT = 1;
	public static final int JV_ITEM_TYPE_BUTTON = 2;
	public static final int JV_ITEM_TYPE_SEND_BUTTON = 3;

	private String bgUrl;
	private int inputType;
	private List<InputItem> inputItems = new ArrayList<InputHolder.InputItem>();;

	public static InputHolder parseInputJson(String msg) {
		InputHolder inputHolder = null;
		try {
			JSONObject json = new JSONObject(msg);
			inputHolder = new InputHolder();
			inputHolder.setBgUrl(json.getString(JK_BACKGROUND));
			inputHolder.setInputType(json.getInt(JK_INPUT_TYPE));
			JSONArray array = json.getJSONArray(JK_ITEMS);
			for (int i = 0, size = array.length(); i < size; i++) {
				JSONObject item = array.getJSONObject(i);
				int itemType = item.getInt(JK_TYPE);
				String name = item.optString(JK_NAME);
				boolean autofill = item.optBoolean(JK_AUTOFILL, false);
				switch (itemType) {
				case JV_ITEM_TYPE_EMOTION:
					InputSwitcher inputSwitcher = new InputSwitcher();
					inputSwitcher.setName(name);
					JSONObject emotionJson = item.getJSONObject(JK_EMOTION);
					boolean emotionDefault = emotionJson.optBoolean(JK_DEFAULT, false);
					inputSwitcher.setEmotionBgSelector(parseBgSelector(emotionJson));
					JSONObject keyboardJson = item.getJSONObject(JK_KEYBOARD);
					inputSwitcher.setKeyboardBgSelector(parseBgSelector(keyboardJson));
					emotionDefault = emotionJson.optBoolean(JK_DEFAULT, false) | emotionDefault;
					inputSwitcher.setEmotionDefault(emotionDefault);
					inputSwitcher.setAutoFill(autofill);
					//
					inputHolder.addInputItem(inputSwitcher);
					break;
				case JV_ITEM_TYPE_INPUT:
					InputText inputText = new InputText();
					inputText.setName(name);
					inputText.setDefText(item.optString(JK_DEF_TEXT));
					inputText.setDefHint(item.optString(JK_DEF_HINT));
					inputText.setBgSelector(parseBgSelector(item.optJSONObject(JK_BG)));
					inputText.setAutoFill(autofill);
					//
					inputHolder.addInputItem(inputText);
					break;
				case JV_ITEM_TYPE_BUTTON:
					InputButton inputButton = new InputButton();
					inputButton.setName(name);
					inputButton.setLabel(item.optString(JK_LABEL));
					inputButton.setBgSelector(parseBgSelector(item.optJSONObject(JK_BG)));
					//
					inputHolder.addInputItem(inputButton);
					break;
				case JV_ITEM_TYPE_SEND_BUTTON:
					InputSendButton inputSendButton = new InputSendButton();
					inputSendButton.setName(name);
					inputSendButton.setLabel(item.optString(JK_LABEL));
					inputSendButton.setBgSelector(parseBgSelector(item.optJSONObject(JK_BG)));
					//
					inputHolder.addInputItem(inputSendButton);
					break;
				}
			}// end for
		} catch (JSONException e) {
			e.printStackTrace();
			inputHolder = null;
		}
		return inputHolder;
	}

	/**
	 * 解析背景色
	 * 
	 * @param json
	 * @return
	 */
	public static BgSelector parseBgSelector(JSONObject json) {
		if (json == null) {
			return null;
		}
		BgSelector bgSelector = new BgSelector();
		try {
			bgSelector.normalBgUrl = json.getString(JK_NORMAL);
			bgSelector.pressedBgUrl = json.getString(JK_PRESSED);
		} catch (JSONException e) {
			e.printStackTrace();
			bgSelector = null;
		}
		return bgSelector;
	}

	public InputHolder() {

	}

	public void addInputItem(InputItem inputItem) {
		inputItems.add(inputItem);
	}

	public List<InputItem> getInputList() {
		return inputItems;
	}

	public String getBgUrl() {
		return bgUrl;
	}

	public void setBgUrl(String bgUrl) {
		this.bgUrl = bgUrl;
	}

	public int getInputType() {
		return inputType;
	}

	public void setInputType(int inputType) {
		this.inputType = inputType;
	}

	public static class InputItem implements Parcelable {
		private String name;
		private boolean autoFill;

		public InputItem() {

		}

		public InputItem(Parcel source) {
			this.name = source.readString();
			this.autoFill = source.readInt() == 1 ? true : false;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isAutoFill() {
			return autoFill;
		}

		public void setAutoFill(boolean autoFill) {
			this.autoFill = autoFill;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(name);
			dest.writeInt(autoFill ? 1 : 0);
		}

		public static final Parcelable.Creator<InputItem> CREATOR = new Creator<InputItem>() {

			@Override
			public InputItem[] newArray(int size) {
				return new InputItem[size];
			}

			@Override
			public InputItem createFromParcel(Parcel source) {
				return new InputItem(source);
			}
		};

	}

	public static class InputButton extends InputItem implements Parcelable {
		private String label;
		private BgSelector bgSelector;

		public InputButton() {

		}

		public InputButton(Parcel source) {
			this.label = source.readString();
			this.bgSelector = new BgSelector(source);
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public BgSelector getBgSelector() {
			return bgSelector;
		}

		public void setBgSelector(BgSelector bgSelector) {
			this.bgSelector = bgSelector;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(label);
			this.bgSelector.writeToParcel(dest, flags);
		}

		public static final Parcelable.Creator<InputButton> CREATOR = new Creator<InputButton>() {

			@Override
			public InputButton[] newArray(int size) {
				return new InputButton[size];
			}

			@Override
			public InputButton createFromParcel(Parcel source) {
				return new InputButton(source);
			}
		};
	}

	public static class InputSendButton extends InputButton {

	}

	public static class InputText extends InputItem implements Parcelable {
		private String defText;
		private String defHint;
		private BgSelector bgSelector;

		public InputText() {
		}

		public InputText(Parcel source) {
			this.defText = source.readString();
			this.defHint = source.readString();
			this.bgSelector = new BgSelector(source);
		}

		public String getDefText() {
			return defText;
		}

		public void setDefText(String defText) {
			this.defText = defText;
		}

		public String getDefHint() {
			return defHint;
		}

		public void setDefHint(String defHint) {
			this.defHint = defHint;
		}

		public BgSelector getBgSelector() {
			return bgSelector;
		}

		public void setBgSelector(BgSelector bgSelector) {
			this.bgSelector = bgSelector;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeString(this.defText);
			dest.writeString(this.defHint);
			this.bgSelector.writeToParcel(dest, flags);
		}

		public static final Parcelable.Creator<InputText> CREATOR = new Creator<InputText>() {

			@Override
			public InputText[] newArray(int size) {
				return new InputText[size];
			}

			@Override
			public InputText createFromParcel(Parcel source) {
				return new InputText(source);
			}
		};
	}

	public static class InputSwitcher extends InputItem implements Parcelable {
		private boolean emotionDefault;
		private BgSelector emotionBgSelector;
		private BgSelector keyboardBgSelector;

		public InputSwitcher() {

		}

		public InputSwitcher(Parcel source) {
			this.emotionDefault = source.readInt() == 1 ? true : false;
			this.emotionBgSelector = new BgSelector(source);
			this.keyboardBgSelector = new BgSelector(source);
		}

		public boolean isEmotionDefault() {
			return emotionDefault;
		}

		public void setEmotionDefault(boolean emotionDefault) {
			this.emotionDefault = emotionDefault;
		}

		public BgSelector getEmotionBgSelector() {
			return emotionBgSelector;
		}

		public void setEmotionBgSelector(BgSelector emotionBgSelector) {
			this.emotionBgSelector = emotionBgSelector;
		}

		public BgSelector getKeyboardBgSelector() {
			return keyboardBgSelector;
		}

		public void setKeyboardBgSelector(BgSelector keyboardBgSelector) {
			this.keyboardBgSelector = keyboardBgSelector;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(emotionDefault ? 1 : 0);
			emotionBgSelector.writeToParcel(dest, flags);
			keyboardBgSelector.writeToParcel(dest, flags);
		}

		public static final Parcelable.Creator<InputSwitcher> CREATOR = new Creator<InputSwitcher>() {

			@Override
			public InputSwitcher[] newArray(int size) {
				return new InputSwitcher[size];
			}

			@Override
			public InputSwitcher createFromParcel(Parcel source) {
				return new InputSwitcher(source);
			}
		};
	}

	public static class BgSelector implements Parcelable {
		public String normalBgUrl;
		public String pressedBgUrl;

		public BgSelector() {

		}

		public BgSelector(Parcel source) {
			this.normalBgUrl = source.readString();
			this.pressedBgUrl = source.readString();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(normalBgUrl);
			dest.writeString(pressedBgUrl);
		}

		public static final Parcelable.Creator<BgSelector> CREATOR = new Creator<InputHolder.BgSelector>() {

			@Override
			public BgSelector[] newArray(int size) {
				return new BgSelector[size];
			}

			@Override
			public BgSelector createFromParcel(Parcel source) {
				return new BgSelector(source);
			}
		};
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(bgUrl);
		dest.writeInt(inputType);
		int size = inputItems.size();
		dest.writeInt(size);
		for (int i = 0; i < size; i++) {
			inputItems.get(i).writeToParcel(dest, flags);
		}
	}

	public static final Parcelable.Creator<InputHolder> CREATOR = new Creator<InputHolder>() {

		@Override
		public InputHolder[] newArray(int size) {
			return new InputHolder[size];
		}

		@Override
		public InputHolder createFromParcel(Parcel source) {
			return new InputHolder(source);
		}
	};

	public InputHolder(Parcel source) {
		this.bgUrl = source.readString();
		this.inputType = source.readInt();
		int size = source.readInt();
		for (int i = 0; i < size; i++) {
			inputItems.add(new InputItem(source));
		}
	}

}
