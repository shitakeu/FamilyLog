package com.example.familylog;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class PhotoActivity extends Activity{

	private	CameraPreview	_view = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		LinearLayout layout = new LinearLayout(this);
		layout.setBackgroundColor(Color.WHITE);

		_view = new CameraPreview(this,savedInstanceState);

//		layout.addView(_view,new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		layout.addView(_view,new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		setContentView(layout);
	}


	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		if(_view != null)
			_view.OnSaveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);  
		
		if(_view != null)
			_view.OnLoadState(savedInstanceState);
	}

}
