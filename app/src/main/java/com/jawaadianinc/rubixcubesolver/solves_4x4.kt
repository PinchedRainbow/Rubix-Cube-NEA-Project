package com.jawaadianinc.rubixcubesolver

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment

class solves_4x4 : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.explode)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_solves_4x4, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val list = view.findViewById(R.id.list4x4) as ListView
        val databaseTimes = DatabaseTimes(requireContext())
        val threeTimes = databaseTimes.get4x4Times()
        val timeArrayAdaptor =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, threeTimes)
        list.adapter = timeArrayAdaptor
        timeArrayAdaptor.notifyDataSetChanged()

        val total = databaseTimes.totalSolves4x4
        val nosolves: TextView = view.findViewById(R.id.nosolves3)

        if (total == 0) {
            list.visibility = View.INVISIBLE
            nosolves.visibility = View.VISIBLE
        } else {
            list.visibility = View.VISIBLE
            nosolves.visibility = View.INVISIBLE
        }

        list.setOnItemClickListener { parent, _, position, _ ->
            var time = parent.getItemAtPosition(position).toString()
            time = time.substringAfter(" ")
            val timeId = databaseTimes.getId(time)
            val date = databaseTimes.getTimeID(timeId)
            val timeSolved = databaseTimes.getSolvedTime(timeId)
            val shuffle = databaseTimes.getShuffle(timeId)
            val typeOfCube = databaseTimes.getTypeOfCube(timeId)
            val number = position + 1
            showPopupWindow(date, shuffle, timeSolved, typeOfCube, number)
        }

    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun showPopupWindow(
        date: String,
        shuffle: String,
        timeSolved: String,
        typeOfCube: String,
        number: Int
    ) {
        val inflater =
            view?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.showtime, null)
        val formal = AnimationUtils.loadAnimation(context, R.anim.fadein)

        val deleteBT: Button = popupView.findViewById(R.id.deleteOne)
        val copyShuffle: Button = popupView.findViewById(R.id.copyShuffle)

        popupView.startAnimation(formal)

        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        val focusable = true
        val popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        val test2 = popupView.findViewById<TextView>(R.id.titleText)
        test2.text = "Solve Time: $timeSolved, Number: $number"
        val textView = popupView.findViewById<TextView>(R.id.H_perm_txt)
        textView.text = "On $date\nShuffle:\n$shuffle\nCube_Original Type: $typeOfCube"
        val buttonEdit: Button = popupView.findViewById(R.id.messageButton)
        buttonEdit.setOnClickListener {
            popupWindow.dismiss()
        }

        deleteBT.setOnClickListener {
            showAlertDialog(timeSolved)
        }

        copyShuffle.setOnClickListener {
            copyText(shuffle)
            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()

        }
    }

    private fun copyText(text: String) {
        val myClipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClip: ClipData = ClipData.newPlainText("Label", text)
        myClipboard.setPrimaryClip(myClip)
    }

    private fun showAlertDialog(time: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Clear solve $time")
        builder.setMessage("Are you sure?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { _, _ ->
            val databaseTime = DatabaseTimes(context)
            if (databaseTime.DeleteTime(time)) {
                Toast.makeText(requireContext(), "Deleted!", Toast.LENGTH_SHORT).show()
                refresh()
            }
        }
        builder.setNegativeButton("Cancel") { _, _ ->
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()

    }

    private fun refresh() {
        activity?.finish()
        val intent = Intent(requireContext(), MainMenu::class.java)
        startActivity(intent)
    }

}