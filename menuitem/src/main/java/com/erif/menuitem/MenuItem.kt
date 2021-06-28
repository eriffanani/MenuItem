package com.erif.menuitem

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat

class MenuItem : View {

    private lateinit var paintImage: Paint
    private lateinit var rectF: RectF
    private var colorMenu: Int = 0
    private lateinit var path: Path

    private var imageView: ImageView? = null
    private lateinit var iconRes: Drawable

    private lateinit var paintText: TextPaint
    private var boundText: Rect = Rect()
    private var menuTitle: String? = null

    private var shape: Int = SHAPE_RECTANGLE

    companion object {
        private const val menuBgSize = 55
        private const val SHAPE_RECTANGLE: Int = 0
        private const val cornerRadius = 5
    }

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(
        context: Context?, attrs: AttributeSet?
    ) : super(context, attrs) {
        init(attrs)
    }

    constructor(
        context: Context?, attrs: AttributeSet?, defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        setWillNotDraw(false)
        colorMenu = Color.CYAN
        val imgID = R.drawable.ic_shopping_cart_white
        iconRes = ContextCompat.getDrawable(context, imgID)
            ?: throw IllegalArgumentException("Cannot load drawable $imgID")
        paintImage = Paint(Paint.ANTI_ALIAS_FLAG)
        paintImage.color = colorMenu
        paintImage.style = Paint.Style.FILL
        paintImage.setShadowLayer(
            1.2f,
            0f,
            1.2f,
            ContextCompat.getColor(context, R.color.colorCardShadow)
        )
        path = Path()
        val bg = ContextCompat.getDrawable(context, R.drawable.background_menu)
        background = bg

        paintText = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paintText.color = Color.BLACK
        paintText.style = Paint.Style.FILL
        val textSize = 12.dp.toFloat()
        paintText.textSize = textSize
        paintText.textAlign = Paint.Align.CENTER
        /*val typeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resources.getFont(R.font.rubik)
        } else {
            ResourcesCompat.getFont(context, R.font.rubik)
        }
        paintText.typeface = typeface*/
        //paintText.typeface = Typeface.create(typeface, Typeface.BOLD)

        val padding = 5.dp
        setPadding(
            padding, padding, padding, padding
        )

        val minSize = menuBgSize.dp + (2 * padding)
        val minHeight = menuBgSize.dp + (3 * padding)

        minimumWidth = minSize
        minimumHeight = minHeight

        val center = this.width.toFloat() / 2

        val rectFLeft = center - (menuBgSize.dp / 2)
        val rectFTop = this.top + padding
        val rectFEnd = rectFLeft + menuBgSize.dp

        rectF = RectF(
            rectFLeft, // Left
            rectFTop.toFloat(), // Top
            rectFEnd, // Right
            menuBgSize.dp.toFloat() // Bottom
        )

        /*orientation = VERTICAL
        gravity = Gravity.CENTER*/

        val theme = context.theme
        if (theme != null) {
            val typedArray = theme.obtainStyledAttributes(
                attrs,
                R.styleable.MenuItem, 0, 0
            )
            try {
                menuTitle = typedArray.getString(R.styleable.MenuItem_title).toString()
                val attrID = typedArray.getResourceId(
                    R.styleable.MenuItem_icon,
                    R.drawable.ic_shopping_cart_white
                )
                iconRes = ContextCompat.getDrawable(context, attrID)
                    ?: throw IllegalArgumentException("Cannot load drawable $imgID")

                colorMenu = typedArray.getColor(R.styleable.MenuItem_background_color, colorMenu)
                paintImage.color = colorMenu

                shape = typedArray.getInteger(R.styleable.MenuItem_shape, shape)

                setWidth()
                createMenu()
            } finally {
                typedArray.recycle()
            }
        } else {
            Log.e("XMenu", "Theme null")
        }
    }

    private fun createMenu() {
        imageView = ImageView(context)
        imageView?.setImageDrawable(iconRes)
    }

    /**
     * On Measure
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )
    }

    /**
     * On Draw
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val x = width / 2
        val y = this.top
        canvas?.save()
        canvas?.translate(x.toFloat(), y.toFloat())

        if (shape == SHAPE_RECTANGLE) {
            path.addRoundRect(
                rectF,
                cornerRadius.dp.toFloat(),
                cornerRadius.dp.toFloat(),
                Path.Direction.CW
            )
            canvas?.drawPath(path, paintImage)
        } else {
            canvas?.drawCircle(
                0f,
                (this.paddingTop.toFloat() + menuBgSize.dp.toFloat()) / 2,
                menuBgSize.dp.toFloat() / 2,
                paintImage
            )
        }

        canvas?.restore()
        imageView?.let {
            val drawable = it.drawable
            val mLeft = (this.width.toFloat() / 2) - (drawable.intrinsicWidth.toFloat() / 2)
            val halfBg =
                (this.top.toFloat() + this.paddingTop.toFloat() + menuBgSize.dp.toFloat()) / 2
            val halfImg = drawable.intrinsicHeight.toFloat() / 2
            val mTop = halfBg - halfImg
            val mRight = drawable.intrinsicWidth + mLeft
            val mBottom = drawable.intrinsicHeight + mTop
            drawable.setBounds(mLeft.toInt(), mTop.toInt(), mRight.toInt(), mBottom.toInt())
            drawable.draw(canvas ?: return)
        }

        val mTitle = menuTitle ?: "Menu Title"
        val xText = this.width.toFloat() / 2
        val yText =
            this.top.toFloat() + menuBgSize.dp.toFloat() + (3.5f * this.paddingTop.toFloat())
        canvas?.drawText(mTitle, xText, yText, paintText)
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = kotlin.math.min(result, specSize)
            }
        }
        if (result < desiredSize) {
            Log.e("ChartView", "The view is too small, the content might get cut")
        }
        return result
    }

    /**
     * Set Menu Title
     */
    fun setTitle(title: String) {
        this.menuTitle = title
        setWidth()
        invalidate()
    }

    /**
     * Set Icon
     */
    fun setIcon(iconID: Int) {
        val newIconRes = ContextCompat.getDrawable(context, iconID)
            ?: throw IllegalArgumentException("Cannot load drawable $iconID")
        this.iconRes = newIconRes
        imageView?.setImageDrawable(newIconRes)
    }

    private fun setWidth() {
        val mTitle = menuTitle ?: "Menu Title"
        paintText.getTextBounds(menuTitle, 0, mTitle.length, boundText)
        val textWidth = boundText.width() + (2 * paddingStart)
        if (this.minimumWidth < textWidth)
            minimumWidth = textWidth
    }

    /**
     * DP
     */
    private val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

}