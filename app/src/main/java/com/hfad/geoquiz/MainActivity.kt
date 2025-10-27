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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue

import androidx.compose.ui.tooling.preview.Preview
import com.hfad.geoquiz.ui.theme.GeoQuizTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

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

// ViewModel - Бизнес логика
class QuizViewModel : ViewModel() {

    private val questions = listOf(
        Question("Canberra is the capital of Australia.", true),
        Question("The Pacific Ocean is larger than the Atlantic Ocean.", true),
        Question("The Suez Canal connects the Red Sea and the Indian Ocean.", false),
        Question("The source of the Nile River is in Egypt.", false),
        Question("The Amazon River is the longest river in the Americas.", true),
        Question("Lake Baikal is the world's oldest and deepest freshwater lake.", true)
    )

    private var state by mutableStateOf(QuizState())

    init {
        state = state.copy(
            userAnswers = List(questions.size) { null }
        )
    }

    fun getState(): QuizState = state

    fun onAnswer(answer: Boolean) {
        val currentIndex = state.currentQuestionIndex
        val updatedAnswers = state.userAnswers.toMutableList().apply {
            this[currentIndex] = answer
        }

        state = state.copy(
            userAnswers = updatedAnswers,
            isAnswered = true
        )
    }

    fun onNext() {
        val nextIndex = state.currentQuestionIndex + 1

        if (nextIndex >= questions.size) {
            state = state.copy(
                showResults = true
            )
        } else {
            state = state.copy(
                currentQuestionIndex = nextIndex,
                isAnswered = false
            )
        }
    }

    fun onRestart() {
        state = QuizState(
            userAnswers = List(questions.size) { null }
        )
    }

    fun getCurrentQuestion(): Question {
        return questions[state.currentQuestionIndex]
    }

    fun getQuestions(): List<Question> = questions

    fun calculateScore(): Int {
        return state.userAnswers.mapIndexed { index, answer ->
            if (answer == questions[index].answer) 1 else 0
        }.sum()
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

@Composable
fun GeoQuizApp() {
    val viewModel: QuizViewModel = viewModel()
    val state = viewModel.getState()

    CompositionLocalProvider(
        // передает состояние и функции в дерево компонентов
        LocalQuizState provides QuizStateHolder(
            state = state,
            onAnswer = viewModel::onAnswer,
            onNext = viewModel::onNext,
            onRestart = viewModel::onRestart,
            getCurrentQuestion = viewModel::getCurrentQuestion,
            getQuestions = viewModel::getQuestions,
            calculateScore = viewModel::calculateScore
        )
    ) {
        QuizScreen()
    }
//Создает ViewModel
//Обертывает приложение в CompositionLocalProvider
//Делает состояние доступным для всех дочерних компонентов
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    GeoQuizTheme {
//        Greeting("Android")
//    }
//}