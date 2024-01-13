package ch.karimattia.workoutpixel.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Database(
	entities = [GoalWithoutCount::class, PastClick::class],
	views = [Goal::class],
	version = 9,
	autoMigrations = [
		AutoMigration(from = 2, to = 4, spec = AppDatabase.AutoMigration::class),
		AutoMigration(from = 3, to = 4, spec = AppDatabase.AutoMigration::class),
		AutoMigration(from = 4, to = 5, spec = AppDatabase.AutoMigration::class),
		AutoMigration(from = 5, to = 6, spec = AppDatabase.AutoMigration::class),
		AutoMigration(from = 6, to = 7, spec = AppDatabase.AutoMigration::class),
		AutoMigration(from = 7, to = 8, spec = AppDatabase.AutoMigration::class),
		AutoMigration(from = 8, to = 9, spec = AppDatabase.AutoMigration::class),
	])
abstract class AppDatabase : RoomDatabase() {
	abstract fun goalDao(): GoalDao

	@DeleteColumn(tableName = "goals", columnName = "status")
	class AutoMigration : AutoMigrationSpec
}

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

	@Provides
	fun provideGoalDao(appDatabaseKotlin: AppDatabase): GoalDao {
		return appDatabaseKotlin.goalDao()
	}

	@Provides
	@Singleton
	fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
		return Room.databaseBuilder(
			context,
			AppDatabase::class.java,
			"WorkoutPixelDatabase"
		)
			//.allowMainThreadQueries()
			//.addMigrations(MIGRATION_1_2)
			//.fallbackToDestructiveMigration()
			.build()
	}
}
