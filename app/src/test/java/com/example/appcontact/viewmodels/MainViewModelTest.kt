package com.example.appcontact.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.appcontact.models.User
import com.example.appcontact.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: UserRepository

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadUsers success updates users LiveData`() = runTest {
        // Arrange
        val userList = listOf(
            User(1, "Test User", "test@test.com"),
            User(2, "Another User", "other@test.com")
        )
        val response = Response.success(userList)
        `when`(repository.getUsers()).thenReturn(response)

        // Act
        viewModel.loadUsers()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(userList, viewModel.users.value)
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun `addUser success refreshes list`() = runTest {
        val name = "New User"
        val email = "new@test.com"
        val expectedUserArg = User(id = null, name = name, email = email)
        
        val returnedUser = User(3, name, email)
        val response = Response.success(returnedUser)
        
        `when`(repository.addUser(expectedUserArg)).thenReturn(response)
        
        val userList = listOf(returnedUser)
        `when`(repository.getUsers()).thenReturn(Response.success(userList))

        viewModel.addUser(name, email)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Contacto agregado", viewModel.operationStatus.value)
        assertEquals(userList, viewModel.users.value)
    }
}
