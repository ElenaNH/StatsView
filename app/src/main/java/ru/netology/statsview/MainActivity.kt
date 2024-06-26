package ru.netology.statsview

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        // ЗАПУСК анимации при помощи присвоения новых данных полю data
        val view = findViewById<StatsView>(R.id.statsView)
        view.data = listOf(
            500F,  //0F
            500F,  //0F
            500F, //1500F
            500F,
            //1000F  //Незаполненная часть
        )

/*        val textViewLabel = findViewById<TextView>(R.id.textViewLabel)
        val baseLabel = getString(R.string.animations_testing)

        // ЗАПУСК анимации, задаваемой файлом animation.xml
        // После запуска анимации можно также запустить слушателя этой анимации
        // при помощи добавления внутрь функции apply
        view.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.animation) //animation.xml
                .apply {
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(p0: Animation?) {
                            textViewLabel.text = "$baseLabel\nonAnimationStart"
                        }

                        override fun onAnimationEnd(p0: Animation?) {
                            textViewLabel.text = "$baseLabel\nonAnimationEnd"
                        }

                        override fun onAnimationRepeat(p0: Animation?) {
                            textViewLabel.text = "$baseLabel\nonAnimationRepeat"
                        }

                    })
                }
        )*/


    }
}
