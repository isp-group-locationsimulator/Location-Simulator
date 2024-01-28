package com.ispgr5.locationsimulator.presentation.select.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ispgr5.locationsimulator.R
import com.ispgr5.locationsimulator.core.util.TestTags
import com.ispgr5.locationsimulator.domain.model.Configuration

/**
 * Shows one Configuration as Button in max width
 */
@Composable
fun RowScope.OneConfigurationListMember(
	configuration: Configuration,
	isToggled: Boolean,
	onToggleClicked: () -> Unit,
	onEditClicked: () -> Unit,
	onSelectClicked: () -> Unit,
	onExportClicked: () -> Unit,
	onDuplicateClicked: () -> Unit,
	hasErrors: Boolean,
	onErrorInfoClicked: () -> Unit,
	isFavorite: Boolean,
	onFavoriteClicked: () -> Unit
) {
	val rowBackgroundColor: Color = MaterialTheme.colors.surface

	Button(
		onClick = onToggleClicked,
		contentPadding = PaddingValues(0.dp),
		enabled = true,
		shape = MaterialTheme.shapes.small,
		border = null,
		elevation = null,
		colors = ButtonDefaults.buttonColors(
			backgroundColor = Color.Transparent,
			disabledBackgroundColor = Color.Transparent,
			disabledContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
		),
		modifier = Modifier
			//.border(width = 1.dp, color = MaterialTheme.colors.primary, shape = RoundedCornerShape(6.dp))
			.background(rowBackgroundColor, shape = RoundedCornerShape(6.dp))
			.weight(1f)
			.heightIn(min = 55.dp)
			.testTag(TestTags.SELECT_CONFIG_BUTTON_PREFIX + configuration.name)

	) {
		//Column is needed for toggling so the Toggled Information is shown under the Configuration name
		Column(modifier = Modifier.padding(4.dp)) {
			Row(
				Modifier.fillMaxWidth(),
				verticalAlignment = Alignment.CenterVertically,
				horizontalArrangement = Arrangement.Start
			) {
				//new row so the Configuration name is centered
				Column(
					Modifier
						.weight(8f)
						.padding(start = 5.dp, end = 5.dp, top = 0.dp, bottom = 0.dp)

				) {
					Column {
						Text(
							text = "${configuration.id} - ${configuration.name}",
							fontSize = 18.sp,
						)
						if (isToggled) {
							Spacer(modifier = Modifier.height(3.dp))
							Text(
								text = configuration.description,
								fontSize = 14.sp,
							)
						}
					}
				}

				Column(Modifier.weight(1f)) {
					//showing errors
					if (hasErrors) {
						Button(
							onClick = onErrorInfoClicked,
							contentPadding = PaddingValues(0.dp),
							enabled = true,
							shape = MaterialTheme.shapes.small,
							border = null,
							elevation = null,
							colors = ButtonDefaults.buttonColors(
								backgroundColor = Color.Transparent,
								contentColor = MaterialTheme.colors.primary,
								disabledBackgroundColor = Color.Transparent,
								disabledContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
							)
						) {
							Icon(
								painter = painterResource(id = R.drawable.ic_baseline_error_outline_24),
								contentDescription = null,
								tint = Color.Red,
							)
						}
					}
				}
				Column(Modifier.weight(1f)) {
					Button(
						onClick = onFavoriteClicked,
						contentPadding = PaddingValues(0.dp),
						enabled = true,
						shape = MaterialTheme.shapes.small,
						border = null,
						elevation = null,
						colors = ButtonDefaults.buttonColors(
							backgroundColor = Color.Transparent,
							contentColor = MaterialTheme.colors.primary,
							disabledBackgroundColor = Color.Transparent,
							disabledContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
						)
					) {
						//favorite
						if (isFavorite) {
							Icon(
								painter = painterResource(id = R.drawable.ic_baseline_star_24),
								contentDescription = null,
								tint = Color.Yellow,
							)
						} else {
							Icon(
								painter = painterResource(id = R.drawable.ic_baseline_star_outline_24),
								contentDescription = null,
								tint = MaterialTheme.colors.onSurface,
							)
						}
					}
				}

				Column(Modifier.weight(1f)) {
					Icon(
						painter = if (isToggled) {
							painterResource(id = R.drawable.ic_baseline_keyboard_arrow_down_24)
						} else {
							painterResource(id = R.drawable.ic_baseline_keyboard_arrow_right_24)
						},
						contentDescription = null
					)
				}
			}
			//The Information which is shown when toggle is active
			if (isToggled) {
				Row(
					Modifier.fillMaxWidth(),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceAround
				) {
					//The Select Button
					Button(
						onClick = onSelectClicked,
						contentPadding = PaddingValues(0.dp),
						enabled = !hasErrors,
						shape = MaterialTheme.shapes.small,
						border = null,
						elevation = null,
						modifier = Modifier.testTag(TestTags.SELECT_CONFIG_BUTTON_SELECT_PREFIX + configuration.name),
						colors = ButtonDefaults.buttonColors(
							backgroundColor = Color.Transparent,
							contentColor = MaterialTheme.colors.primary,
							disabledBackgroundColor = Color.Transparent,
							disabledContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
						)
					) {
						Icon(
							painter = painterResource(id = R.drawable.ic_baseline_play_arrow_24),
							contentDescription = null,
							tint = MaterialTheme.colors.onSurface
						)
					}
					//The Export Button
					Button(
						onClick = onExportClicked,
						contentPadding = PaddingValues(0.dp),
						enabled = !hasErrors,
						shape = MaterialTheme.shapes.small,
						border = null,
						elevation = null,
						colors = ButtonDefaults.buttonColors(
							backgroundColor = Color.Transparent,
							contentColor = MaterialTheme.colors.primary,
							disabledBackgroundColor = Color.Transparent,
							disabledContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
						)
					) {
						Icon(
							painter = painterResource(id = R.drawable.ic_ios_share_24),
							contentDescription = null,
							tint = MaterialTheme.colors.onSurface
						)
					}
					//The Edit Button
					Button(
						onClick = onEditClicked,
						contentPadding = PaddingValues(0.dp),
						enabled = true,
						shape = MaterialTheme.shapes.small,
						border = null,
						elevation = null,
						modifier = Modifier.testTag(TestTags.SELECT_CONFIG_BUTTON_EDIT_PREFIX + configuration.name),
						colors = ButtonDefaults.buttonColors(
							backgroundColor = Color.Transparent,
							contentColor = MaterialTheme.colors.primary,
							disabledBackgroundColor = Color.Transparent,
							disabledContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
						)
					) {
						Icon(
							painter = painterResource(id = R.drawable.ic_baseline_edit_24),
							contentDescription = null,
							tint = MaterialTheme.colors.onSurface
						)
					}
					//The Duplicate Button
					Button(
						onClick = onDuplicateClicked,
						contentPadding = PaddingValues(0.dp),
						enabled = !hasErrors,
						shape = MaterialTheme.shapes.small,
						border = null,
						elevation = null,
						modifier = Modifier.testTag(TestTags.SELECT_CONFIG_BUTTON_DUPLICTAE_PREFIX + configuration.name),
						colors = ButtonDefaults.buttonColors(
							backgroundColor = Color.Transparent,
							contentColor = MaterialTheme.colors.onSurface,
							disabledBackgroundColor = Color.Transparent,
							disabledContentColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.disabled),
						)
					) {
						Icon(
							painter = painterResource(id = R.drawable.duplicate_icon_24),
							contentDescription = null
						)
					}
				}
			}
		}
	}
}