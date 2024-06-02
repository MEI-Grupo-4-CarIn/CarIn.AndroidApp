package com.carin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.carin.R
import com.carin.domain.enums.RouteType
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale

class MainRoutesListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.route_fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tab_Layout)
        val viewPager = view.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.frag_view_pager)

        viewPager.adapter = RoutesFragmentTypeAdapter(this)

        val currentLocale = Locale.getDefault()
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val currentLabel = when (currentLocale.language) {
                "pt" -> RouteType.entries[position].labelPt
                else -> RouteType.entries[position].labelEn
            }
            tab.text = currentLabel
        }.attach()

    }
}

class RoutesFragmentTypeAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = RouteType.entries.size

    override fun createFragment(position: Int): Fragment {
        val fragment = RoutesTabFragment()
        fragment.arguments = Bundle().apply {
            putSerializable("routeType", RouteType.entries[position])
        }

        return fragment
    }
}

