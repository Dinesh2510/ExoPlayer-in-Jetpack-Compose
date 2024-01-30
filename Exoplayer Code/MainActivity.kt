package com.player.application

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
import androidx.compose.material3.Surface
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.network.HttpException
import coil.request.ImageRequest
import com.player.application.includes.Tools.parseJsonFromAssets
import com.player.application.model.MovieModel
import com.player.application.ui.theme.AppTheme


class MainActivity() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VideoListScreen()
                }
            }
        }
    }

}
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun VideoListScreen() {
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
                }, actions = {})

            },
            content = { paddingValues ->
                VerticalListing(paddingValues)

            }
        )

    }

    @Composable
    fun VerticalListing(paddingValues: PaddingValues) {
        val context = LocalContext.current
        val itemList = remember {
            parseJsonFromAssets(context, "data.json")
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(
                start = 8.dp,
                end = 8.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            content = {
                items(itemList.size) { index ->
                    val movie = itemList[index]
                    MovieItemCard(movie)
                    ListItemDivider()
                }
            })


    }

    @Composable
    fun MovieItemCard(movie: MovieModel) {
        val context = LocalContext.current

        Card(
            modifier = Modifier
                .padding(10.dp)
                .background(color = Color.White)
                .clickable {
                    val intent = Intent(context, VideoPlayerActivity::class.java)
                    intent.putExtra("key", movie.trailer_url)
                    context.startActivity(intent)
                },
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.width(170.dp)) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(movie!!.thumbnail)
                        .crossfade(true).build(),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxWidth().height(220.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = movie.title,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Normal
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

