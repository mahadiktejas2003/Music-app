package com.example.musicapp

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log


import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberImagePainter
import com.example.musicapp.ui.theme.MusicAppTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MusicAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MusicAppUI()
                }
            }
        }
    }
}

@Composable
fun MusicAppUI() {
    var dataList by remember { mutableStateOf<List<Data>>(emptyList()) }

    val retrofitBuilder = Retrofit.Builder()
        .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)

    val retrofitData = retrofitBuilder.getData("eminem")
    LaunchedEffect(retrofitData) {
        retrofitData.enqueue(object : Callback<MyData> {
            override fun onResponse(call: Call<MyData>, response: Response<MyData?>) {
                val fetchedDataList = response.body()?.data ?: emptyList()
                dataList = fetchedDataList
            }

            override fun onFailure(call: Call<MyData>, t: Throwable) {
                Log.d("TAG: onFailure: ", "onFailure: " + t.message)
            }
        })
    }

    LazyColumn {
        items(dataList) { data ->
            MusicItem(data, context = LocalContext.current)
            Divider() // Add a divider between items
        }
    }

}
@Composable
fun MusicItem(data: Data, context: Context) {
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Image(
                painter = rememberImagePainter(data.album.cover),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = data.title, style = MaterialTheme.typography.bodySmall)
                Text(text = data.album.title, style = MaterialTheme.typography.bodyMedium)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val playIcon = painterResource(id = R.drawable.baseline_play_arrow_24)
                    val pauseIcon = painterResource(id = R.drawable.baseline_pause_24)

                    Image(
                        painter = if (isPlaying) pauseIcon else playIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                if (isPlaying) {
                                    mediaPlayer?.stop()
                                    mediaPlayer?.release()
                                    mediaPlayer = null
                                    isPlaying = false
                                } else {
                                    mediaPlayer = MediaPlayer.create(context, data.preview.toUri())
                                    mediaPlayer?.start()
                                    isPlaying = true
                                }
                            }
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPlaying) "Playing" else "Paused",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isPlaying) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    }
}


