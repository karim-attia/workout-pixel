package ch.karimattia.workoutpixel

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

const val TAG = "SettingsRepository"
private const val USER_PREFERENCES_NAME = "user_preferences"
private const val DATA_STORE_FILE_NAME = "user_prefs.pb"
private const val SORT_ORDER_KEY = "sort_order"

class SettingsRepository @Inject constructor(
	@ApplicationContext val context: Context,
) {

	companion object PreferenceKeys {
		val colorDone = intPreferencesKey(name = "colorDone")
		val colorFirstInterval = intPreferencesKey(name = "colorFirstInterval")
		val colorSecondInterval = intPreferencesKey(name = "colorSecondInterval")
		val colorInitial = intPreferencesKey(name = "colorInitial")
	}

/*	private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
		name = PREFERENCE_NAME
	)

	fun getSetting(preferenceKey: Preferences.Key<Int>): Flow<Int> = context.dataStore.data
		.map { preferences ->
			// TODO: default color
			preferences[preferenceKey] ?: 0
		}*/


	val Context.dataStore by dataStore("settings.json", serializer = SettingsDataSerializer)

	/*private val Context.userPreferencesStore: DataStore<SettingsData> by dataStore(
		fileName = DATA_STORE_FILE_NAME,
		serializer = SettingsData2Serializer
	)*/

	val getSettings: Flow<SettingsData> = context.dataStore.data

	suspend fun updateColorDone(colorDoneInt: Int) {
		context.dataStore.updateData {
			it.copy(colorDoneInt = colorDoneInt)
		}
	}


/*
	suspend fun saveColorDone(color: Int) {
		context.dataStore.edit { settings ->
			settings[colorDone] = color
		}
	}

	suspend fun saveIntSetting(preferenceKey: Preferences.Key<Int>, int: Int) {
		Log.d(TAG, "saveIntSetting: $preferenceKey - $int")
		context.dataStore.edit { settings ->
			settings[preferenceKey] = int
		}
	}

	fun getSettings(): Flow<SettingsData> = flow {
		val settingsData = SettingsData()
		// settingsData.colorDoneInt = getSetting(colorDone).last()
		getSetting(colorDone).onEach { }.collect { settingsData.colorDoneInt = it }
		Log.d(TAG, "getSettings0: ${settingsData.colorDoneInt}")
		emit(settingsData)
	}


	fun getSettingsInt(): Flow<SettingsDataInt> = flow {
		SettingsDataFlowInt(
			colorDone = getSetting(colorDone),
		)
	}

	private fun getSettingsDataColorFromSettingsDataInt(settingsDataInt: SettingsDataInt): SettingsDataColor = SettingsDataColor(
		colorDone = Color(settingsDataInt.colorDone),
		colorFirstInterval = Color(settingsDataInt.colorFirstInterval),
		colorSecondInterval = Color(settingsDataInt.colorSecondInterval),
		colorInitial = Color(settingsDataInt.colorInitial)
	)

	fun getSettingsColorFlow(): Flow<SettingsDataColor> = getSettingsInt().transform { getSettingsDataColorFromSettingsDataInt(it) }

*/

}
/*

data class SettingsDataColor internal constructor(
	val colorDone: Color = Color(Green),
	val colorFirstInterval: Color = Color(Blue),
	val colorSecondInterval: Color = Color(Red),
	val colorInitial: Color = Color(Purple),
)

data class SettingsDataFlowInt internal constructor(
	val colorDone: Flow<Int>,
)

data class SettingsDataInt internal constructor(
	val colorDone: Int = Green,
	val colorFirstInterval: Int = Blue,
	val colorSecondInterval: Int = Red,
	val colorInitial: Int = Purple,
)
*/

@Serializable
data class SettingsData(
	val colorDoneInt: Int = Green,
	val colorFirstIntervalInt: Int = Blue,
	val colorSecondIntervalInt: Int = Red,
	val colorInitialInt: Int = Purple,
) {
	fun colorDone(): Color = Color(colorDoneInt)
	fun colorFirstInterval(): Color = Color(colorFirstIntervalInt)
	fun colorSecondInterval(): Color = Color(colorSecondIntervalInt)
	fun colorInitial(): Color = Color(colorInitialInt)
}

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
	@Singleton
	@Provides
	fun providesDataStore(
		@ApplicationContext context: Context
	) = SettingsRepository(context = context)
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
		output.write(Json.encodeToString(SettingsData.serializer(), t).encodeToByteArray())
	}
}
/*

object UserPreferencesSerializer : Serializer<SettingsData> {
	override val defaultValue: SettingsData = SettingsData.getDefaultInstance()
	override suspend fun readFrom(input: InputStream): SettingsData {
		try {
			return SettingsData.parseFrom(input)
		} catch (exception: InvalidProtocolBufferException) {
			throw CorruptionException("Cannot read proto.", exception)
		}
	}

	override suspend fun writeTo(t: SettingsData, output: OutputStream) = t.writeTo(output)
}
*/
