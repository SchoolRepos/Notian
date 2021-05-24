package me.profiluefter.profinote.data.local

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.profiluefter.profinote.data.entities.RawTodo
import me.profiluefter.profinote.data.entities.RawTodoList
import javax.inject.Singleton

@Dao
interface ListDao {
    @Insert
    suspend fun insert(list: RawTodoList): Long

    @Insert
    suspend fun insert(lists: List<RawTodoList>)

    @Query("SELECT * FROM rawtodolist")
    suspend fun getAll(): List<RawTodoList>

    @Query("SELECT * FROM rawtodolist")
    fun getAllLive(): LiveData<List<RawTodoList>>

    @Query("SELECT * FROM rawtodolist WHERE localID = :listID")
    fun getByLocalIDLive(listID: Int): LiveData<RawTodoList>

    @Query("SELECT * FROM rawtodolist WHERE localID = :listID")
    suspend fun getByLocalID(listID: Int): RawTodoList

    @Query("SELECT localID FROM rawtodolist WHERE id = :todoListId")
    suspend fun getLocalIDByRemoteID(todoListId: String): Int

    @Update
    suspend fun update(list: List<RawTodoList>)

    @Query("UPDATE rawtodolist SET id = :id WHERE localID = :localID")
    suspend fun changeID(localID: Int, id: String)

    @Query("DELETE FROM rawtodolist")
    suspend fun nuke()

    @Delete
    suspend fun delete(it: RawTodoList)
}

@Dao
interface TodoDao {
    @Insert
    suspend fun insert(rawTodo: RawTodo): Long

    @Insert
    suspend fun insert(rawTodo: List<RawTodo>)

    @Query("SELECT * FROM rawtodo")
    suspend fun getAll(): List<RawTodo>

    @Query("SELECT * FROM rawtodo WHERE localListID = :listID")
    suspend fun getByListID(listID: Int): List<RawTodo>

    @Query("SELECT * FROM rawtodo WHERE localListID = :listID")
    fun getByListIDLive(listID: Int): LiveData<List<RawTodo>>

    @Query("SELECT * FROM rawtodo WHERE localID = :localID")
    suspend fun getByLocalID(localID: Int): RawTodo

    @Update
    suspend fun update(rawTodo: RawTodo)

    @Update
    suspend fun update(list: List<RawTodo>)

    @Query("UPDATE rawtodo SET additionalData = 'DELETE' WHERE localID = :localID")
    suspend fun scheduleDelete(localID: Int)

    @Query("UPDATE rawtodo SET state = CASE WHEN :checked = 1 THEN 'DONE' ELSE 'TODO' END WHERE localID = :localID")
    suspend fun setChecked(localID: Int, checked: Boolean)

    @Query("UPDATE rawtodo SET id = :id WHERE localID = :localID")
    suspend fun changeID(localID: Int, id: String)

    @Query("UPDATE rawtodo SET todoListId = :remoteID WHERE localListID = :localID")
    suspend fun changeListID(localID: Int, remoteID: String)

    @Delete
    suspend fun delete(rawTodo: RawTodo)

    @Query("DELETE FROM rawtodo")
    suspend fun nuke()
}

@Database(entities = [RawTodoList::class, RawTodo::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun listDao(): ListDao
}

@Module
@InstallIn(SingletonComponent::class)
object NoteDatabaseModule {
    @Provides
    @Singleton
    fun noteDatabase(@ApplicationContext context: Context): NotesDatabase {
        return Room.databaseBuilder(
            context,
            NotesDatabase::class.java,
            "notes"
        ).build()
    }
}