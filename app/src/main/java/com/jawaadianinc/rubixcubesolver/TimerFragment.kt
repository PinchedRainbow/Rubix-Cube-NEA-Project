package com.jawaadianinc.rubixcubesolver

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.transition.TransitionInflater
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.ThreadLocalRandom

@SuppressLint("SetTextI18n")
class TimerFragment : Fragment() {
    var millisecondTime: Long = 0
    var startTime: Long = 0
    var timeBuff: Long = 0
    var updateTime: Long = 0L
    var handler: Handler? = null
    var seconds = 0
    var minutes: Int = 0
    var milliSeconds: Int = 0

    private var currentShuffle = ""
    var currentTime = ""
    private val colours = arrayListOf("Red", "Green", "Blue", "White", "Yellow", "Orange")
    private var alreadyToldUser = 0
    private var typeOfCube = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.explode)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val timerText: TextView = view.findViewById(R.id.timerText) as TextView
        val shuffleText: TextView = getView()?.findViewById(R.id.shuffleText) as TextView
        timerText.visibility = View.INVISIBLE

        Search.init()
        handler = Handler()

        timerText.text = "0:00:000"
        shuffleText.text = "Tap here to shuffle"
        shuffleText.gravity = 1

        val spinner: Spinner = view.findViewById(R.id.CubeChooser)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.cubeeSizes,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (parent?.getItemAtPosition(position).toString()) {
                    "3x3" -> {
                        shuffleText.text = "Tap here to shuffle (3x3)"
                    }
                    "2x2" -> {
                        shuffleText.text = "Tap here to shuffle (2x2)"
                    }
                    "4x4" -> {
                        shuffleText.text = "Tap here to shuffle (4x4)"
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        timerText.setOnClickListener {
            if (timerText.text == "0:00:000") {
                startTimer()
                timerText.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_200))
            } else {
                stopTimer()
                timerText.setTextColor(Color.parseColor(("#5D8832")))
            }
        }
        var alreadyPlayeed = 0
        val animation3: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)

        shuffleText.setOnClickListener {
            if (alreadyPlayeed == 0) {
                alreadyPlayeed++
                timerText.visibility = View.VISIBLE
                timerText.startAnimation(animation3)

            }
            shuffle()
        }

    }

    private fun saveTime() {
        val timeModel =
            TimeModel(currentTime, Timestamp(Date().time).toString(), currentShuffle, typeOfCube)
        val databaseTime = DatabaseTimes(context)
        databaseTime.addTime(timeModel)
        Toast.makeText(context, "Saved! -> $currentTime", Toast.LENGTH_SHORT).show()
    }

    private fun startTimer() {
        val spinner: Spinner = requireView().findViewById(R.id.CubeChooser)
        if (alreadyToldUser == 0) {
            view?.let {
                Snackbar.make(it, "Click on time to stop!", Snackbar.LENGTH_SHORT)
                    .show()
            }
            alreadyToldUser += 1
        }

        val animation2: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        val timerText: TextView? = view?.findViewById(R.id.timerText) as? TextView

        val shuffleText: TextView = view?.findViewById(R.id.shuffleText) as TextView
        shuffleText.startAnimation(animation2)

        shuffleText.visibility = View.INVISIBLE
        spinner.visibility = View.INVISIBLE

        startTime = SystemClock.uptimeMillis()
        if (timerText != null) {
            timerText.gravity = 1
        }
        handler?.postDelayed(runnable, 0)
    }

    private fun stopTimer() {
        val animation1: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        val timerText: TextView = view?.findViewById(R.id.timerText) as TextView
        handler?.removeCallbacks(runnable)

        val shuffleText: TextView = view?.findViewById(R.id.shuffleText) as TextView
        val spinner: Spinner = requireView().findViewById(R.id.CubeChooser)
        shuffleText.startAnimation(animation1)

        shuffleText.visibility = View.VISIBLE

        saveTime()
        shuffle()
        timerText.text = "0:00:000"
        spinner.visibility = View.VISIBLE

    }

    private var runnable: Runnable = object : Runnable {
        @SuppressLint("DefaultLocale")
        override fun run() {
            val timerText: TextView? = view?.findViewById(R.id.timerText) as? TextView
            millisecondTime = SystemClock.uptimeMillis() - startTime
            updateTime = timeBuff + millisecondTime
            seconds = ((updateTime.toInt() / 1000))
            minutes = seconds / 60
            seconds %= 60
            milliSeconds = (updateTime % 1000).toInt()
            timerText?.gravity = Gravity.CENTER
            timerText?.text = (minutes.toString() + ":"
                    + java.lang.String.format("%02d", seconds).toString() + ":"
                    + java.lang.String.format("%03d", milliSeconds))
            currentTime = (minutes.toString() + ":"
                    + java.lang.String.format("%02d", seconds).toString() + ":"
                    + java.lang.String.format("%03d", milliSeconds))
            handler?.postDelayed(this, 0)
        }
    }
    private var toastingShuffle = 0

    private fun shuffle() {
        val spinner: Spinner = requireView().findViewById(R.id.CubeChooser)
        val shuffuleText: TextView = view?.findViewById(R.id.shuffleText) as TextView

        when {
            shuffuleText.text == "Tap here to shuffle (2x2)" -> {
                val slen = randomInt2x2()
                val scramble = ScrambleGenerator(slen).scramble
                typeOfCube = "2x2"
                val chosenColour = colours.random()

                currentShuffle = scramble
                shuffuleText.gravity = 1
                shuffuleText.setTextAnimation("Hold $chosenColour in front\n$scramble", 500)
                when (chosenColour) {
                    "White" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    "Red" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FF0000"))
                    }
                    "Blue" -> {
                        shuffuleText.setTextColor(Color.parseColor("#0000ee"))
                    }
                    "Green" -> {
                        shuffuleText.setTextColor(Color.parseColor("#7fff00"))
                    }
                    "Yellow" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FFFF00"))
                    }
                    "Orange" -> {
                        shuffuleText.setTextColor(Color.parseColor("#ff7f00"))
                    }
                }
            }
            shuffuleText.text == "Tap here to shuffle (3x3)" -> {
                val slen = randomInt()
                val scramble = ScrambleGenerator(slen).scramble
                typeOfCube = "3x3"
                val chosenColour = colours.random()

                currentShuffle = scramble
                shuffuleText.gravity = 1
                shuffuleText.setTextAnimation("Hold $chosenColour in front\n$scramble", 500)
                when (chosenColour) {
                    "White" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    "Red" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FF0000"))
                    }
                    "Blue" -> {
                        shuffuleText.setTextColor(Color.parseColor("#0000ee"))
                    }
                    "Green" -> {
                        shuffuleText.setTextColor(Color.parseColor("#7fff00"))
                    }
                    "Yellow" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FFFF00"))
                    }
                    "Orange" -> {
                        shuffuleText.setTextColor(Color.parseColor("#ff7f00"))
                    }
                }
            }
            shuffuleText.text == "Tap here to shuffle (4x4)" -> {
                val slen = randomInt4x4()
                val scramble = ScrambleGenerator(slen).scramble
                typeOfCube = "4x4"
                val chosenColour = colours.random()

                currentShuffle = scramble
                shuffuleText.gravity = 1
                shuffuleText.setTextAnimation("Hold $chosenColour in front\n$scramble", 500)
                when (chosenColour) {
                    "White" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    "Red" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FF0000"))
                    }
                    "Blue" -> {
                        shuffuleText.setTextColor(Color.parseColor("#0000ee"))
                    }
                    "Green" -> {
                        shuffuleText.setTextColor(Color.parseColor("#7fff00"))
                    }
                    "Yellow" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FFFF00"))
                    }
                    "Orange" -> {
                        shuffuleText.setTextColor(Color.parseColor("#ff7f00"))
                    }
                }
            }
            typeOfCube == "3x3" -> {
                val slen = randomInt()
                val scramble = ScrambleGenerator(slen).scramble
                val chosenColour = colours.random()
                typeOfCube = "3x3"
                currentShuffle = scramble
                shuffuleText.gravity = 1
                shuffuleText.setTextAnimation("Hold $chosenColour in front\n$scramble", 500)
                when (chosenColour) {
                    "White" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    "Red" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FF0000"))
                    }
                    "Blue" -> {
                        shuffuleText.setTextColor(Color.parseColor("#0000ee"))
                    }
                    "Green" -> {
                        shuffuleText.setTextColor(Color.parseColor("#7fff00"))

                    }
                    "Yellow" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FFFF00"))
                    }
                    "Orange" -> {
                        shuffuleText.setTextColor(Color.parseColor("#ff7f00"))
                    }
                }
            }
            typeOfCube == "2x2" -> {
                val slen = randomInt2x2()
                val scramble = ScrambleGenerator(slen).scramble
                typeOfCube = "2x2"
                val chosenColour = colours.random()
                currentShuffle = scramble
                shuffuleText.gravity = 1
                shuffuleText.setTextAnimation("Hold $chosenColour in front\n$scramble", 500)
                when (chosenColour) {
                    "White" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    "Red" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FF0000"))
                    }
                    "Blue" -> {
                        shuffuleText.setTextColor(Color.parseColor("#0000ee"))
                    }
                    "Green" -> {
                        shuffuleText.setTextColor(Color.parseColor("#7fff00"))

                    }
                    "Yellow" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FFFF00"))
                    }
                    "Orange" -> {
                        shuffuleText.setTextColor(Color.parseColor("#ff7f00"))
                    }
                }
            }
            typeOfCube == "4x4" -> {
                val slen = randomInt4x4()
                val scramble = ScrambleGenerator(slen).scramble
                typeOfCube = "4x4"
                val chosenColour = colours.random()

                currentShuffle = scramble
                shuffuleText.gravity = 1
                shuffuleText.setTextAnimation("Hold $chosenColour in front\n$scramble", 500)
                when (chosenColour) {
                    "White" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FFFFFF"))
                    }
                    "Red" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FF0000"))
                    }
                    "Blue" -> {
                        shuffuleText.setTextColor(Color.parseColor("#0000ee"))
                    }
                    "Green" -> {
                        shuffuleText.setTextColor(Color.parseColor("#7fff00"))

                    }
                    "Yellow" -> {
                        shuffuleText.setTextColor(Color.parseColor("#FFFF00"))
                    }
                    "Orange" -> {
                        shuffuleText.setTextColor(Color.parseColor("#ff7f00"))
                    }
                }
            }
        }

        if (toastingShuffle == 0) {
            Snackbar.make(
                requireView(),
                "To learn the notation\nGo to the tutorial",
                Snackbar.LENGTH_SHORT
            ).show()
            toastingShuffle += 1
        }

        spinner.visibility = View.INVISIBLE

    }

    private fun randomInt(): Int {
        return ThreadLocalRandom.current().nextInt(17, 22)
    }

    private fun randomInt2x2(): Int {
        return ThreadLocalRandom.current().nextInt(8, 12)
    }

    private fun randomInt4x4(): Int {
        return ThreadLocalRandom.current().nextInt(20, 30)
    }

    private fun TextView.setTextAnimation(
        text: String,
        duration: Long = 300,
        completion: (() -> Unit)? = null
    ) {
        fadOutAnimation(duration) {
            this.text = text
            fadInAnimation(duration) {
                completion?.let {
                    it()
                }
            }
        }
    }

    private fun View.fadOutAnimation(
        duration: Long = 300,
        visibility: Int = View.INVISIBLE,
        completion: (() -> Unit)? = null
    ) {
        animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                this.visibility = visibility
                completion?.let {
                    it()
                }
            }
    }

    private fun View.fadInAnimation(duration: Long = 300, completion: (() -> Unit)? = null) {
        alpha = 0f
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .setDuration(duration)
            .withEndAction {
                completion?.let {
                    it()
                }
            }
    }
}

