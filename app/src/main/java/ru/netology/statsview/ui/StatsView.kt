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

    // Прозрачный цвет (можно совсем ноль или чуть гуще)
    private val transparentColor = 0x44CCCCCC.toInt()  //0x01CCCCCC.toInt()
    private val undefinedColor = -1
    private var firstColor: Int = undefinedColor
    private var firstAngle: Float = -1F

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth.toFloat()).toInt()

            colors = listOf(
                getColor(R.styleable.StatsView_color1, generateRandomColor()),
                getColor(R.styleable.StatsView_color2, generateRandomColor()),
                getColor(R.styleable.StatsView_color3, generateRandomColor()),
                getColor(R.styleable.StatsView_color4, generateRandomColor()),
                transparentColor
            )

            // Если не задать это при инициализации, будет только полкруга
            firstColor = colors[0] // При инициализации уверены, что дуга ненулевая
            firstAngle = 1F // При инициализации знаем, что дуга ненулевая

        }
    }

    //var data: List<Float> = emptyList()
    var data: List<Float> = listOf(  // Решили по умолчанию делать непустой список
        0.25F,
        0.25F,
        0.25F,
        0.25F,
        0F  //Незаполненная часть
    )
        set(value) {
            // У нас 4 цвета; если передаем пять углов, то последний - пустая дуга
            // Если меньше 4 цветов - то это нулевые углы, включая и пустой нулевой угол
            // Шестое и остальные числа игнорируем
            val revisedValue = List(5) {
                value.getOrElse(it) { 0F }
            }
            revisedValue.forEachIndexed { index, datum ->
                if (firstColor == undefinedColor)
                    if (datum != 0F) {
                        firstColor = colors.getOrElse(index) { undefinedColor }
                        firstAngle = datum
                    }
            }
            if (firstColor == undefinedColor) { // Не нашли ненулевых дуг
                firstColor = transparentColor
                firstAngle = 0F
            }

            field = calcListProportion(revisedValue)
            invalidate()  // данный метод спровоцирует вызов функции onDraw()
        }

    private val totalDataProportion: Float
        get() = if (data.isEmpty()) 0F else data.sum() - data.last()

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

        if (data.isEmpty()) {
            return
        }

        // Просто незаполненный круг - имеет смысл при наличии незаполненной части и при анимации
        // (например, если захотим, чтобы он был полупрозрачным, то это подойдет)
        paint.color = transparentColor
        canvas.drawCircle(center.x, center.y, radius, paint)

        // Многоцветный круг с процентной отрисовкой разными цветами, взятыми из макета
        // либо случайных (цветов столько, сколько параметров в макете, а также прозрачный)
        if (data.isEmpty()) {
            return
        }

        var startAngle = -90F
        data.forEachIndexed { index, datum ->
            val angle = 360F * datum
            paint.color =
                colors.getOrElse(index) { generateRandomColor() } // При отсутствии элемента будет случайный цвет
            canvas.drawArc(oval, startAngle, angle, false, paint)
            // Изменим стартовый угол, чтобы следующий кусочек дуги начать с конца ранее нарисованного
            startAngle += angle
        }


        // Dot - Подъем начала первой дуги над наслоившейся последней дугой
        // Мне не нравится идея закрашивать наслоение кружочком (= точкой)
        // Уж лучше малой частью первой дуги и ее же цветом (минимум от 1 градуса и полдуги)
// TODO - вдруг кто-то догадается делать нулевую первую дугу - вторую будем наслаивать???
        startAngle = -90F  // Восстанавливаем только верхнее наслоение
        val angle = listOf(1F, 360F * 0.5F * firstAngle).min()
        paint.color = firstColor
        canvas.drawArc(oval, startAngle, angle, false, paint)

        canvas.drawText(
            "%.2f%%".format(totalDataProportion * 100), // Доля переводится в проценты и форматируется
            center.x,
            center.y + textPaint.textSize / 4, // смещаем низ текста чуть ниже центра
            textPaint
        )
    }

    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
    private fun calcListProportion(inputList: List<Float>): List<Float> {
        val listSum = inputList.sum()
        val zeroList = List(inputList.count()) { 0F }
        return if (listSum == 0F) zeroList //listOf(0F, 0F, 0F, 0F, 0F)
        else inputList.map { (it.toFloat() / listSum) }
    }
}
