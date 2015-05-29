package kr.ac.kit.util;

import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

@SharedPref
public interface AppSharedPreference
{
	@DefaultString("")
	public String myName();
	public long lastUpdated();
}
