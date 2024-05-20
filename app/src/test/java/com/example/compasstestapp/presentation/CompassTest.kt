package com.example.compasstestapp.presentation

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CompassTest {

    @Test
    fun `test getEvery10thCharacter with example string`() = runBlocking {
        val input = "This is a sample string to demonstrate getting every 10th character."
        val expected = listOf("r", "m", "g", "e", "h")
        val result = input.getEvery10thCharacter()
        assertEquals(expected, result)
    }

    @Test
    fun `test getEvery10thCharacter with short string`() = runBlocking {
        val input = "Short"
        val expected = emptyList<String>()
        val result = input.getEvery10thCharacter()
        assertEquals(expected, result)
    }

    @Test
    fun `test getEvery10thCharacter with exactly 10 characters`() = runBlocking {
        val input = "1234567890"
        val expected = listOf("0")
        val result = input.getEvery10thCharacter()
        assertEquals(expected, result)
    }

    @Test
    fun `test countWords with example text`() = runBlocking {
        val input = "This is a sample text. This text includes several words, some of which are repeated. This is to test the word count."
        val expected = listOf(
            "a -> 1", "are -> 1", "count. -> 1", "includes -> 1", "is -> 2",
        "of -> 1", "repeated. -> 1", "sample -> 1", "several -> 1", "test -> 1", "text -> 1", "text. -> 1",
        "the -> 1", "this -> 3","to -> 1", "which -> 1", "word -> 1", "words, -> 1", "some -> 1")
        val result = input.countWords()
        assertEquals(expected.sorted(), result.sorted())
    }

    @Test
    fun `test countWords with empty string`() = runBlocking {
        val input = ""
        val expected = listOf("")
        val result = input.countWords()
        assertEquals(expected, result)
    }

    @Test
    fun `test countWords with single word`() = runBlocking {
        val input = "word word"
        val expected = listOf("word -> 2")
        val result = input.countWords()
        assertEquals(expected, result)
    }
}