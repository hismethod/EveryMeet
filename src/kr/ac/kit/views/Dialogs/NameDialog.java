package kr.ac.kit.views.Dialogs;

import com.gc.materialdesign.views.ButtonFlat;

import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import kr.ac.kit.R;

public class NameDialog
{
	private AlertDialog nameDialog;
	private Context mContext;
	private String name;
	
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
	
	public AlertDialog createDialog(final Context context)
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setView(R.layout.dialog_init_name);
		dialogBuilder.setTitle("당신의 성함은 무엇인가요?");
		
		nameDialog = dialogBuilder.create();
		nameDialog.setCancelable(false);                                                                        
		nameDialog.setCanceledOnTouchOutside(false);
		
		return nameDialog;
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
				}
			}
		});
		initNameBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				doWhenButtonClick();
			}
		});
	}
	
	public String getInputName()
	{
		EditText dialogInputName = (EditText) nameDialog.findViewById(R.id.initName);
		return dialogInputName.getText().toString().trim();
	}
	
	private void doWhenButtonClick()
	{
		EditText dialogInputName = (EditText) nameDialog.findViewById(R.id.initName);
		String inputName = dialogInputName.getText().toString().trim();
		Toast toast = Toast.makeText(mContext, "User: " + inputName, Toast.LENGTH_LONG);
		toast.show();
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