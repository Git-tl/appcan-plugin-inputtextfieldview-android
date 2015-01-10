package org.zywx.wbpalmstar.plugin.uexinput;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;

public class InputUtility {

	public static final String TAG = "InputUtility";

	public static void addEmotion(Context context, EditText editText, String emotionCode) {
		int index = editText.getSelectionStart();// 获取光标所在位置
		editText.getEditableText().insert(index, emotionCode);// 在光标所在位置插入表情代号
		index = editText.getSelectionStart();// 获取光标所在位置
		editText.setText(InputUtility.formatSmiles(editText.getText(), context));
		editText.setSelection(index);
	}

	public static SpannableString formatSmiles(CharSequence text, Context context) {
		SpannableString spannableString = new SpannableString(text);
		if (spannableString.length() == 0) {
			return spannableString;
		}
		Pattern pattern = Pattern.compile("(\\[dh_[0-9]{1,2}\\])"); // 正则匹配
		Matcher matcher = pattern.matcher(spannableString);
		try {
			while (matcher.find()) {
				String key = matcher.group(1);
				key = key.substring(4, key.length() - 1);// [dh_13]
				int id = EUExUtil.getResDrawableID("plugin_input_emotion_" + key);
				spannableString.setSpan(new ImageSpan(context, id), matcher.start(), matcher.end(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return spannableString;
	}

	public static Bitmap loadBitmapByMaxSize(String filePath, int maxSize) {
		if (filePath == null || filePath.length() == 0) {
			return null;
		}
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap source = BitmapFactory.decodeFile(filePath, options);
		final int srcHeight = options.outHeight;
		final int srcWidth = options.outWidth;
		if (srcHeight <= 0 || srcWidth <= 0) {
			return null;
		}
		float scaleRate = 1;
		if (srcHeight > srcWidth) {
			scaleRate = srcHeight / maxSize;
		} else {
			scaleRate = srcWidth / maxSize;
		}
		scaleRate = scaleRate > 1 ? scaleRate : 1;
		options.inJustDecodeBounds = false;
		options.inSampleSize = (int) scaleRate;
		options.inDither = false;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(filePath));
			source = BitmapFactory.decodeFileDescriptor(fis.getFD(), null, options);
			if (source != null) {
				final int width = source.getWidth();
				final int height = source.getHeight();
				if (width > height) {
					source = Bitmap.createBitmap(source, (width - height) / 2, 0, height, height);
				} else {
					source = Bitmap.createBitmap(source, 0, (height - width) / 2, width, width);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "load :" + filePath + e.getMessage());
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Log.e(TAG, "load :" + filePath + e.getMessage());
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return source;
	}

	public static Bitmap getImage(Context ctx, String imgUrl) {
		if (imgUrl == null || imgUrl.length() == 0) {
			return null;
		}
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			if (imgUrl.startsWith(BUtility.F_Widget_RES_SCHEMA)) {
				is = BUtility.getInputStreamByResPath(ctx, imgUrl);
				bitmap = BitmapFactory.decodeStream(is);
			} else if (imgUrl.startsWith(BUtility.F_FILE_SCHEMA)) {
				imgUrl = imgUrl.replace(BUtility.F_FILE_SCHEMA, "");
				bitmap = BitmapFactory.decodeFile(imgUrl);
			} else if (imgUrl.startsWith(BUtility.F_Widget_RES_path)) {
				try {
					is = ctx.getAssets().open(imgUrl);
					if (is != null) {
						bitmap = BitmapFactory.decodeStream(is);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				bitmap = BitmapFactory.decodeFile(imgUrl);
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			Log.i("fzy", "is:" + is);
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Log.i("fzy", "bitmap:" + bitmap);
		return bitmap;
	}
}
