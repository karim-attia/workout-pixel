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
	entities = [Goal::class, PastClick::class],
	version = 4,
	autoMigrations = [
		AutoMigration(from = 2, to = 4, spec = AppDatabaseKotlin.AutoMigration::class),
		AutoMigration(from = 3, to = 4, spec = AppDatabaseKotlin.AutoMigration::class)
	])
abstract class AppDatabaseKotlin : RoomDatabase() {
	abstract fun goalDao(): GoalDao

	@DeleteColumn(tableName = "goals", columnName = "status")
	class AutoMigration : AutoMigrationSpec
}

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

	@Provides
	fun provideGoalDao(appDatabaseKotlin: AppDatabaseKotlin): GoalDao {
		return appDatabaseKotlin.goalDao()
	}

	@Provides
	@Singleton
	fun provideAppDatabase(@ApplicationContext context: Context): AppDatabaseKotlin {
		return Room.databaseBuilder(
			context,
			AppDatabaseKotlin::class.java,
			"WorkoutPixelDatabase"
		)
			.allowMainThreadQueries()
			//.addMigrations(MIGRATION_1_2)
			//.fallbackToDestructiveMigration()
			.build()
	}
}
