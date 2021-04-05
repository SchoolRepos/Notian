package me.profiluefter.profinote.data.synchronization

import retrofit2.Call
import retrofit2.http.*

interface NotesAPI {
    @POST("register.php")
    suspend fun register(
        @Body credentials: APIObjects.Credentials
    ): Call<APIObjects.UserID>

    // --------------------
    // Lists
    // --------------------

    @POST("todolists.php")
    suspend fun newList(
        @Body list: APIObjects.TodoList,
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<APIObjects.TodoList>

    @GET("todolists.php")
    suspend fun getList(
        @Query("id") id: Int,
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<APIObjects.TodoList>

    @GET("todolists.php")
    suspend fun getAllLists(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<List<APIObjects.TodoList>>

    @PUT("todolists.php")
    suspend fun editList(
        @Query("id") id: Int,
        @Body list: APIObjects.TodoList,
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<APIObjects.TodoList>

    @DELETE("todolists.php")
    suspend fun deleteList(
        @Query("id") id: Int,
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<Void>

    // --------------------
    // Todos
    // --------------------

    @POST("todo.php")
    suspend fun newTodo(
        @Body todo: APIObjects.Todo,
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<APIObjects.Todo>

    @GET("todo.php")
    suspend fun getTodo(
        @Query("id") id: Int,
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<APIObjects.Todo>

    @GET("todo.php")
    suspend fun getAllTodos(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<List<APIObjects.Todo>>

    @PUT("todo.php")
    suspend fun editTodo(
        @Query("id") id: Int,
        @Body todo: APIObjects.Todo,
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<APIObjects.Todo>

    @DELETE("todo.php")
    suspend fun deleteTodo(
        @Query("id") id: Int,
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<Void>
}

object APIObjects {
    data class Credentials(
        val username: String,
        val password: String,
        val name: String
    )

    data class UserID(
        val userId: String
    )

    data class TodoList(
        val name: String,
        val additionalData: String,
        val id: String? = null,
        val ownerId: String? = null
    )

    data class Todo(
        val todoListId: String,
        val title: String,
        val description: String,
        val dueDate: String,
        val state: String,
        val additionalData: String,
        val id: String?,
        val ownerId: String?
    )
}
