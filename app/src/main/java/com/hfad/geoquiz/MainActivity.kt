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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.hfad.geoquiz.ui.theme.GeoQuizTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.window.DialogProperties


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

//При изменении любого поля в QuizState происходит рекомпозиция зависимых компонентов
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
        //mutableStateOf() создает observable состояние, которое отслеживается Compose
    var state by mutableStateOf(QuizState())
        private set

    init {
        state = state.copy(
            userAnswers = List(questions.size) { null }
        )
    }


    fun onAnswer(answer: Boolean) {
        val currentIndex = state.currentQuestionIndex
        val updatedAnswers = state.userAnswers.toMutableList().apply {
            this[currentIndex] = answer
        }

        state = state.copy( //  ИЗМЕНЕНИЕ STATE - РЕКОМПОЗИЦИЯ
            userAnswers = updatedAnswers,
            isAnswered = true
        )
        // ЕСЛИ ЭТО ПОСЛЕДНИЙ ВОПРОС - сразу показываем результаты
        if (currentIndex == questions.size - 1) {
            state = state.copy(
                showResults = true
            )
        }

    }

    fun onNext() {
        val nextIndex = state.currentQuestionIndex + 1

        if (nextIndex >= questions.size) {
            state = state.copy( //  ИЗМЕНЕНИЕ STATE - РЕКОМПОЗИЦИЯ
                showResults = true
            )
        } else {
            state = state.copy( //  ИЗМЕНЕНИЕ STATE - РЕКОМПОЗИЦИЯ
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
    MaterialTheme(
        content = content
    )
}

@Composable
fun GeoQuizApp() {
    val viewModel: QuizViewModel = viewModel()
    val state = viewModel.state

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
//ОСНОВНОЙ ЭКРАН
@Composable
fun QuizScreen() {
    val quizState = LocalQuizState.current
    val currentQuestion = quizState.getCurrentQuestion()
    val questions = quizState.getQuestions()
    val score = quizState.calculateScore()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ОСНОВНОЙ КОНТЕНТ - показываем только если НЕ показываем результаты
        if (!quizState.state.showResults) { // РЕКОМПОЗИЦИЯ ПРИ ИЗМЕНЕНИИ showResults
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Прогресс
                Text(
                    text = "Question ${quizState.state.currentQuestionIndex + 1} of ${questions.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Вопрос
                Text(
                    text = currentQuestion.text,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(64.dp))

                // Кнопки ответов
                AnswerButtons()

                Spacer(modifier = Modifier.height(64.dp))

                // Кнопка Next
                NextButton()
            }
        }

        // Всплывающая панель с результатами
        if (quizState.state.showResults) { // РЕКОМПОЗИЦИЯ ПРИ ИЗМЕНЕНИИ showResults
            ResultsDialog(score = score, totalQuestions = questions.size)
        }
    }
}
@Composable
fun AnswerButtons() {
    val quizState = LocalQuizState.current

    // Кнопки становятся невидимыми после ответа
    if (!quizState.state.isAnswered) { // РЕКОМПОЗИЦИЯ ПРИ ИЗМЕНЕНИИ isAnswered
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AnswerButton(
                text = "True",
                onClick = { quizState.onAnswer(true) },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            AnswerButton(
                text = "False",
                onClick = { quizState.onAnswer(false) },
                modifier = Modifier.weight(1f)
            )
        }
    }
    //Показывает кнопки ТОЛЬКО когда isAnswered = false
    //При нажатии вызывает onAnswer и устанавливает isAnswered = true
    //Это делает кнопки невидимыми после ответа
}

@Composable
fun AnswerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF8B00FF)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun NextButton() {
    val quizState = LocalQuizState.current
    val questions = quizState.getQuestions()

    // Кнопка Next видна только после ответа и не на последнем вопросе
    if (quizState.state.isAnswered &&   // РЕКОМПОЗИЦИЯ ПРИ ИЗМЕНЕНИИ isAnswered ИЛИ currentQuestionInd
        quizState.state.currentQuestionIndex < questions.size - 1) {
        Button(
            onClick = quizState.onNext,
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B00FF) // Тот же фиолетовый цвет
            )
        ) {
            Text(
                text = "Next Question",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
//Показывает кнопку ТОЛЬКО когда:
//Пользователь ответил (isAnswered = true)
//Это НЕ последний вопрос
//На последнем вопросе кнопка Next не показывается

@Composable
fun ResultsDialog(
    score: Int,
    totalQuestions: Int
) {
    val quizState = LocalQuizState.current
    val percentage = if (totalQuestions > 0) (score * 100 / totalQuestions) else 0

    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = "Quiz Results",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    text = "Your score: $score/$totalQuestions",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Percentage: ${percentage}%",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = when {
                        percentage >= 80 -> "Excellent!"
                        percentage >= 60 -> "Good job!"
                        else -> "Keep practicing!"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = quizState.onRestart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B00FF) // Тот же фиолетовый цвет
                )
            ) {
                Text("Take Quiz Again?")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}
//Показывает счет и процент правильных ответов
//Кнопка "Take Quiz Again?" сбрасывает тест через onRestart()

// Поток данных и рекомпозиция
//Изменение состояния → Рекомпозиция зависимых компонентов → Обновление UI
//        ↑
//Пользовательское действие (нажатие кнопки)



//State определяет, что показывать
//Рекомпозиция автоматически обновляет UI при изменении state