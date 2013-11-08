package com.app.floatinglabelpattern;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * font color, font size, font face, hint 
 * 0094f8
 * @author hbansal
 *
 */
public class FloatingLabelView extends RelativeLayout implements OnFocusChangeListener, AnimationListener, TextWatcher {

	EditText mEditText;
	TextView mFloatText;
	private Animation showAnimation;
	private Animation hideAnimation;
	
	boolean isHidden;
	private int mTextColorDisabled;
	private int mTextColor;
	
	public FloatingLabelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	public FloatingLabelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}
	
	private void init(Context context, AttributeSet attrs, int defStyle) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.floatingtext, this, true);
		
		mEditText = (EditText) findViewById(R.id.edit_text);
		mEditText.setOnFocusChangeListener(this);
		mEditText.addTextChangedListener(this);
		mFloatText = (TextView) findViewById(R.id.floating_text);
		
		showAnimation = AnimationUtils.loadAnimation(context, R.anim.show_text);
		showAnimation.setAnimationListener(this);
		hideAnimation = AnimationUtils.loadAnimation(context, R.anim.hide_text);
		hideAnimation.setAnimationListener(this);
		
		setAttributes(context, attrs, defStyle);
	}

	private void setAttributes(Context context, AttributeSet attrs, int defStyle) {
		Resources res = context.getResources();
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.floatlabel, defStyle, 0);

		String hintText = a.getString(R.styleable.floatlabel_text);
		mEditText.setHint(hintText);
		mFloatText.setText(mEditText.getHint());
		
		/*int inputType = a.getInteger(R.styleable.floatlabel_input_type, InputType.TYPE_TEXT_VARIATION_NORMAL);
		mEditText.setInputType(inputType);*/
		
		mTextColor = a.getInteger(R.styleable.floatlabel_text_color, res.getColor(R.color.blue));
		mFloatText.setTextColor(mTextColor);
		
		mTextColorDisabled = a.getInteger(R.styleable.floatlabel_text_color_disabled, res.getColor(R.color.gray));
		
		int textSize = a.getInteger(R.styleable.floatlabel_text_size, (int)res.getDimension(R.dimen.default_text_size));
		mFloatText.setTextSize(textSize);
		
		a.recycle();
		isHidden = true;
	}

	private void showFloatingText() {
		if(!isHidden) return;
		
		enableText(true);
		mFloatText.setVisibility(View.VISIBLE);
		
		Animation animation = mFloatText.getAnimation();
		
		if(animation == null || (animation != null && !animation.hasStarted())) {
			mFloatText.startAnimation(showAnimation);	
		}
	}
	
	private void hideFloatingText() {
		if(isHidden) return;
		
		Animation animation = mFloatText.getAnimation();
		
		if(animation == null || (animation != null && !animation.hasStarted())) {
			mFloatText.startAnimation(hideAnimation);
		}
	}
	
	private void enableText(boolean isEnable) {
		if(isEnable) {
			mFloatText.setTextColor(mTextColor);
		} else {
			mFloatText.setTextColor(mTextColorDisabled);
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence charseq, int start, int count, int after) {
		showFloatingText();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus) {
			
			if(mEditText.getText().length() > 0) {
				enableText(true);
				showFloatingText();	
			}
			
		} else {
			enableText(false);
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(animation == hideAnimation) {
			isHidden = true;
			mFloatText.setVisibility(View.INVISIBLE);
			
		} else if(animation == showAnimation) {
			isHidden = false;
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}
	
	@Override
	public void afterTextChanged(Editable arg0) {
	}
	
	@Override
	public void onTextChanged(CharSequence charseq, int arg1, int arg2, int arg3) {
		if(TextUtils.isEmpty(charseq)) hideFloatingText();
	}
}