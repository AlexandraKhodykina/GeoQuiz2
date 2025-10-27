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
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.compose.ui.tooling.preview.Preview
import com.hfad.geoquiz.ui.theme.GeoQuizTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewmodel.compose.viewModel

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

// Управление состоянием
class QuizStateHolder(
    val state: QuizState,
    val onAnswer: (Boolean) -> Unit,
    val onNext: () -> Unit,
    val onRestart: () -> Unit,
    val getCurrentQuestion: () -> Question,
    val getQuestions: () -> List<Question>,
    val calculateScore: () -> Int
)
//QuizStateHolder - контейнер для состояния и функций
//LocalQuizState - позволяет передавать состояние глубоко в дерево компонентов без явной передачи через параметры

val LocalQuizState = staticCompositionLocalOf<QuizStateHolder> {
    error("QuizStateHolder not provided")
}

//Бизнес логика
class QuizViewModel : ViewModel() {
    private val questions = listOf(
        // список вопросов и ответов
    )
    private var state by mutableStateOf(QuizState())
    fun onAnswer(answer: Boolean) {
        // Обновляет ответы пользователя и помечает вопрос как отвеченный
    }
    fun onNext() {
        // Переходит к следующему вопросу или показывает результаты
    }
    fun onRestart() {
        // Сбрасывает тест к начальному состоянию
    }
    fun calculateScore(): Int {
        // Считает количество правильных ответов
    }
}
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