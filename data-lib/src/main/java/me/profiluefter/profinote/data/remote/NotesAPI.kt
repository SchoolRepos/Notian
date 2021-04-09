package me.profiluefter.profinote.data.remote

import me.profiluefter.profinote.data.entities.RawTodo
import me.profiluefter.profinote.data.entities.RawTodoList
import retrofit2.http.*

interface NotesAPI {
    @POST("register.php")
    suspend fun register(
        @Body credentials: Credentials
    ): UserID

    // --------------------
    // Lists
    // --------------------

    @POST("todolists.php")
    suspend fun newList(
        @Body list: RawTodoList,
        @Query("username") username: String,
        @Query("password") password: String
    ): RawTodoList

    @GET("todolists.php")
    suspend fun getList(
        @Query("id") id: Int,
        @Query("username") username: String,
        @Query("password") password: String
    ): RawTodoList

    @GET("todolists.php")
    suspend fun getAllLists(
        @Query("username") username: String,
        @Query("password") password: String
    ): List<RawTodoList>

    @PUT("todolists.php")
    suspend fun editList(
        @Query("id") id: Int,
        @Body list: RawTodoList,
        @Query("username") username: String,
        @Query("password") password: String
    ): RawTodoList

    @DELETE("todolists.php")
    suspend fun deleteList(
        @Query("id") id: Int,
        @Query("username") username: String,
        @Query("password") password: String
    )

    // --------------------
    // Todos
    // --------------------

    @POST("todo.php")
    suspend fun newTodo(
        @Body todo: RawTodo,
        @Query("username") username: String,
        @Query("password") password: String
    ): RawTodo

    @GET("todo.php")
    suspend fun getTodo(
        @Query("id") id: Int,
        @Query("username") username: String,
        @Query("password") password: String
    ): RawTodo

    @GET("todo.php")
    suspend fun getAllTodos(
        @Query("username") username: String,
        @Query("password") password: String
    ): List<RawTodo>

    @PUT("todo.php")
    suspend fun editTodo(
        @Query("id") id: Int,
        @Body todo: RawTodo,
        @Query("username") username: String,
        @Query("password") password: String
    ): RawTodo

    @DELETE("todo.php")
    suspend fun deleteTodo(
        @Query("id") id: Int,
        @Query("username") username: String,
        @Query("password") password: String
    )
}

data class Credentials(
    val username: String,
    val password: String,
    val name: String
)

data class UserID(
    val userId: String
)

data class Error(
    val message: String
)