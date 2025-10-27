package com.hfad.geoquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
import com.hfad.geoquiz.ui.theme.GeoQuizTheme

// Question - модель для хранения вопроса и правильного ответа
data class Question(
    val text: String,
    val answer: Boolean
)
//QuizState - состояние всего приложения:
//currentQuestionIndex - текущий номер вопроса (0, 1, 2...)
//userAnswers - список ответов пользователя (null если не отвечал)
//isAnswered - флаг, отвечен ли текущий вопрос
//showResults - флаг, показывать ли результаты

data class QuizState(
    val currentQuestionIndex: Int = 0,
    val userAnswers: List<Boolean?> = emptyList(),
    val isAnswered: Boolean = false,
    val showResults: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeoQuizTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GeoQuizApp()
                }
            }
        }
    }
}
//setContent - устанавливает Compose как UI
//GeoQuizTheme - применяет Material Design тему

@Composable
fun GeoQuizTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    GeoQuizTheme {
//        Greeting("Android")
//    }
//}