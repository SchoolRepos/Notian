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
    @Query("SELECT * FROM rawtodolist")
    suspend fun getAll(): List<RawTodoList>

    @Query("SELECT * FROM rawtodolist")
    fun getAllLive(): LiveData<List<RawTodoList>>

    @Query("SELECT * FROM rawtodolist WHERE localID = :listID")
    fun getByLocalIDLive(listID: Int): LiveData<RawTodoList>
}

@Dao
interface TodoDao {
    @Insert
    suspend fun insert(rawTodo: RawTodo)

    @Query("SELECT * FROM rawtodo WHERE localListID = :listID")
    fun getByListIDLive(listID: Int): LiveData<List<RawTodo>>

    @Query("SELECT * FROM rawtodo WHERE localID = :localID")
    suspend fun getByLocalID(localID: Int): RawTodo

    @Query("DELETE FROM rawtodo WHERE localID = :localID")
    suspend fun delete(localID: Int)

    @Query("UPDATE rawtodo SET additionalData = 'DELETE' WHERE localID = :localID")
    suspend fun scheduleDelete(localID: Int)

    @Update
    suspend fun update(rawTodo: RawTodo)

    @Query("UPDATE rawtodo SET id = :id WHERE localID = :localID")
    suspend fun changeID(localID: Int, id: String)

    @Query("SELECT MAX(localID) + 1 FROM rawtodo")
    suspend fun nextAvailableID(): Int
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