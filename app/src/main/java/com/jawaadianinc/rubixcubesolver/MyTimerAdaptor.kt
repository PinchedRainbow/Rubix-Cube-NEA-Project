package com.jawaadianinc.rubixcubesolver

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class MyTimerAdaptor(fm: FragmentManager?, var totalTabs: Int) :

    FragmentPagerAdapter(fm!!) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                TimerFragment()
            }
            1 -> {
                YourSolvesFragment()
            }
            2 -> {
                solves_3x3()
            }
            3 -> {
                solves_2x2()
            }
            4 -> {
                solves_4x4()
            }


            else -> throw IllegalStateException("position $position is invalid for this viewpager")
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}