package com.player.demo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint.Align
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.network.HttpException
import coil.request.ImageRequest
import com.player.demo.includes.Tools.parseJsonFromAssets
import com.player.demo.model.MovieModel
import com.player.demo.ui.theme.ExoplayerComposeTheme


class MainActivity : ComponentActivity() {

    @SuppressLint("UnrememberedMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExoplayerComposeTheme() {
                ListViewContent()
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExoplayerComposeTheme() {
        ListViewContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListViewContent() {

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column(modifier = Modifier.padding(4.dp)) {
                    Text(
                        text = "ExoPlayer Demo",
                        modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically)
                    )
                }
            }, navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Filled.Menu,
                        "",
                    )
                }
            }, actions = {

            })

        },
        content = { paddingValues ->
            VerticalListView(paddingValues)
        }
    )

}

@Composable
fun VerticalListView(paddingValues: PaddingValues) {
    val context = LocalContext.current
    val COLUMN_COUNT = 2
    val GRID_SPACING = 8.dp
    var landscape: Boolean = false
    val itemList = remember { parseJsonFromAssets(context, "data.json") }
    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMN_COUNT),
        modifier = Modifier.padding(paddingValues),
        contentPadding = PaddingValues(
            start = GRID_SPACING,
            end = GRID_SPACING,
        ),
        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING, Alignment.CenterHorizontally),
        content = {
            items(itemList.size) { index ->
                val movie = itemList[index]
                MovieItemCard(movie, Modifier.width(170.dp))
                ListItemDivider()
            }
        })
}


@Composable
fun MovieItemCard(item: MovieModel?, modifier: Modifier) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(10.dp)
            .background(color = Color.White)
            .clickable {
                val intent = Intent(context, VideoPlayerActivity::class.java)
                intent.putExtra("Key", item!!.trailer_url)
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = modifier
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item!!.thumbnail)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
                onError = { ex ->
                    Log.e("TAG_IMAGE_ERROR", "MovieItemCard: "+ex.result.request.error)
                },
                error = {
                    painterResource(R.drawable.ic_error)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),

                loading = {
                    painterResource(R.drawable.placeholder_image)
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = item.title,
                modifier = Modifier
                    .padding(start = 4.dp, top = 4.dp)
                    .fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
                , fontStyle = FontStyle.Normal
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

    }

}


@Composable
private fun ListItemDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    )
}

