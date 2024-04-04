package hr.foi.rmai.memento.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hr.foi.rmai.memento.entities.TaskCategory

@Dao
interface TaskCategoriesDAO {
    @Query("SELECT * FROM task_categories")
    fun getAllCategories(): List<TaskCategory>
    @Query("SELECT * FROM task_categories WHERE id = :id")
    fun getCategoryById(id: Int): TaskCategory
    @Insert
    fun insertCategory(category: TaskCategory)
}