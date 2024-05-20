package com.example.compasstestapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compasstestapp.data.ApiClient
import com.example.compasstestapp.presentation.theme.CompassTestAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val service = ApiClient.getApiService(applicationContext)
        super.onCreate(savedInstanceState)
        setContent {
            var res by remember { mutableStateOf("") }
            var every10 by remember { mutableStateOf(emptyList<String>()) }
            var wordCountList by remember { mutableStateOf(emptyList<String>()) }
            var isLoading by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()
            LaunchedEffect(res) {
                launch { every10 = res.getEvery10thCharacter() }
                launch { wordCountList = res.countWords()}
            }
            CompassTestAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (every10.isNotEmpty() && wordCountList.isNotEmpty()) isLoading = false
                        CompassTestScreen(every10, wordCountList) {
                            coroutineScope.launch(Dispatchers.IO) {
                                isLoading = true
                                val response = service.getAboutSection().awaitResponse().body()
                                response?.let {
                                    res = response.string()
                                }
                            }
                        }
                        AnimatedVisibility(visible = isLoading) {
                            Box(modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .size(100.dp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color = MaterialTheme.colorScheme.primary.copy(alpha = .7f)),
                                contentAlignment = Alignment.Center
                            ){
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompassTestScreen(every10thCharacter: List<String>, wordCount: List<String>, onButtonClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Button(onClick = onButtonClicked) {
            Text(text = "Get Compass")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            val scrollStateChars = rememberScrollState()
            val scrollStateWords = rememberScrollState()
            Column(modifier = Modifier
                .width(20.dp)
                .verticalScroll(scrollStateChars)) {
                every10thCharacter.forEach {
                    Text(text = it)
                }
            }
            Column(modifier = Modifier
                .verticalScroll(scrollStateWords)) {
                wordCount.forEach {
                    Text(text = it)
                }
            }
        }
    }
}

suspend fun String.getEvery10thCharacter(): List<String> {
    return withContext(Dispatchers.Default) {
        filterIndexed { index, _ ->
            (index + 1) % 10 == 0  // index plus one to start in base 1
        }
            .split("")
            .filterNot { it.isBlank() || it.isEmpty() }
    }
}

suspend fun String.countWords(): List<String> {
    return if (isEmpty()) listOf("") else withContext(Dispatchers.Default){
        var result = mutableListOf<String>()
        val wordList = trimIndent()
            .lowercase(Locale.getDefault())
            .split(" ")
        // Use a map to count the occurrences of each word
        val wordCountMap = mutableMapOf<String, Int>()
        wordList.forEach { word ->
            val count = wordCountMap.getOrDefault(word, 0)
            wordCountMap[word] = count + 1
        }
        wordCountMap.map {
            result.add("${it.key} -> ${it.value}")
        }
        result
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CompassTestAppTheme {

    }
}