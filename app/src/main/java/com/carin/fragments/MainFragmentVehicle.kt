package com.carin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.carin.R
import com.carin.domain.enums.VehicleType
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Locale

class MainFragmentVehicle : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.vehicle_fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tab_Layout)
        val viewPager = view.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.frag_view_pager)

        viewPager.adapter = FragmentTypeAdapter(this)

        val currentLocale = Locale.getDefault()
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val currentLabel = when (currentLocale.language) {
                "pt" -> VehicleType.entries[position].labelPt
                else -> VehicleType.entries[position].labelEn
            }
            tab.text = currentLabel
        }.attach()

    }
}

class FragmentTypeAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = VehicleType.entries.size

    override fun createFragment(position: Int): Fragment {
        val fragment = VehiclesTabFragment()
        fragment.arguments = Bundle().apply {
            putSerializable("vehicleType", VehicleType.entries[position])
        }

        return fragment
    }
}
