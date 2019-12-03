package io.chipotie.fastchoisebutton.configurations

/*
 * @author savirdev on 2019-12-02
 */

abstract class ChoiceConfiguration{
    abstract fun getColorButton() : Int
    abstract fun getDrawable(): Int
    abstract fun getTag() : String
    abstract fun getSound() : Int?
}