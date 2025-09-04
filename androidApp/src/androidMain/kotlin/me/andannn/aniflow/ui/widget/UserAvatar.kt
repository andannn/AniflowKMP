package me.andannn.aniflow.ui.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import me.andannn.aniflow.data.model.UserModel

@Composable
fun UserAvatar(
    modifier: Modifier = Modifier,
    user: UserModel?,
    onAuthIconClick: () -> Unit = {},
) {
    Box(
        modifier = modifier.size(36.dp),
    ) {
        if (user != null) {
            BadgedBox(
                badge = {
                    val badgeNumber = user.unreadNotificationCount
                    if (badgeNumber != 0) {
                        Badge {
                            Text(
                                badgeNumber.toString(),
                            )
                        }
                    }
                },
            ) {
                IconButton(
                    onClick = onAuthIconClick,
                ) {
                    AsyncImage(
                        model = user.avatar,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        } else {
            IconButton(
                onClick = onAuthIconClick,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                )
            }
        }
    }
}
