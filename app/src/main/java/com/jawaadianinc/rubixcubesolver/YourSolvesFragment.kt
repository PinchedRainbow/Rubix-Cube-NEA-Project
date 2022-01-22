package com.jawaadianinc.rubixcubesolver

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment


class YourSolvesFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.explode)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_your_solves, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val textSolves: TextView = view.findViewById(R.id.totalSolves)
        val textSolves3x3: TextView = view.findViewById(R.id.text_3x3)
        val textSolves2x2: TextView = view.findViewById(R.id.text_2x2)
        val textSolves4x4: TextView = view.findViewById(R.id.text_4x4)

        val databaseTimes = DatabaseTimes(context)

        //3x3 card view
        val allTimes3x3 = databaseTimes.allTimes3x3
        val total = databaseTimes.totalSolves3x3
        textSolves3x3.text = "Total Solves: $total\nAverage time: $allTimes3x3"

        //2x2 card view
        val allTimes2x2 = databaseTimes.allTimes2x2
        val total2x2 = databaseTimes.totalSolves2x2
        textSolves2x2.text = "Total Solves: $total2x2\nAverage time: $allTimes2x2"

        //4x4 card view
        val allTimes4x4 = databaseTimes.allTimes4x4
        val total4x4 = databaseTimes.totalSolves4x4
        textSolves4x4.text = "Total Solves: $total4x4\nAverage time: $allTimes4x4"

        //Total solves
        val count = databaseTimes.totalSolves
        textSolves.text = "Overall total solves: $count"

        val cardView: CardView = view.findViewById(R.id.card_view)
        val cardView2x2: CardView = view.findViewById(R.id.card_view2x2)
        val cardView4x4: CardView = view.findViewById(R.id.card_view4x4)


        val sharedPreferences = requireActivity().getSharedPreferences(
            "sharedPrefs", AppCompatActivity.MODE_PRIVATE
        )
        val isDarkModeOn = sharedPreferences
            .getBoolean(
                "isDarkModeOn", true
            )

        if (isDarkModeOn) {
            cardView2x2.setCardBackgroundColor((Color.rgb(64, 60, 69)))
            cardView4x4.setCardBackgroundColor((Color.rgb(64, 60, 69)))
            cardView.setCardBackgroundColor((Color.rgb(64, 60, 69)))
        } else {
            cardView2x2.setCardBackgroundColor(Color.rgb(187, 134, 252))
            cardView.setCardBackgroundColor(Color.rgb(187, 134, 252))
            cardView4x4.setCardBackgroundColor(Color.rgb(187, 134, 252))
        }
    }
}
