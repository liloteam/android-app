package mozilla.liloapp.migration.db

import androidx.room.Dao
import androidx.room.Query

@Dao
internal interface MigrationDao {

    @Query("SELECT * FROM tabs ORDER BY position DESC")
    suspend fun getTabsList(): List<TabEntity>
}
