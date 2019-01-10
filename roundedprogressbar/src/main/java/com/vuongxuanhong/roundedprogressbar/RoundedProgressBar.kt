package com.vuongxuanhong.roundedprogressbar

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.v4.content.res.ResourcesCompat
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * TODO: document your custom view class.
 */
class RoundedProgressBar : View {

    private var rimPaint: Paint? = null
    private var rimOval: RectF? = null

    private var innerContourPaint: Paint? = null
    private var innerOval: RectF? = null

    private var outerContourPaint: Paint? = null
    private var outerOval: RectF? = null

    private var barPaint: Paint? = null
    private var barShader: Shader? = null
    private var barOval: RectF? = null
    private var barPercent: Float = 0f

    private var fillPaint: Paint? = null
    private var fillOval: RectF? = null

    private var textPaint: TextPaint? = null
    private var textWidth: Float = 0f
    private var textHeight: Float = 0f
    private var textBound: RectF? = null
    private var _text: String? = "0"
        set(value) {
            field = value
            invalidateTextPaintAndMeasurements()
        }
    private var _textColor: Int = Color.BLACK
        set(value) {
            field = value
            invalidateTextPaintAndMeasurements()
        }
    private var _textSize: Float = 10f
        set(value) {
            field = value
            invalidateTextPaintAndMeasurements()
        }
    private var _textFont: Int = R.font.opensans_regular
        set(value) {
            field = value
            invalidateTextPaintAndMeasurements()

            invalidate()
        }

    private var _rimWidth: Float = 10f
        set(value) {
            field = value
            invalidateRim()
        }
    private var _rimColor: Int = Color.RED
        set(value) {
            field = value
            invalidateRim()
        }
    private var _fillColor: Int = Color.RED
        set(value) {
            field = value
            invalidateFill()
        }
    private var _barWidth: Float = 8f
        set(value) {
            field = value
            invalidateBar()
        }
    private var _barColor: Int = Color.BLUE
        set(value) {
            field = value
            invalidateBar()
        }
    private var _barEndColor: Int = Color.BLUE
        set(value) {
            field = value
            invalidateBar()
        }
    private var _innerContourWidth: Float = 5f
        set(value) {
            field = value
            invalidateInnerContour()
        }
    private var _innerContourColor: Int = Color.CYAN
        set(value) {
            field = value
            invalidateInnerContour()
        }
    private var _outerContourWidth: Float = 5f
        set(value) {
            field = value
            invalidateOuterContour()
        }
    private var _outerContourColor: Int = Color.CYAN
        set(value) {
            field = value
            invalidateOuterContour()
        }
    private var _value: Float = 5f
        set(value) {
            field = value
            barPercent = if (maxValue == 0f) 100f else (value * 100f / maxValue)
            invalidateBar()

            invalidate()
        }
    private var _maxValue: Float = 100f
        set(value) {
            field = value
            barPercent = if (maxValue == 0f) 100f else value * 100f / maxValue
            invalidateBar()
        }

    var text: String?
        get() = _text
        set(value) {
            _text = value
        }
    var textColor: Int
        get() = _textColor
        set(value) {
            _textColor = value
        }
    var textSize: Float
        get() = _textSize
        set(value) {
            _textSize = value
        }
    var textFont: Int
        get() = _textFont
        set(value) {
            _textFont = value
        }
    var rimWidth: Float
        get() = _rimWidth
        set(value) {
            _rimWidth = value
        }
    var rimColor: Int
        get() = _rimColor
        set(value) {
            _rimColor = value
        }
    var fillColor: Int
        get() = _fillColor
        set(value) {
            _fillColor = value
        }
    var barWidth: Float
        get() = _barWidth
        set(value) {
            _barWidth = Math.min(value, rimWidth)
        }
    var barColor: Int
        get() = _barColor
        set(value) {
            _barColor = value
        }
    var barEndColor: Int
        get() = _barEndColor
        set(value) {
            _barEndColor = value
        }
    var innerContourWidth: Float
        get() = _innerContourWidth
        set(value) {
            _innerContourWidth = value
        }
    var innerContourColor: Int
        get() = _innerContourColor
        set(value) {
            _innerContourColor = value
        }
    var outerContourWidth: Float
        get() = _outerContourWidth
        set(value) {
            _outerContourWidth = value
        }
    var outerContourColor: Int
        get() = _outerContourColor
        set(value) {
            _outerContourColor = value
        }
    var value: Float
        get() = _value
        set(value) {
            _value = value
            onProgressChangedListener?.onProgressChanged(value)
        }
    var valueWithAnimation: Float
        get() = _value
        set(value) {
            val objectAnimator = ObjectAnimator.ofFloat(this, "progress", _value, value)
            objectAnimator.duration = 1000
            objectAnimator.interpolator = LinearInterpolator()
            objectAnimator.start()
        }
    var maxValue: Float
        get() = _maxValue
        set(value) {
            _maxValue = value
        }

    private fun setProgress(newValue: Float) {
        value = newValue
    }

    private var mLayoutWidth = 0
    private var mLayoutHeight = 0

    private var onProgressChangedListener: OnProgressChangedListener? = null


    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.RoundedProgressBar, defStyle, 0)

        _text = a.getString(R.styleable.RoundedProgressBar_rpb_text)
        _textColor = a.getColor(R.styleable.RoundedProgressBar_rpb_textColor, textColor)
        _textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.RoundedProgressBar_rpb_textSize, textSize), resources.displayMetrics)
        _textFont = a.getResourceId(R.styleable.RoundedProgressBar_rpb_textFont, 0)

        _rimWidth = a.getDimension(R.styleable.RoundedProgressBar_rpb_rimWidth, rimWidth)
        _rimColor = a.getColor(R.styleable.RoundedProgressBar_rpb_rimColor, rimColor)

        _fillColor = a.getColor(R.styleable.RoundedProgressBar_rpb_fillColor, fillColor)

        _barWidth = a.getDimension(R.styleable.RoundedProgressBar_rpb_barWidth, barWidth)
        _barColor = a.getColor(R.styleable.RoundedProgressBar_rpb_barColor, barColor)
        _barEndColor = a.getColor(R.styleable.RoundedProgressBar_rpb_barEndColor, barEndColor)

        _innerContourWidth = a.getDimension(R.styleable.RoundedProgressBar_rpb_innerContourWidth, innerContourWidth)
        _innerContourColor = a.getColor(R.styleable.RoundedProgressBar_rpb_innerContourColor, innerContourColor)

        _outerContourWidth = a.getDimension(R.styleable.RoundedProgressBar_rpb_outerContourWidth, outerContourWidth)
        _outerContourColor = a.getColor(R.styleable.RoundedProgressBar_rpb_outerContourColor, outerContourColor)

        _value = a.getFloat(R.styleable.RoundedProgressBar_rpb_value, value)
        _maxValue = a.getFloat(R.styleable.RoundedProgressBar_rpb_maxValue, maxValue)
        barPercent = if (_maxValue == 0f) 100f else _value * 100f / _maxValue

        a.recycle()

        // Update TextPaint and text measurements from attributes

        initPaints()

        if (!isInEditMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
            }
        }
    }

    private fun initPaints() {
        barPaint = Paint().apply {
            isAntiAlias = true
            strokeWidth = barWidth
            strokeCap = Paint.Cap.ROUND
            color = barColor
            style = Paint.Style.STROKE
        }

        rimPaint = Paint().apply {
            isAntiAlias = true
            strokeWidth = rimWidth
            strokeCap = Paint.Cap.ROUND
            color = rimColor
            style = Paint.Style.STROKE
        }

        fillPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = fillColor
        }

        innerContourPaint = Paint().apply {
            isAntiAlias = true
            strokeWidth = innerContourWidth
            strokeCap = Paint.Cap.ROUND
            color = innerContourColor
            style = Paint.Style.FILL_AND_STROKE
        }

        outerContourPaint = Paint().apply {
            isAntiAlias = true
            strokeWidth = outerContourWidth
            strokeCap = Paint.Cap.ROUND
            color = outerContourColor
            style = Paint.Style.FILL_AND_STROKE
        }

        // Set up a default TextPaint object
        textPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER
            textSize = this@RoundedProgressBar.textSize
            color = textColor
            typeface = Typefaces.get(context, textFont)
            textWidth = measureText(text)
            textHeight = fontMetrics.bottom
        }
    }

    private fun invalidateRim() {
        val contentWidth = mLayoutWidth - paddingLeft - paddingRight
        val contentHeight = mLayoutHeight - paddingTop - paddingBottom

        rimOval = RectF(
                paddingLeft + outerContourWidth + rimWidth / 2,
                paddingTop + outerContourWidth + rimWidth / 2,
                paddingLeft + contentWidth - outerContourWidth - rimWidth / 2,
                paddingTop + contentHeight - outerContourWidth - rimWidth / 2
        )
    }

    private fun invalidateInnerContour() {
        val contentWidth = mLayoutWidth - paddingLeft - paddingRight
        val contentHeight = mLayoutHeight - paddingTop - paddingBottom

        innerOval = RectF(
                paddingLeft + outerContourWidth + rimWidth + innerContourWidth / 2,
                paddingTop + outerContourWidth + rimWidth + innerContourWidth / 2,
                paddingLeft + contentWidth - outerContourWidth - rimWidth - innerContourWidth / 2,
                paddingTop + contentHeight - outerContourWidth - rimWidth - innerContourWidth / 2
        )
    }

    private fun invalidateOuterContour() {
        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        outerOval = RectF(
                paddingLeft + outerContourWidth / 2,
                paddingTop + outerContourWidth / 2,
                paddingLeft + contentWidth - outerContourWidth / 2,
                paddingTop + contentHeight - outerContourWidth / 2
        )
    }

    private fun invalidateBar() {
        val contentWidth = mLayoutWidth - paddingLeft - paddingRight
        val contentHeight = mLayoutHeight - paddingTop - paddingBottom

        barOval = RectF(
                paddingLeft + outerContourWidth + rimWidth / 2,
                paddingTop + outerContourWidth + rimWidth / 2,
                paddingLeft + contentWidth - outerContourWidth - rimWidth / 2,
                paddingTop + contentHeight - outerContourWidth - rimWidth / 2
        )
                .apply {
                    barShader = LinearGradient(left, top, right, bottom, barColor, barEndColor, Shader.TileMode.MIRROR)
                }
    }

    private fun invalidateFill() {
        val contentWidth = mLayoutWidth - paddingLeft - paddingRight
        val contentHeight = mLayoutHeight - paddingTop - paddingBottom

        fillOval = RectF(
                paddingLeft + outerContourWidth + rimWidth + innerContourWidth,
                paddingTop + outerContourWidth + rimWidth + innerContourWidth,
                paddingLeft + contentWidth - outerContourWidth - rimWidth - innerContourWidth,
                paddingTop + contentHeight - outerContourWidth - rimWidth - innerContourWidth
        )
    }

    private fun invalidateTextBound() {
        val contentWidth = mLayoutWidth - paddingLeft - paddingRight
        val contentHeight = mLayoutHeight - paddingTop - paddingBottom

        textBound = RectF(
                paddingLeft + outerContourWidth + rimWidth + innerContourWidth,
                paddingTop + outerContourWidth + rimWidth + innerContourWidth,
                paddingLeft + contentWidth - outerContourWidth - rimWidth - innerContourWidth,
                paddingTop + contentHeight - outerContourWidth - rimWidth - innerContourWidth
        )
    }

    private fun invalidateTextPaintAndMeasurements() {
        if (text == null) return

        textPaint?.apply {
            textSize = textSize
            color = textColor
            typeface = Typefaces.get(context, textFont)
            textWidth = measureText(text)
            textHeight = fontMetrics.bottom
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val size: Int
        val width = measuredWidth
        val height = measuredHeight
        val widthWithoutPadding = width - paddingLeft - paddingRight
        val heightWithoutPadding = height - paddingTop - paddingBottom

        // force size of this view is square
        // Finally we have some simple logic that calculates the size of the view
        // and calls setMeasuredDimension() to set that size.
        // Before we compare the width and height of the view, we remove the padding,
        // and when we set the dimension we add it back again. Now the actual content
        // of the view will be square, but, depending on the padding, the total dimensions
        // of the view might not be.
        size = Math.min(widthWithoutPadding, heightWithoutPadding)

        setMeasuredDimension(size + paddingLeft + paddingRight, size + paddingTop + paddingBottom);
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // this method is call after measure phase, so size of the view is determined.
        mLayoutWidth = width
        mLayoutHeight = height

        invalidateOuterContour()
        invalidateRim()
        invalidateBar()
        invalidateInnerContour()
        invalidateFill()
        invalidateTextBound()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        outerContourPaint?.apply {
            canvas.drawArc(outerOval, 0f, 360f, false, this)
        }

        innerContourPaint?.apply {
            canvas.drawArc(innerOval, 0f, 360f, false, this)
        }

        rimPaint?.apply {
            canvas.drawArc(rimOval, 0f, 360f, false, this)
        }

        barPaint?.apply {
            shader = barShader
            Log.d("RoundedProgressBar", "barpercent = $barPercent")
            canvas.drawArc(barOval, 0f, barPercent * 3.6f, false, this)
        }

        fillPaint?.apply {
            canvas.drawArc(fillOval, 0f, 360f, false, this)
        }

        textPaint?.apply {
            if (text != null && text!!.isNotEmpty()) {
                canvas.drawText(text, textBound!!.centerX(), textBound!!.centerY() - ((textPaint!!.descent() + textPaint!!.ascent()) / 2), textPaint)
            }
        }

    }

    interface OnProgressChangedListener {
        fun onProgressChanged(value: Float)
    }
}
