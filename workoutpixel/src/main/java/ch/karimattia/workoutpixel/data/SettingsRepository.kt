package ch.karimattia.workoutpixel.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import ch.karimattia.workoutpixel.ui.theme.Blue
import ch.karimattia.workoutpixel.ui.theme.Green
import ch.karimattia.workoutpixel.ui.theme.Purple
import ch.karimattia.workoutpixel.ui.theme.Red
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("unused")
private const val TAG = "SettingsRepository"

class SettingsRepository @Inject constructor(
	@ApplicationContext val context: Context,
) {
	private val Context.dataStore by dataStore("settings.json", serializer = SettingsDataSerializer)
	val getSettings: Flow<SettingsData> = context.dataStore.data
	suspend fun getSettingsOnce(): SettingsData = getSettings.first()
	suspend fun updateSettings(settingsData: SettingsData) = context.dataStore.updateData { settingsData }
}

@Serializable
data class SettingsData(
	val colorDoneInt: Int = Green,
	val colorFirstIntervalInt: Int = Blue,
	val colorSecondIntervalInt: Int = Red,
	val colorInitialInt: Int = Purple,
	val dateLanguage: String? = Locale.getDefault().language,
	val dateCountry: String? = Locale.getDefault().country,
	val timeLanguage: String? = Locale.getDefault().language,
	val timeCountry: String? = Locale.getDefault().country,
) {
	fun colorDone(): Color = Color(colorDoneInt)
	fun colorFirstInterval(): Color = Color(colorFirstIntervalInt)
	fun colorSecondInterval(): Color = Color(colorSecondIntervalInt)
	fun colorInitial(): Color = Color(colorInitialInt)
	fun isDateLocaleDefault(): Boolean = (dateLanguage == null || dateCountry == null)
	fun isTimeLocaleDefault(): Boolean = (timeLanguage == null || timeCountry == null)
	fun dateLocale(): Locale = if (isDateLocaleDefault()) Locale.getDefault() else Locale(dateLanguage!!, dateCountry!!)
	fun timeLocale(): Locale = if (isTimeLocaleDefault()) Locale.getDefault() else Locale(timeLanguage!!, timeCountry!!)
}

object SettingsDataSerializer : Serializer<SettingsData> {
	override val defaultValue = SettingsData()

	override suspend fun readFrom(input: InputStream): SettingsData {
		try {
			return Json.decodeFromString(
				SettingsData.serializer(), input.readBytes().decodeToString()
			)
		} catch (serialization: SerializationException) {
			throw CorruptionException("Unable to read SettingsData", serialization)
		}
	}

	override suspend fun writeTo(t: SettingsData, output: OutputStream) {
		@Suppress("BlockingMethodInNonBlockingContext")
		output.write(Json.encodeToString(SettingsData.serializer(), t).encodeToByteArray())
	}
}

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
	@Singleton
	@Provides
	fun providesDataStore(
		@ApplicationContext context: Context,
	) = SettingsRepository(context = context)
}

