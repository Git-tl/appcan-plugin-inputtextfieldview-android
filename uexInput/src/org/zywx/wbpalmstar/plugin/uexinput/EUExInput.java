package org.zywx.wbpalmstar.plugin.uexinput;

import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;

/**
 * 带表情输入功能的输入控件
 * 
 * @author zhenyu.fang
 * 
 */
public class EUExInput extends EUExBase {

	public static final String TAG = "EUExInput";
	private InputUIActivity mInputContext;
	public static final String ON_FUNCTION_BUTTON_CLICK = "uexInput.onButtonClick";
	public static final String CALLBACK_CREATE = "uexInput.cbCreate";
	private View decorView;

	public EUExInput(Context context, EBrowserView inParent) {
		super(context, inParent);
	}

	public static void mylog(String string) {
		Log.e("luke", string);
	}

	/**
	 * 创建输入控件<br>
	 * 实际形式:create(String json)
	 * 
	 * @param params
	 */
	public void create(String[] params) {
		if (params.length != 1) {
			return;
		}
		mylog("create:" + params[0]);
		final InputHolder inputHolder = InputHolder.parseInputJson(params[0]);
		if (inputHolder == null) {
			errorCallback(0, 0, "参数错误");
			return;
		}
		((Activity) mContext).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mInputContext == null) {
					mylog("mInputContext==null");
					Intent intent = new Intent(mContext, InputUIActivity.class);
					LocalActivityManager localActivityManager = ((ActivityGroup) mContext)
							.getLocalActivityManager();
					intent.putExtra(InputUIActivity.INTENT_KEY_INPUT,
							inputHolder);
					Window window = localActivityManager.startActivity(TAG,
							intent);
					mInputContext = (InputUIActivity) window.getContext();
					mInputContext.init(inputHolder);
					decorView = window.getDecorView();
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
					lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					addViewToCurrentWindow(decorView, lp);
					mInputContext.setSendCallback(new OnSendCallback() {

						@Override
						public void onPerformSendAction(View clickedView,
								String text) {
							Object tag = clickedView.getTag();
							String tagedName = (tag != null ? tag.toString()
									: "");
							String js = SCRIPT_HEADER + "if("
									+ ON_FUNCTION_BUTTON_CLICK + "){"
									+ ON_FUNCTION_BUTTON_CLICK + "('"
									+ tagedName + "','" + text + "');}";
							onCallback(js);
						}
					});
					jsCallback(CALLBACK_CREATE, 0, EUExCallback.F_C_INT,
							EUExCallback.F_C_SUCCESS);
				} else {

					mInputContext.init(inputHolder);
					decorView = mInputContext.getWindow().getDecorView();
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
					lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					addViewToCurrentWindow(decorView, lp);
				}
			}
		});
	}

	/**
	 * 清除输入框文本<br>
	 * 实际形式:clearText();
	 * 
	 * @param params
	 */
	public void clearText(String[] params) {
		mylog("clearText:");
		((Activity) mContext).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mInputContext != null) {
					mInputContext.clearInputText();
				}
			}
		});
	}

	/**
	 * 关闭输入控件<br>
	 * 实际形式:close();
	 * 
	 * @param params
	 */
	public void close(String[] params) {
		mylog("close5161836:");
		((Activity) mContext).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mInputContext != null) {
					mInputContext.clearInputText();
					removeViewFromCurrentWindow(decorView);
					decorView = null;
				}
			}
		});
	}

	@Override
	protected boolean clean() {
		close(null);
		return true;
	}

}
