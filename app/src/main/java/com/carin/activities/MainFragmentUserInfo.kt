package com.carin.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.carin.R
import com.carin.fragments.UserInfoFragment
import com.carin.model.TypeInfoUser
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale

class MainFragmentUserInfo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_info_fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tab_Layout)
        val viewPager = view.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.frag_view_pager)

        viewPager.adapter = UserInfoFragmentTypeAdapter(this)

        val currentLocale = Locale.getDefault()
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val currentLabel = when (currentLocale.language) {
                "pt" -> TypeInfoUser.values()[position].labelPt
                else -> TypeInfoUser.values()[position].labelEn
            }
            tab.text = currentLabel
        }.attach()

    }
}

class UserInfoFragmentTypeAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = TypeInfoUser.values().size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserInfoFragment()
            1 -> UserRouteFragment()
            else -> UserInfoFragment()
        }
    }
}
