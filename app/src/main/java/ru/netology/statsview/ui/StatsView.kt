package ru.netology.statsview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils.dp
import kotlin.math.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes,
) {
    // Пока у нас не было атрибутов в нашем StatsView, мы задавали значения в коде (тут)
    // Теперь же мы используем эти значения как дефолтные, если они не заданы в верстке
    private var textSize = dp(context, 20).toFloat()
    private var lineWidth = dp(context, 5)
    private var colors = emptyList<Int>()

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth.toFloat()).toInt()

            colors = listOf(
                getColor(R.styleable.StatsView_color1, generateRandomColor()),
                getColor(R.styleable.StatsView_color2, generateRandomColor()),
                getColor(R.styleable.StatsView_color3, generateRandomColor()),
                getColor(R.styleable.StatsView_color4, generateRandomColor()),
            )
        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()  // данный метод спровоцирует вызов функции onDraw()
        }
    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()

    // Кисть не рекомендуется создавать при вызове onDraw, лучше здесь
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    // Отдельная кисть для отрисовки текста всегда удобнее
    // Тут имеют значение уже другие параметры
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = this@StatsView.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        /* Надо убрать super.onSizeChanged(w, h, oldw, oldh), т.к.
        реализация по умолчанию ничего полезного не делает */
        radius = min(w, h) / 2F - lineWidth
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius,
        )
    }

    override fun onDraw(canvas: Canvas) {
        /* Уберем функцию super.onDraw(canvas), поскольку в ней ничего не происходит */

        // Просто черный круг
        //canvas.drawCircle(center.x, center.y, radius, paint)

        // Многоцветный круг с процентной отрисовкой случайными цветами (цветов столько, сколько аргументов передано)
        if (data.isEmpty()) {
            return
        }

        var startAngle = -90F
        data.forEachIndexed { index, datum ->
            val angle = 360F * datum
            paint.color = colors.getOrElse(index) { generateRandomColor() } // При отсутствии элемента будет случайный цвет
            canvas.drawArc(oval, startAngle, angle, false, paint)
            // Изменим стартовый угол, чтобы следующий кусочек дуги начать с конца ранее нарисованного
            startAngle += angle
        }

        canvas.drawText(
            "%.2f%%".format(data.sum() * 100), // Доля переводится в проценты и форматируется
            center.x,
            center.y + textPaint.textSize / 4, // смещаем низ текста чуть ниже центра
            textPaint
        )
    }

    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}
