package io.pulque.blindhorse.choices

import io.pulque.blindhorse.R
import io.pulque.fastchoicebutton.configurations.ChoiceConfiguration

/*
 * @author savirdev on 2019-12-02
 */

private const val WRONG_OPTION_CONFIG = "WRONG_OPTION_CONFIG"
class WrongOptionConfig : ChoiceConfiguration(){

    override fun getColorButton() = R.color.colorAccent

    override fun getDrawable(): Int = R.drawable.ic_close_black_24dp

    override fun getTag() = WRONG_OPTION_CONFIG

    override fun getSound(): Int? = R.raw.wrong

}