package com.example.androidlab2

import MoviesViewModel
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

enum class AppScreen(@StringRes val title: Int) {
    MainMenu(title = R.string.main_menu),
    Movie(title = R.string.chosen_movie),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesAppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    title: String,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = title) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesApp(
    moviesViewModel: MoviesViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.MainMenu.name
    )

    val topBarTitle = if (currentScreen==AppScreen.MainMenu) "Menu Główne" else moviesViewModel.getCurrentMovie()

    Scaffold(
        topBar = {
            MoviesAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                topBarTitle
            )
        }
    ) { innerPadding ->
        val moviesState by moviesViewModel.moviesState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = AppScreen.MainMenu.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = AppScreen.MainMenu.name) {
                MovieMenu(moviesViewModel, moviesState, {navController.navigate(AppScreen.Movie.name)})
            }
            composable(route = AppScreen.Movie.name) {
                MovieScreen(moviesViewModel, moviesState)
            }
        }
    }
}

