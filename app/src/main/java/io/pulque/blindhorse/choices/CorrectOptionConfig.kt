package io.pulque.blindhorse.choices

import io.pulque.blindfold.R
import io.pulque.fastchoicebutton.configurations.ChoiceConfiguration

/*
 * @author savirdev on 2019-12-02
 */

private const val CORRECT_OPTION_CONFIG = "CORRECT_OPTION_CONFIG"
class CorrectOptionConfig : ChoiceConfiguration(){

    override fun getColorButton() = R.color.colorAccent

    override fun getDrawable(): Int = R.drawable.ic_check_black_24dp

    override fun getTag() = CORRECT_OPTION_CONFIG

    override fun getSound(): Int? = R.raw.correct

}