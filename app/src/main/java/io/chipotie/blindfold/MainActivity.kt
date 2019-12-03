package io.chipotie.blindfold

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import io.chipotie.blindfold.choices.CorrectOptionConfig
import io.chipotie.blindfold.choices.WrongOptionConfig
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val columns = arrayOf("A", "B","C","D","E","F","G","H")

    private var lastPair : Pair<Int, Int>? = null

    private var tts: TextToSpeech? = null

    private val correctOptionConfig = CorrectOptionConfig()
    private val wrongOptionConfig = WrongOptionConfig()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initVoiceEngine()

        setupFastChoise()

    }

    private fun setupFastChoise(){
        colorSelector.apply {
            setOnLeftSwipeCompleteListener{
                lastPair?.let {
                    if (!isBlackSquare(it.first,it.second)){
                        colorSelector.applyConfiguration(correctOptionConfig)
                    }else{
                        colorSelector.applyConfiguration(wrongOptionConfig)
                    }
                }
            }
            setOnRightSwipeCompleteListener{
                lastPair?.let {
                    if (isBlackSquare(it.first,it.second)){
                        colorSelector.applyConfiguration(correctOptionConfig)
                    }else{
                        colorSelector.applyConfiguration(wrongOptionConfig)
                    }
                }
            }

            setOnResetSwipeListener{
                generatePair()
            }

            registerConfiguration(CorrectOptionConfig())
            registerConfiguration(WrongOptionConfig())
        }
    }

    private fun generatePair() {
        lastPair = Pair(generateRandom(), generateRandom())
        lastPair?.let {
            showRandom()
            speak(getString(R.string.square_placeholder, columns[it.first - 1], it.second))
        }
    }

    private fun initVoiceEngine(){
        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS){
                tts?.language = Locale("es-MX")
                generatePair()
            }
        }
    }


    private fun speak(text: String){
        tts?.speak(text, TextToSpeech.QUEUE_ADD,null, null)
    }

    private fun showRandom() = lastPair?.let {
        square.text = getString(R.string.square_placeholder, columns[it.first - 1], it.second)
    }

    private fun isBlackSquare(column: Int, row: Int) : Boolean{
        val isColumnPair = column % 2 == 0
        val isRowPair = row % 2 == 0

        return (isColumnPair && isRowPair) || (!isColumnPair && !isRowPair)
    }

    private fun generateRandom() = (1 until 8).random()
}
