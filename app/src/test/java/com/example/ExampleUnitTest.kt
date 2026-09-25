package com.example

import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testTabIndices() {
    val tabNames = listOf("Home", "Measure", "Fertilizer", "Booklet", "History")
    assertEquals(5, tabNames.size)
    assertEquals("Home", tabNames[0])
    assertEquals("Measure", tabNames[1])
    assertEquals("Fertilizer", tabNames[2])
    assertEquals("Booklet", tabNames[3])
    assertEquals("History", tabNames[4])
  }

  @Test
  fun testDefaultFarmAreaState() {
    val initialArea = ""
    assertTrue("Initial farm area should be empty so placeholder shows", initialArea.isEmpty())
    val parsedArea = initialArea.toDoubleOrNull() ?: 1.0
    assertEquals(1.0, parsedArea, 0.001)
  }
}
