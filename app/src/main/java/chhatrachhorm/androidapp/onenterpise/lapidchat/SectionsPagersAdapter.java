package chhatrachhorm.androidapp.onenterpise.lapidchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by chhormchhatra on 8/12/17.
 */

class SectionsPagersAdapter extends FragmentPagerAdapter {
    public SectionsPagersAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new RequestFragment();
            case 1:
                return new ChatsFragment();
            case 2:
                return new FriendsFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Requests";
            case 1:
                return "Chats";
            case 2:
                return "Friends";
            default:
                return null;
        }
    }
}
