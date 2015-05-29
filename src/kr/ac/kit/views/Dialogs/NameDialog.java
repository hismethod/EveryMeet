package kr.ac.kit.views.Dialogs;

import com.gc.materialdesign.views.ButtonFlat;
import com.rey.material.widget.EditText;
import com.afollestad.*;
import com.afollestad.materialdialogs.prefs.MaterialEditTextPreference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import kr.ac.kit.R;

public class NameDialog
{
	private Context mContext;
	private AlertDialog nameDialog;
	private MaterialEditTextPreference nameDialog2;
	private String name;
	
	public NameDialog(){}
	public NameDialog(Context context, String title)
	{
		createDialog(context, title);
	}
	
	public String getName()
	{
		return name;
	}

	public void setOnDismissListener(OnDismissListener listener)
	{
		nameDialog.setOnDismissListener(listener);
	}
	
	public void createDialog(Context context, String title)
	{
		mContext = context;
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setView(R.layout.dialog_init_name);
		dialogBuilder.setTitle(title);
		
		nameDialog = dialogBuilder.create();
		nameDialog.setCancelable(false);                                                                        
		nameDialog.setCanceledOnTouchOutside(false);
	}
	
	public void createDialog2(Context context, String title)
	{
		nameDialog2 = new MaterialEditTextPreference(context);
		nameDialog2.setTitle(title);
		
		nameDialog2.setText("여기에 내용?");
	}
	
	public void showDialog2()
	{
		Dialog a = nameDialog2.getDialog();
	}
	
	public void showDialog()
	{
		nameDialog.show();
		EditText dialogInputName = (EditText) nameDialog.findViewById(R.id.initName);
		final ButtonFlat initNameBtn = (ButtonFlat) nameDialog.findViewById(R.id.initNameBtn);
		initNameBtn.setEnabled(false);
		dialogInputName.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override
			public void afterTextChanged(Editable str)
			{
				if(isNull(str.toString().trim()))
				{
					initNameBtn.setEnabled(false);
				}
				else
				{
					initNameBtn.setEnabled(true);
					initNameBtn.setBackgroundColor(Color.parseColor("#00897B"));
				}
			}
		});
		initNameBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				doWhenSubmitButtonClick();
			}
		});
	}
	
	private void doWhenSubmitButtonClick()
	{
		EditText dialogInputName = (EditText) nameDialog.findViewById(R.id.initName);
		String inputName = dialogInputName.getText().toString().trim();
		name = inputName;
		nameDialog.dismiss();
	}
	
	private static boolean isNull(String query)
	{
		if(query.isEmpty() || query.equals(null))
		{
			return true;
		}
		else
			return false;
	}
}