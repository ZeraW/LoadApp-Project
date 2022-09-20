package com.udacity

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.math.min

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(
    context,
    attrs,
    defStyleAttr) {
    //btn attribute
    private var defaultText: String = ""
    private var loadingText: String = ""
    private var btnColor = 0
    private var btnLoadingColor = 0
    private var textColor = 0
    private var pacManColor = 0

    //btn width and height
    private var mWidth:Float = 0f
    private var mHeight:Float = 0f

    //paints
    private val btnPaint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val textPaint = TextPaint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    //text
    private var buttonText = ""
    private lateinit var textRect: Rect

    //pacman
    private var pacManSize = 0f
    private val pacManRect = RectF()

    //anim
    private val animSet: AnimatorSet = AnimatorSet()
    private var currentPacManValue = 0f
    private val pacManAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            currentPacManValue = it.animatedValue as Float
            invalidate()
        }
    }
    private var currentBtnAnimValue = 0f
    private lateinit var btnAnim: ValueAnimator

    //btnState
    private var buttonState: ButtonState = ButtonState.Completed

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            btnColor =
                getColor(R.styleable.LoadingButton_btnColor, 0)
            btnLoadingColor =
                getColor(R.styleable.LoadingButton_btnLoadingColor, 0)
            defaultText =
                getText(R.styleable.LoadingButton_defaultText) as String
            textColor =
                getColor(R.styleable.LoadingButton_textColor, 0)
            loadingText =
                getText(R.styleable.LoadingButton_loadingText) as String
            pacManColor = getColor(R.styleable.LoadingButton_pacManColor, 0)

        }
        buttonText = defaultText
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = paddingLeft + paddingRight + suggestedMinimumWidth
        val w = resolveSizeAndState(
            minWidth,
            widthMeasureSpec,
            1
        )
        mWidth = w.toFloat()
        val h = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        mHeight = h.toFloat()
        setMeasuredDimension(w, h)
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        pacManSize = (min(w, h)) * 0.2f


        if (!::textRect.isInitialized) {
            textRect = Rect()
            textPaint.getTextBounds(loadingText, 0, loadingText.length, textRect)

            //align PacMan to the text
            val horCen = (textRect.right + textRect.width() + 16f)
            val verCen = (mHeight / 2f)
            pacManRect.set(
                horCen - pacManSize,
                verCen - pacManSize,
                horCen + pacManSize,
                verCen + pacManSize
            )
        }


        btnAnim = ValueAnimator.ofFloat(0f, mWidth).apply {
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                currentBtnAnimValue = it.animatedValue as Float
                invalidate()
            }
        }
        animSet.duration = 2000
        animSet.playTogether(pacManAnimator, btnAnim)

    }

    //draw
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { btnCanvas ->
            btnCanvas.apply {
                drawBtnColor()
                drawText()
                drawPacMan()
            }
        }
    }
    private fun Canvas.drawText() {
        textPaint.color = textColor
        drawText(
            buttonText,
            (mWidth / 2f),
            (mHeight / 2f) + 16f,
            textPaint
        )
    }
    private fun Canvas.drawBtnColor() {
        if(buttonState == ButtonState.Loading){
            drawDarkColor()
            removeLightColor()
        }else {
            drawColor(btnColor)
        }
    }
    private fun Canvas.drawDarkColor(){
        btnPaint.color= btnLoadingColor
        drawRect(
            0f,
            0f,
            mWidth,
            mHeight,
            btnPaint
        )
    }
    private fun Canvas.removeLightColor(){
        btnPaint.color= btnColor
        drawRect(
            currentBtnAnimValue,
            0f,
            mWidth,
            mHeight,
            btnPaint
        )

    }
    private fun Canvas.drawPacMan(){
        if(buttonState==ButtonState.Loading){
            btnPaint.color = pacManColor
            this.drawArc(
                pacManRect,
                0f,
                currentPacManValue,
                true,
                btnPaint
            )
        }

    }

    //action
    fun updateBtnState(state: ButtonState) {
        if (state != buttonState) {
            buttonState = state
            invalidate()
        }

        if (state == ButtonState.Loading) {
            // change btn text
            buttonText = loadingText
            // start anim
            animSet.start()
            //disable the btn during download
            this.isEnabled = false
        } else {
            // change btn text
            buttonText = defaultText
            // stop anim
            animSet.cancel()
            //enable the btn after the download complete
            this.isEnabled = true
        }
    }
}