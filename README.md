# Workout Pixel

## Table of content
1. [Features](#Features)
2. [Download](#Download)
3. [About the code](#About-the-code)

## Features
<img src="/workoutpixel/src/main/res/drawable-v24/instructions_pitch.png" alt="Features" width="200"/>

## Download
You can download the app in the [Play Store](https://play.google.com/store/apps/details?id=ch.karimattia.workoutpixel).

## About the code
### Adding a new goal
* At the heart of the app, there is the data class [Goal](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/data/Goal.kt). Among other things it contains the following information:

	| Variable      | Description          														|
	| :--- 					| :--- 																						|
	| title 				| The name of this goal 													|
	| lastWorkout 	| The last time the widget was clicked 						|
	| intervalBlue 	| How often the user wants to achieve this goal 	|
	| status 				| Whether it has been reached within lastWorkout, determines the color of the widget |

* Users can add new goals by adding a widget on their Android launcher. This starts the [ConfigureActivity](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/activities/ConfigureActivity.kt) where they can add the details of their goal. 
* Saving the new goal adds it to the database and initializes the widget including a listener for when users click on it ([GoalWidgetActions](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/core/GoalWidgetActions.kt)).
* Goals are saved in a SQL database ([AppDatabase](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/data/AppDatabase.kt) and [GoalDao](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/data/GoalDao.kt)) and can be accessed through the [GoalRepository](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/data/GoalRepository.kt).

### Clicking on the widget
* When a user clicks on a widget, the [WorkoutPixelAppWidgetProvider](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/core/WorkoutPixelAppWidgetProvider.kt) class receives a broadcast to update the goal.
* This instructs the [GoalSaveActions](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/core/GoalSaveActions.kt) class to update the goal with this latest click.
* Then, the [GoalWidgetActions](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/core/GoalWidgetActions.kt) class updates the widget on the homescreen, e.g. turns it green.
* This also saves a [PastClick](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/data/PastClick.kt) to the database.

### Updating the widget over night
* The [WidgetAlarm](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/core/WidgetAlarm.kt) class triggers a broadcast at 3:00 every night.
* Similarly as when a user clicks on a widget, the [WorkoutPixelAppWidgetProvider](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/core/WorkoutPixelAppWidgetProvider.kt) class receives this broadcast.
* This instructs the [GoalWidgetActions](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/core/GoalWidgetActions.kt) class to update all widgets on the homescreen based on their new status.

### Main activity
The [MainActivity](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/activities/MainActivity.kt) class contains the screens for when users open the app itself:
* The [Instructions](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/composables/Instructions.kt) screen shows how to add a widget on the homescreen in a couple of gifs.
* The [AllGoalsView](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/composables/AllGoalsView.kt) screen gives an overview over all goals.
* Clicking on a goal leads to the [GoalDetailView](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/composables/GoalDetailView.kt) screen which shows some more information on this goal including a list of all past clicks on the widget.
* Clicking on the edit icon leads to the [EditGoalView](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/composables/EditGoalView.kt) screen where users can edit their goal similarly as when they create it.
* In the [Settings](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/composables/Settings.kt) screen, users can configure the app. The preferences are stored in the [SettingsRepository](/workoutpixel/src/main/java/ch/karimattia/workoutpixel/data/SettingsRepository.kt).
