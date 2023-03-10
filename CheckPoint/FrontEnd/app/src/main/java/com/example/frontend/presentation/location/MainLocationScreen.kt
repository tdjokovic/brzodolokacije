package com.example.frontend.presentation.location

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.frontend.common.navigation.Screen
import com.example.frontend.domain.model.Location

@Composable
fun MainLocationScreen(
    navController: NavController,
    viewModel : LocationViewModel = hiltViewModel()
)
{
    val state = viewModel.state.value
    var searchText by remember{ mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    )
    {
        ProfileTopBar(navController, viewModel)

        LocationSearchBar(searchText, onChange = {
            searchText = it
            if(searchText == ""){
                viewModel.getAllLocations();
            }
            else{
                viewModel.searchLocations(searchText)
            }
        })

        if(state.isLoading){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        else{
            LocationList(locationList = state.locations, navController = navController)
        }
    }
}


@Composable
fun LocationSearchBar(
    searchText : String,
    onChange: (String) -> Unit = {}
)
{
    var searchTextChange by remember {
        mutableStateOf(searchText)
    }

    val trailingIconView = @Composable {
        IconButton(onClick = {
            onChange("") //isprazni search text
        }) {
            Icon(
                Icons.Default.Clear,
                contentDescription ="",
                tint = Color.Black
            )
        }
    }

    TextField(
        value = searchText,
        trailingIcon = if(searchText.isNotBlank()) trailingIconView else null,
        onValueChange = {
            onChange(it)
        },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null)},
        modifier = Modifier
            .fillMaxWidth(),
        placeholder = {
            Text("Search locations")
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(20.dp)

        )
}

@Composable
fun LocationList(
    locationList : List<Location>?,
    navController: NavController
)
{

    if(locationList == null || locationList.isEmpty()){
        Text(
            text = "No locations found!",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
    }

    else{
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ){
            items(locationList){
                    location -> LocationCard(location = location, navController = navController)
            }
        }
    }
}


@Composable
fun LocationCard(
    location : Location,
    navController: NavController
)
{
    val painter = rememberImagePainter(
        data = "https://www.moneycrashers.com/wp-content/uploads/2018/01/thailand-trip-travel-budget.jpg",
        builder = {

        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(painter = painter,
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable{
                    navController.navigate(Screen.PostsScreen.withArgs(location.id));
                }
                .height(200.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(){
            Text(text = location.name)
        }
    }
}

@Composable
fun ProfileTopBar(
    navController: NavController,
    viewModel: LocationViewModel
)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {
        Row(){
            IconButton(onClick = {
                navController.navigate(Screen.NovPostScreen.route)
//                viewModel.getAllLocations()
//                navController.navigate(Screen.ProfileScreen.route); //navigate to new post
            }) {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = "",
                    tint = Color.Black
                )
            }

            IconButton(onClick = {
                viewModel.getAllLocations()
                navController.navigate(Screen.ProfileScreen.withArgs(viewModel.loginUserId));
            }) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "",
                    tint = Color.Black
                )
            }

            IconButton(onClick = {
//                viewModel.getAllLocations()
//                navController.navigate(Screen.ProfileScreen.route); //navigate to messages
            }) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "",
                    tint = Color.Black
                )
            }
        }
    }
}