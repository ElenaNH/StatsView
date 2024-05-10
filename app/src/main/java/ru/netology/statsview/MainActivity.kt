package ru.netology.statsview

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Scene
import androidx.transition.TransitionManager
import ru.netology.statsview.ui.StatsView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Нижеследующего кода нет в примере для Layouttransition (включая и setOnApplyWindowInsetsListener)
        // Но без этого кода приложение падает
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val root = findViewById<ViewGroup>(R.id.root)
        // Настройка свойств для более интересной анимации
        root.layoutTransition = LayoutTransition().apply {
            setDuration(2_000)
            setInterpolator(LayoutTransition.CHANGE_APPEARING, BounceInterpolator())

        }

        val buttonGo = findViewById<View>(R.id.buttonGo)

        // Обработчик нажатия на кнопку (запускает добавление, сопровождающееся анимацией)
        buttonGo.setOnClickListener {
            //layoutInflater.inflate(R.layout.stats_view, root, true) // true - добавляем в группу сразу после создания
            val view = layoutInflater.inflate(R.layout.stats_view, root, false) // false - добавляем в группу отдельно ниже
            root.addView(view, 0) // Добавляем созданный выше view на начальную позицию
        }

    }
}

