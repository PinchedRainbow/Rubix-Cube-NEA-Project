package com.jawaadianinc.rubixcubesolver

import java.util.*

class ScrambleGenerator(algorithmLength: Int) {
    var mLastFace = 0
    var mTwoFacesAgo = 0
    var scramble = ""
    private val nextFace: Int
        get() {
            var random = Random().nextInt(6) + 1
            while (random == mTwoFacesAgo && random % 3 == mLastFace % 3 || random == mLastFace) {
                random = Random().nextInt(6) + 1
            }
            mTwoFacesAgo = mLastFace
            mLastFace = random
            return random
        }
    private val nextRotation: Int
        get() = Random().nextInt(4) + 1
    private val nextMove: String
        get() {
            var nextMove = ""
            when (nextFace) {
                1 -> nextMove = "U"
                2 -> nextMove = "L"
                3 -> nextMove = "F"
                4 -> nextMove = "D"
                5 -> nextMove = "R"
                6 -> nextMove = "B"
            }
            when (nextRotation) {
                2 -> nextMove += "2"
                3 -> nextMove = "$nextMove'"
                4 -> nextMove += "2"
            }
            return "$nextMove "
        }

    init {
        for (i in 1..algorithmLength) {
            scramble += nextMove
        }
    }
}