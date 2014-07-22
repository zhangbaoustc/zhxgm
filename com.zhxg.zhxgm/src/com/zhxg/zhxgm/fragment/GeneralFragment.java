package com.zhxg.zhxgm.fragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhxg.zhxgm.MainActivity;
import com.zhxg.zhxgm.R;
/**
 * 
 * @author zb
 *
 */
public class GeneralFragment extends Fragment{
	/**
	 * 
	 */
	
	private int item; 
	protected static View main_title_RelativeLayout; 
	protected final static String key = "Bundle";   
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getArguments() != null) {  
			if(getArguments().containsKey(MainActivity.Item)) {
				item = getArguments().getInt(MainActivity.Item);
			}
		}
	}
	
	/** Fragment**/
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_general, container, false);
		GeneralFragment fragment = null;
		switch(item) {
		case R.id.fragment_bottom_news: 
			break;
		case R.id.fragment_bottom_game_man:
			fragment = new GameManager(); 
			 break;
		case R.id.fragment_bottom_notice:
			break;
		case R.id.fragment_bottom_more:
			break;
		default:
			break;
		}
		if(fragment != null) {
			getActivity().getFragmentManager().beginTransaction().replace(R.id.general_fragment, fragment).commit();
		}
		return view;
	}
	
	/**���ñ���**/
	protected void setTitle(Object title) {
		if(main_title_RelativeLayout != null) {
		}
	}
	
	/**ҳ����תֵ����**/
	protected void setBundle(Object... objects) {
		Bundle arguments = new Bundle();
		arguments.putSerializable(key, objects);
		GeneralFragment generalFragment = new GeneralFragment();
		generalFragment.setArguments(arguments);
	}
	
	/**��ȡ��ݵ�ֵ**/
	protected Object[] getBundle() {
		if(getArguments() != null) {
			System.out.println("getBundle");
			if(getArguments().containsKey(key)) {
				Object[] object = (Object[]) getArguments().getSerializable(key);
				return object;
			}
		}
		return null;
	}
	
	/**�޲�ҳ����ת**/
	protected void toIntent(GeneralFragment generalFragment) {
		if(generalFragment != null) {
			getActivity().getFragmentManager().beginTransaction().replace(R.id.general_fragment, generalFragment).commit();
		}
	}

}
