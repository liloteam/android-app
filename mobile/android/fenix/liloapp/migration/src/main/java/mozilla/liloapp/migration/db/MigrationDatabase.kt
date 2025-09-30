package mozilla.liloapp.migration.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TabEntity::class], version = 45)
internal abstract class MigrationDatabase : RoomDatabase() {
    abstract fun migrationDao(): MigrationDao

    companion object {
        @Volatile
        private var instance: MigrationDatabase? = null

        @Synchronized
        fun get(context: Context): MigrationDatabase {
            instance?.let { return it }

            return Room.databaseBuilder(
                context,
                MigrationDatabase::class.java,
                "app.db",
            ).addMigrations(
                Migrations.migration_44_45
            ).build().also {
                instance = it
            }
        }
    }
}

internal object Migrations {
    val migration_44_45 = object : Migration(44, 45) {
        override fun migrate(db: SupportSQLiteDatabase) {

        }
    }

}
