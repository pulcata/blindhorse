package io.chipotie.fastchoisebutton

import android.animation.*
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import io.chipotie.fastchoisebutton.configurations.ChoiceConfiguration
import io.chipotie.fastchoisebutton.configurations.ConfigurationManager


/*
 * @author savirdev on 2019-12-02
 */

class FastChoiseButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleInt: Int = 0,
    defStyleRes: Int = 0
) : RelativeLayout(context, attributeSet, defStyleInt, defStyleRes) {

    private val slidingButton: ImageView
    private val firstOption: TextView
    private val secondOption: TextView
    private var active = false
    private var initialButtonWidth = 0

    private var rightSwipeCompleteListener: (() -> Unit)? = null
    private var leftSwipeCompleteListener: (() -> Unit)? = null
    private var resetSwipeCompleteListener: (() -> Unit)? = null

    val centerDrawable: Drawable?

    private val configurationManager: ConfigurationManager = ConfigurationManager()

    init {

        val background: RelativeLayout = this

        val layoutParamsView = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParamsView.addRule(CENTER_IN_PARENT, TRUE)

        background.background = ContextCompat.getDrawable(
            context,
            R.drawable.shape_rounded
        )

        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val leftText = TextView(context)
        firstOption = leftText

        leftText.gravity = Gravity.CENTER

        leftText.setTextColor(Color.WHITE)

        val rightText = TextView(context)
        secondOption = rightText

        rightText.gravity = Gravity.CENTER

        rightText.text = context.getString(R.string.default_instruction)

        rightText.setTextColor(Color.WHITE)

        val optionsContainer = LinearLayout(context)
        optionsContainer.orientation = LinearLayout.HORIZONTAL

        val llParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.MATCH_PARENT, 1F
        )

        optionsContainer.addView(leftText, llParams)
        optionsContainer.addView(rightText, llParams)

        background.addView(optionsContainer, layoutParams)

        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FastChoiseButton)
        centerDrawable = typedArray.getDrawable(R.styleable.FastChoiseButton_centerDrawable)
        firstOption.text = typedArray.getString(R.styleable.FastChoiseButton_firstOption)
        secondOption.text = typedArray.getString(R.styleable.FastChoiseButton_secondOption)
        typedArray.recycle()

        val swipeButton = ImageView(context)
        slidingButton = swipeButton

        swipeButton.setImageDrawable(centerDrawable)
        swipeButton.setPadding(40, 40, 40, 40)

        val layoutParamsButton = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        layoutParamsButton.addRule(CENTER_HORIZONTAL, TRUE)
        layoutParamsButton.addRule(CENTER_VERTICAL, TRUE)

        swipeButton.background = ContextCompat.getDrawable(
            context,
            R.drawable.shape_button
        )

        addView(swipeButton, layoutParamsButton)

        setOnTouchListener(getButtonTouchListener())
    }

    private fun getButtonTouchListener(): OnTouchListener? {
        return OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> return@OnTouchListener true

                MotionEvent.ACTION_MOVE -> {

                    //Scroll
                    if (event.x + slidingButton.width / 2F < width && event.x - slidingButton.width / 2F > 0) {
                        slidingButton.x = event.x - slidingButton.width / 2F
                        val a = 1F - 1.3F * (slidingButton.x + slidingButton.width/2) / width
                        firstOption.alpha = 1 - a
                        secondOption.alpha = a
                    }

                    //Right Limit
                    if (event.x + slidingButton.width / 2 > width &&
                        slidingButton.x + slidingButton.width / 2 < width
                    ) {
                        slidingButton.x = width.toFloat() - slidingButton.width
                    }

                    //Left Limit
                    if (event.x - slidingButton.width / 2 < 0 &&
                        slidingButton.x + slidingButton.width / 2 > 0
                    ) {
                        slidingButton.x = slidingButton.width.toFloat()
                    }


                    if (event.x < slidingButton.width / 2 &&
                        slidingButton.x > 0
                    ) {
                        slidingButton.x = 0F
                    }
                    return@OnTouchListener true
                }

                MotionEvent.ACTION_UP -> {
                    if (active) {
                        collapseButton()
                    } else {
                        initialButtonWidth = slidingButton.width

                        when {
                            slidingButton.x + slidingButton.width > width * 0.85F -> {
                                rightSwipeCompleteListener?.invoke()
                            }
                            slidingButton.x + slidingButton.width < width * 0.85F -> {
                                leftSwipeCompleteListener?.invoke()
                            }
                            else -> {
                                moveButtonBack()
                            }
                        }
                    }
                    return@OnTouchListener true
                }
            }
            false
        }
    }

    fun setOnRightSwipeCompleteListener(listener: () -> Unit = {}) {
        rightSwipeCompleteListener = listener
    }

    fun setOnLeftSwipeCompleteListener(listener: () -> Unit = {}) {
        leftSwipeCompleteListener = listener
    }

    fun setOnResetSwipeListener(listener: () -> Unit = {}) {
        resetSwipeCompleteListener = listener
    }

    fun registerConfiguration(configuration: ChoiceConfiguration) {
        configurationManager.registerConfiguration(configuration)
    }

    fun applyCOnfiguration(tag: String) {
        configurationManager.getConfiguration(tag)?.let {
            applyConfiguration(it)
        }
    }

    fun applyConfiguration(configuration: ChoiceConfiguration) {
        val positionAnimator = ValueAnimator.ofFloat(slidingButton.x, 0f)
        positionAnimator.addUpdateListener {
            val x = positionAnimator.animatedValue as Float
            slidingButton.x = x
        }
        val widthAnimator = ValueAnimator.ofInt(
            slidingButton.width,
            width + slidingButton.width
        )
        widthAnimator.addUpdateListener {
            val params = slidingButton.layoutParams
            params.width = (widthAnimator.animatedValue as Int)
            slidingButton.layoutParams = params
        }
        val animatorSet = AnimatorSet()
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)

                active = true

                slidingButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        configuration.getDrawable()
                    )
                )

                configuration.getSound()?.let {
                    playSound(it)
                }
            }
        })
        animatorSet.playTogether(positionAnimator, widthAnimator)
        animatorSet.start()
    }

    private fun playSound(sound: Int) {
        val mp = MediaPlayer.create(context, sound)
        mp.start()
        mp.setOnCompletionListener {
            collapseButton()
        }
    }

    private fun collapseButton() {
        val widthAnimator = ValueAnimator.ofInt(
            slidingButton.width,
            initialButtonWidth
        )
        widthAnimator.addUpdateListener {
            val params = slidingButton.layoutParams
            params.width = (widthAnimator.animatedValue as Int)
            slidingButton.layoutParams = params
        }
        widthAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                active = false
                slidingButton.setImageDrawable(centerDrawable)
            }
        })
        val firstOptionAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
            firstOption, "alpha", 1F
        )

        val secondOptionAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
            secondOption, "alpha", 1F
        )
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(firstOptionAnimator, secondOptionAnimator, widthAnimator)
        animatorSet.start()
        resetSwipeCompleteListener?.invoke()
    }

    private fun moveButtonBack() {
        val positionAnimator = ValueAnimator.ofFloat(slidingButton.x, 0f)
        positionAnimator.interpolator = AccelerateDecelerateInterpolator()
        positionAnimator.addUpdateListener {
            val x = positionAnimator.animatedValue as Float
            slidingButton.x = x
        }
        val firstOptionAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
            firstOption, "alpha", 1F
        )
        val secondOptionAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
            secondOption, "alpha", 1F
        )
        positionAnimator.duration = 200
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(firstOptionAnimator, secondOptionAnimator, positionAnimator)
        animatorSet.start()
    }
}