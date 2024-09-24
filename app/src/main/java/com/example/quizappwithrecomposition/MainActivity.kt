package com.example.quizappwithrecomposition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quizappwithrecomposition.ui.theme.QuizAppWithRecompositionTheme

data class Flashcard(val question: String, val answer: String)

val flashcards = listOf(
    Flashcard("What is the capital of France?", "Paris"),
    Flashcard("What is 2 + 2?", "4"),
    Flashcard("What is 7 - 4", "3"),
    Flashcard("What is the capital of China?", "Beijing"),
    Flashcard("What is 5 * 5?", "25"),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizAppWithRecompositionTheme {
                QuizApp()
            }
        }
    }
}

@Composable
fun QuizApp() {
    val currentQuestionIndex = remember { mutableStateOf(0) }
    val userInput = remember { mutableStateOf("") }
    val isQuizComplete = remember { mutableStateOf(false) }
    val score = remember { mutableStateOf(0) }
    val incorrectAttempts = remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    val currentQuestion = remember { mutableStateOf("") }

    // When snack bar is set to true, snack bar will show up once.
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false
        }
    }

    if (currentQuestionIndex.value < flashcards.size) {
        currentQuestion.value = flashcards[currentQuestionIndex.value].question
    } else {
        currentQuestion.value = "Quiz Complete"
    }

    if (!isQuizComplete.value) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Score: ${score.value}",
                    modifier = Modifier.padding(16.dp)
                )

                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text(
                        text = currentQuestion.value,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                TextField(
                    value = userInput.value,
                    onValueChange = { userInput.value = it },
                    label = { Text("Your answer") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )

                Button(
                    onClick = {
                        if (userInput.value == flashcards[currentQuestionIndex.value].answer) {
                            score.value++
                            snackbarMessage = "Correct!"
                            currentQuestionIndex.value++
                            if (currentQuestionIndex.value == flashcards.size) {
                                isQuizComplete.value = true
                            }
                        } else {
                            incorrectAttempts.value++
                            snackbarMessage = "Incorrect! Attempts: ${incorrectAttempts.value}/3"
                            if (incorrectAttempts.value >= 3) {
                                incorrectAttempts.value = 0
                                currentQuestionIndex.value++
                                if (currentQuestionIndex.value == flashcards.size) {
                                    isQuizComplete.value = true
                                }
                            }
                        }
                        showSnackbar = true
                        userInput.value = ""

                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Submit Answer")
                }
            }
        }
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Quiz is complete! Your score: ${score.value}")
            Button(onClick = {
                currentQuestionIndex.value = 0
                score.value = 0
                userInput.value = ""
                isQuizComplete.value = false
                incorrectAttempts.value = 0
            }) {
                Text("Restart Quiz")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizAppPreview() {
    QuizAppWithRecompositionTheme {
        QuizApp()
    }
}