package mozilla.liloapp.migration

import android.content.Context
import mozilla.liloapp.migration.db.MigrationDatabase
import mozilla.liloapp.migration.db.TabEntity

/**
 * A storage implementation for organizing DDG tabs migration to Fenix.
 */
class MigrationStorage(context: Context) {

    internal var database: Lazy<MigrationDatabase> = lazy { MigrationDatabase.get(context) }

    private val migrationDao by lazy { database.value.migrationDao() }

    /**
     * Returns a [List] of all the [TabEntity] instances.
     */
    suspend fun getTabsList(): List<TabEntity> {
        return migrationDao.getTabsList()
    }
}
