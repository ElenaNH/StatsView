package ru.netology.statsview

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Решили, что начальные данные будем заполнять в самом элементе
        /*val view = findViewById<StatsView>(R.id.statsView)
        view.data = listOf(
            500F,  //0F
            500F,  //0F
            500F, //1500F
            500F,
            //1000F  //Незаполненная часть
        )*/

        val root = findViewById<ViewGroup>(R.id.root)
        // Подготовка сцены
        val scene = Scene.getSceneForLayout(root, R.layout.end_scene, this)
        // Обработчик нажатия на кнопку (стартует анимацию)
        findViewById<View>(R.id.goButton).setOnClickListener {
            TransitionManager.go(scene)
        }

    }
}
