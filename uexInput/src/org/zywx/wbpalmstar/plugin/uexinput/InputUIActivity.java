package org.zywx.wbpalmstar.plugin.uexinput;

import java.util.List;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexinput.InputHolder.BgSelector;
import org.zywx.wbpalmstar.plugin.uexinput.InputHolder.InputButton;
import org.zywx.wbpalmstar.plugin.uexinput.InputHolder.InputItem;
import org.zywx.wbpalmstar.plugin.uexinput.InputHolder.InputSendButton;
import org.zywx.wbpalmstar.plugin.uexinput.InputHolder.InputSwitcher;
import org.zywx.wbpalmstar.plugin.uexinput.InputHolder.InputText;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class InputUIActivity extends Activity {

	public static final String INTENT_KEY_INPUT = "input";

	private ViewPager viewPager;
	private PageIndicator pageIndicator;
	private ViewGroup totalLayout;
	private LinearLayout controllerLayout;
	private InputHolder inputHolder;
	private ViewGroup emotionLayout;
	private EditText editText;
	private OnSendCallback sendCallback;
	private InputMethodManager inputMethodManager;
	private Handler handler = new Handler();
	private TranslateAnimation showAnimation = null;
	private TranslateAnimation hideAnimation = null;
	private StateListDrawable emotionDrawable;
	private StateListDrawable keyboardDrawable;
	private ImageButton switchBtn;

	public void init(InputHolder inputHolder) {
		this.inputHolder = inputHolder;
		controllerLayout.removeAllViews();
		inflaterView(inputHolder);
	}

	@Override
	protected void onResume() {
		EUExInput.mylog("InputUIActivity--onResume");
		super.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		EUExInput.mylog("InputUIActivity--onNewIntent");
		super.onNewIntent(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inputHolder = getIntent().getParcelableExtra(INTENT_KEY_INPUT);
		setContentView(EUExUtil.getResLayoutID("plugin_input_main_layout"));
		totalLayout = (ViewGroup) findViewById(EUExUtil
				.getResIdID("plugin_input_total_layout"));
		controllerLayout = (LinearLayout) findViewById(EUExUtil
				.getResIdID("plugin_input_controllers_layout"));
		emotionLayout = (ViewGroup) findViewById(EUExUtil
				.getResIdID("plugin_input_emotion_layout"));
		viewPager = (ViewPager) findViewById(EUExUtil
				.getResIdID("plugin_input_emotion_pages"));
		pageIndicator = (PageIndicator) findViewById(EUExUtil
				.getResIdID("plugin_input_emotion_page_indictor"));
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		showAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
				0.0f);
		showAnimation.setInterpolator(new DecelerateInterpolator());
		showAnimation.setDuration(200);
		hideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f);
		hideAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		hideAnimation.setDuration(200);
		setData();
		inflaterView(inputHolder);
	}

	public void setSendCallback(OnSendCallback sendCallback) {
		this.sendCallback = sendCallback;
	}

	private void setData() {
		EmotionPagerAdapter pagerAdapter = new EmotionPagerAdapter(this,
				Emotion.getSystemEmotionList());
		viewPager.setAdapter(pagerAdapter);
		pageIndicator.setTotalPageSize(pagerAdapter.getCount());
		pageIndicator.setCurrentPage(viewPager.getCurrentItem());
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int index) {
				pageIndicator.setCurrentPage(index);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		pagerAdapter
				.setOnEmotionClickedCallback(new EmotionPagerAdapter.OnEmotionClickedCallback() {

					@Override
					public void OnEmotionClicked(Emotion emotion) {
						if (editText != null) {
							InputUtility.addEmotion(InputUIActivity.this,
									editText, emotion.getCode());
						}
					}
				});
	}

	private void inflaterView(InputHolder holder) {
		controllerLayout.setGravity(Gravity.CENTER_VERTICAL);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int margin = (int) (4 * dm.density);
		Drawable bgDrawable = new BitmapDrawable(getResources(),
				InputUtility.getImage(this, holder.getBgUrl()));
		controllerLayout.setBackgroundDrawable(bgDrawable);
		List<InputItem> list = holder.getInputList();
		for (InputItem inputItem : list) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			params.setMargins(margin, margin, margin, margin);
			if (inputItem.isAutoFill()) {
				params.weight = 1;
			}
			if (inputItem instanceof InputSwitcher) {
				final InputSwitcher inputSwitcher = (InputSwitcher) inputItem;
				switchBtn = new ImageButton(this);
				switchBtn.setTag(inputSwitcher.getName());
				controllerLayout.addView(switchBtn, params);
				emotionDrawable = loadBgDrawable(inputSwitcher
						.getEmotionBgSelector());
				keyboardDrawable = loadBgDrawable(inputSwitcher
						.getKeyboardBgSelector());
				switchBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						boolean isVisiblity = isEmotionLayoutVisibility();
						if (isVisiblity) {
							toggleEmotionLayout(false);
						} else {
							toggleEmotionLayout(true);
						}
					}
				});
				toggleEmotionLayout(inputSwitcher.isEmotionDefault());
			} else if (inputItem instanceof InputText) {
				InputText inputText = (InputText) inputItem;
				editText = new EditText(this);
				setInputType(editText, inputHolder.getInputType());
				editText.setSingleLine(false);
				editText.setMaxLines(3);
				editText.setTag(inputText.getName());
				editText.setHint(inputText.getDefHint());
				editText.setText(inputText.getDefText());
				controllerLayout.addView(editText, params);
				StateListDrawable editTextDrawable = loadBgDrawable(inputText
						.getBgSelector());
				if (editTextDrawable != null) {
					editText.setBackgroundDrawable(editTextDrawable);
				}
				editText.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// 点击输入框时,隐藏表情布局
						boolean isHandle = false;
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							if (emotionLayout.getVisibility() == View.VISIBLE) {
								toggleEmotionLayout(false);
								isHandle = true;
							}
						}
						return isHandle;
					}
				});
			} else if (inputItem instanceof InputSendButton) {
				InputSendButton inputSendButton = (InputSendButton) inputItem;
				final Button sendBtn = new Button(this);
				sendBtn.setTag(inputSendButton.getName());
				sendBtn.setText(inputSendButton.getLabel());
				controllerLayout.addView(sendBtn, params);
				StateListDrawable sendBtnDrawable = loadBgDrawable(inputSendButton
						.getBgSelector());
				if (sendBtnDrawable != null) {
					sendBtn.setBackgroundDrawable(sendBtnDrawable);
				}
				sendBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								if (sendCallback != null) {
									sendCallback.onPerformSendAction(sendBtn,
											getInputText());
								}
								clearInputText();
								if (isEmotionLayoutVisibility()) {
									hideEmotionLayout();
								}
								if (editText != null) {
									inputMethodManager.hideSoftInputFromWindow(
											editText.getWindowToken(), 0);
								}
							}
						});
					}// end onClick() method
				});
			} else if (inputItem instanceof InputButton) {
				InputButton inputButton = (InputButton) inputItem;
				final Button button = new Button(this);
				button.setTag(inputButton.getName());
				button.setText(inputButton.getLabel());
				controllerLayout.addView(button, params);
				StateListDrawable buttonDrawable = loadBgDrawable(inputButton
						.getBgSelector());
				if (buttonDrawable != null) {
					button.setBackgroundDrawable(buttonDrawable);
				}
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								if (sendCallback != null) {
									sendCallback.onPerformSendAction(button,
											getInputText());
								}
							}
						});
					}
				});
			}
		}// end for
	}

	public void clearInputText() {
		if (editText != null) {
			editText.setText("");
		}
	}

	public String getInputText() {
		if (editText != null) {
			return editText.getText().toString();
		}
		return "";
	}

	/**
	 * 
	 * @param show
	 *            是否显示表情布局
	 * @param keyboard
	 *            是否显示键盘
	 */
	public void toggleEmotionLayout(boolean show) {
		updateSwitchBtnBg(show);
		if (show) {
			if (editText != null) {
				inputMethodManager.hideSoftInputFromWindow(
						editText.getWindowToken(), 0);
			}
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					emotionLayout.setVisibility(View.VISIBLE);
					totalLayout.startAnimation(showAnimation);
				}
			}, 300);
		} else {
			controllerLayout.startAnimation(hideAnimation);
			emotionLayout.startAnimation(hideAnimation);
			hideAnimation
					.setAnimationListener(new Animation.AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {

						}

						@Override
						public void onAnimationRepeat(Animation animation) {

						}

						@Override
						public void onAnimationEnd(Animation animation) {
							emotionLayout.setVisibility(View.GONE);

							if (editText != null) {
								editText.requestFocus();
								inputMethodManager.showSoftInput(editText, 0);
							}
						}
					});
		}
	}

	public void hideEmotionLayout() {
		controllerLayout.startAnimation(hideAnimation);
		emotionLayout.startAnimation(hideAnimation);
		hideAnimation.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				emotionLayout.setVisibility(View.GONE);
				updateSwitchBtnBg(false);
			}
		});
	}

	public void updateSwitchBtnBg(boolean show) {
		if (switchBtn == null) {
			return;
		}
		if (show) {
			switchBtn.setBackgroundDrawable(keyboardDrawable);
		} else {
			switchBtn.setBackgroundDrawable(emotionDrawable);
		}
	}

	public boolean isEmotionLayoutVisibility() {
		return emotionLayout.getVisibility() == View.VISIBLE ? true : false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (emotionLayout.getVisibility() == View.VISIBLE) {
				// emotionLayout.setVisibility(View.GONE);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (emotionLayout.getVisibility() == View.VISIBLE) {
				emotionLayout.setVisibility(View.GONE);
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	public StateListDrawable loadBgDrawable(BgSelector bgSelector) {
		if (bgSelector == null) {
			return null;
		}
		StateListDrawable stateListDrawable = new StateListDrawable();
		Drawable normalDrawable = NinePatchUtils.wrapBitmapToDrawable(
				getResources(),
				InputUtility.getImage(this, bgSelector.normalBgUrl));
		Drawable pressedDrawable = NinePatchUtils.wrapBitmapToDrawable(
				getResources(),
				InputUtility.getImage(this, bgSelector.pressedBgUrl));
		stateListDrawable.addState(new int[] { android.R.attr.state_pressed },
				pressedDrawable);
		stateListDrawable.addState(new int[] { android.R.attr.state_focused },
				pressedDrawable);
		stateListDrawable.addState(new int[] { android.R.attr.state_selected },
				pressedDrawable);
		stateListDrawable.addState(new int[] {}, normalDrawable);
		return stateListDrawable;
	}

	/**
	 * 设置输入框输入类型
	 * 
	 * @param inputType
	 */
	public void setInputType(EditText editText, int inputType) {
		switch (inputType) {
		case InputHolder.INPUT_TYPE_NORMAL:
			editText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
			break;
		case InputHolder.INPUT_TYPE_DIGITAL:
			editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
			break;
		case InputHolder.INPUT_TYPE_EMAIL:
			editText.setInputType(EditorInfo.TYPE_CLASS_TEXT
					| EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			break;
		case InputHolder.INPUT_TYPE_URL:
			editText.setInputType(EditorInfo.TYPE_CLASS_TEXT
					| EditorInfo.TYPE_TEXT_VARIATION_URI);
			break;
		case InputHolder.INPUT_TYPE_PWD:
			editText.setInputType(EditorInfo.TYPE_CLASS_TEXT
					| EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
			break;
		}
	}

}
