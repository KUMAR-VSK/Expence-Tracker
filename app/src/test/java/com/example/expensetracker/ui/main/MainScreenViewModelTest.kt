package com.example.expensetracker.data

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultDataRepositoryTest {
    @Test
    fun defaultDataRepository_emitsInitialData() = runTest {
        val repository = DefaultDataRepository()
        assertEquals(listOf("Android"), repository.data.first())
    }
}
