package ch.karimattia.workoutpixel.composables

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.karimattia.workoutpixel.ui.theme.GrayBackground
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import java.util.*
import ch.karimattia.workoutpixel.*
import ch.karimattia.workoutpixel.R

private const val TAG: String = "Instructions"

@ExperimentalCoilApi
@ExperimentalAnimationGraphicsApi
@ExperimentalPagerApi
@Composable
fun Instructions() {

	// remember: https://stackoverflow.com/questions/65889035/how-to-use-string-resources-in-android-jetpack-compose
	val instructions = arrayListOf(
		Instruction(
			stringResource(R.string.instructions_intro_title),
			R.string.instructions_pitch,
			R.drawable.instructions_pitch,
			R.drawable.instructions_pitch,
		),
		Instruction(
			stringResource(R.string.step) + " 1",
			R.string.instructions_step1,
			R.drawable.step1,
			R.drawable.instructions_long_click
		), Instruction(
			stringResource(R.string.step) + " 2",
			R.string.instructions_step2,
			R.drawable.step2,
			R.drawable.instructions_widget_selection
		), Instruction(
			stringResource(R.string.step) + " 3",
			R.string.instructions_step3,
			R.drawable.step3,
			R.drawable.instructions_configure_widget
		), Instruction(
			stringResource(R.string.step) + " 4",
			R.string.instructions_step4,
			R.drawable.step4,
			R.drawable.instructions_widget_created
		), Instruction(
			stringResource(R.string.step) + " 5",
			R.string.instructions_step5,
			R.drawable.step5,
			R.drawable.instructions_main_app
		)
	)

	Column(
		modifier = Modifier
			.background(Color(GrayBackground))
			.padding(16.dp)
	) {
		val pagerState = rememberPagerState(pageCount = instructions.size)
		HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->

			Column {
				// https://stackoverflow.com/questions/60229555/adding-gif-into-jetpack-compose

				val imageLoader = ImageLoader.invoke(LocalContext.current).newBuilder()
					.componentRegistry {
						if (SDK_INT >= 28) {
							add(ImageDecoderDecoder(LocalContext.current))
						} else {
							add(GifDecoder())
						}
					}.build()

				// https://stackoverflow.com/questions/68848663/how-do-i-create-a-jetpack-compose-image-that-resizes-automatically-based-on-a-re
				val painter =
					rememberImagePainter(data = instructions[page].gif, imageLoader = imageLoader)
				Image(
					painter = painter,
					contentDescription = "instructions image",
					contentScale = ContentScale.Fit,
					modifier = Modifier
						.fillMaxHeight()
						.weight(weight = 1f, fill = true)
						.padding(all = 12.dp)
						.shadow(elevation = 4.dp)
						.border(border = BorderStroke(4.dp, Color.White))
						.align(alignment = CenterHorizontally)
						// Ratio from today's screenshots so the border doesn't jump.
						.aspectRatio(ratio = 0.4615384615f)
						// Properly calculated aspect ratio after the image is loaded.
						.then(
							(painter.state as? ImagePainter.State.Success)
								?.painter
								?.intrinsicSize
								?.let { intrinsicSize ->
									Modifier.aspectRatio(ratio = intrinsicSize.width / intrinsicSize.height)
								} ?: Modifier
						)
				)
				Text(
					text = instructions[page].title.uppercase(Locale.getDefault()),
					fontWeight = FontWeight.Bold,
					fontSize = 16.sp,
					textAlign = TextAlign.Center,
					modifier = Modifier
						.fillMaxWidth()
						.padding(all = 8.dp)
						.align(alignment = CenterHorizontally)
				)
				Text(
					text = stringResource(instructions[page].text),
					fontSize = 14.sp,
					textAlign = TextAlign.Center,
					modifier = Modifier
						.fillMaxWidth()
						.padding(all = 8.dp)
						.height(70.dp)
						.align(CenterHorizontally)
				)
			}
		}

		HorizontalPagerIndicator(
			pagerState = pagerState,
			indicatorWidth = 12.dp,
			modifier = Modifier
				.align(alignment = CenterHorizontally)
				.padding(all = 8.dp),
		)
	}
}


@ExperimentalCoilApi
@ExperimentalAnimationGraphicsApi
@ExperimentalPagerApi
@Preview(name = "Instructions preview")
@Composable
fun InstructionsPreview() {
	Instructions()
}

class Instruction internal constructor(
	val title: String,
	val text: Int,
	val gif: Int,
	val backupImage: Int
)
