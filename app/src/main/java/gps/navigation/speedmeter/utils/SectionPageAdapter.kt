package gps.navigation.speedmeter.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionPageAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
    // This array list will gonna add the fragment one after another
    private val mFragmentList: MutableList<Fragment> = ArrayList()

    // This list of type string is for the title of the tab
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getItem(i: Int): Fragment {
        return mFragmentList[i]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }
}