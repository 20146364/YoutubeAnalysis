package hungpt.deverlopment.youtubeanalysis.fragment.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TabAdaptor extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private final FragmentManager fragmentManager;

    public TabAdaptor(FragmentManager fm) {
        super(fm);
        this.fragmentManager = fm;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String id, int cmt) {
        Bundle bundle = new Bundle();
        bundle.putString("ID", id);
        bundle.putInt("cmt", cmt);
        fragment.setArguments(bundle);
        mFragmentList.add(fragment);
    }

    public void clearAllItems() {
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }

    public void removeItem(int position){
        mFragmentList.remove(position == 0 ? 0 : position - 1);
        mFragmentTitleList.remove(position == 0 ? 0 : position - 1);
    }

    public void updateItem(int position, Fragment fragment){
        mFragmentList.set(position, fragment);
    }

    public void updateItem(String title, Fragment fragment){
        int index = mFragmentTitleList.indexOf(title);
        if(index != -1){
            updateItem(index, fragment);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        if (mFragmentList.contains(object)) return mFragmentList.indexOf(object);
        else return POSITION_NONE;
    }

    public void notifyDataSetUpdate(){
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragmentManager.beginTransaction().remove((Fragment) object).commitNowAllowingStateLoss();
    }

}
