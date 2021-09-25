package ch.karimattia.workoutpixel.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(entities = [PastWorkout::class, Goal::class], version = 2)
abstract class AppDatabaseKotlin : RoomDatabase() {
	abstract fun goalDao(): GoalDao
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