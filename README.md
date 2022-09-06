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
* At the heart of the app, there is the data class [Goal](/app/src/main/java/ch/karimattia/workoutpixel/data/Goal.kt). Among other things it contains the following information:

	| Variable      | Description          														|
	| :--- 					| :--- 																						|
	| title 				| The name of this goal 													|
	| lastWorkout 	| The last time the widget was clicked 						|
	| intervalBlue 	| How often the user wants to achieve this goal 	|
	| status 				| Whether it has been reached within lastWorkout, determines the color of the widget |

* Users can add new goals by adding a widget on their Android launcher. This starts the [ConfigureActivity](/app/src/main/java/ch/karimattia/workoutpixel/activities/ConfigureActivity.kt) where they can add the details of their goal. 
* Saving the new goal adds it to the database and initializes the widget including a listener for when users click on it ([WidgetActions](/app/src/main/java/ch/karimattia/workoutpixel/core/WidgetActions.kt)).
* Goals are saved in a SQL database ([AppDatabase](/app/src/main/java/ch/karimattia/workoutpixel/data/AppDatabase.kt) and [GoalDao](/app/src/main/java/ch/karimattia/workoutpixel/data/GoalDao.kt)) and can be accessed through the [GoalRepository](/app/src/main/java/ch/karimattia/workoutpixel/data/GoalRepository.kt).

### Clicking on the widget
* When a user clicks on a widget, the [WorkoutPixelAppWidgetProvider](/app/src/main/java/ch/karimattia/workoutpixel/core/WorkoutPixelAppWidgetProvider.kt) class receives a broadcast to update the goal.
* This instructs the [GoalRepository](/app/src/main/java/ch/karimattia/workoutpixel/data/GoalRepository.kt) to update the goal with this latest click.
* Then, the [WidgetActions](/app/src/main/java/ch/karimattia/workoutpixel/core/WidgetActions.kt) class updates the widget on the homescreen, e.g. turns it green.
* This also saves a [PastClick](/app/src/main/java/ch/karimattia/workoutpixel/data/PastClick.kt) to the database.

### Updating the widget over night
* The [WidgetAlarm](/app/src/main/java/ch/karimattia/workoutpixel/core/WidgetAlarm.kt) class triggers a broadcast at 3:00 every night.
* Similarly as when a user clicks on a widget, the [WorkoutPixelAppWidgetProvider](/app/src/main/java/ch/karimattia/workoutpixel/core/WorkoutPixelAppWidgetProvider.kt) class receives this broadcast.
* This instructs the [WidgetActions](/app/src/main/java/ch/karimattia/workoutpixel/core/WidgetActions.kt) class to update all widgets on the homescreen based on their new status.

### Main activity
The [MainActivity](/app/src/main/java/ch/karimattia/workoutpixel/activities/MainActivity.kt) class contains the screens for when users open the app itself:
* The [Instructions](/app/src/main/java/ch/karimattia/workoutpixel/screens/Instructions.kt) screen shows how to add a widget on the homescreen in a couple of gifs.
* The [AllGoalsView](/app/src/main/java/ch/karimattia/workoutpixel/screens/allGoals/AllGoalsView.kt) screen gives an overview over all goals.
* Clicking on a goal leads to the [GoalDetailView](/app/src/main/java/ch/karimattia/workoutpixel/screens/goalDetail/GoalDetailView.kt) screen which shows some more information on this goal including a list of all past clicks on the widget.
* Clicking on the edit icon leads to the [EditGoalView](/app/src/main/java/ch/karimattia/workoutpixel/screens/EditGoalView.kt) screen where users can edit their goal similarly as when they create it.
* In the [Settings](/app/src/main/java/ch/karimattia/workoutpixel/screens/settings/Settings.kt) screen, users can configure the app. The preferences are stored in the [SettingsRepository](/app/src/main/java/ch/karimattia/workoutpixel/data/SettingsRepository.kt).
