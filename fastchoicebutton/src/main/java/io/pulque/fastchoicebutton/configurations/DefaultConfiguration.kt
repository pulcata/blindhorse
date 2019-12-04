package io.pulque.fastchoicebutton.configurations

import io.pulque.fastchoicebutton.R

/*
 * @author savirdev on 2019-12-02
 */

private const val DEFAULT__CONFIGURATION_TAG = "DEFAULT__CONFIGURATION_TAG"
class DefaultConfiguration : ChoiceConfiguration(){

    override fun getColorButton(): Int {
        return R.color.colorAccent
    }

    override fun getDrawable(): Int {
        return R.drawable.ic_check_black_24dp
    }

    override fun getTag() = DEFAULT__CONFIGURATION_TAG

    override fun getSound(): Int? = null

}