package com.chassisui.example.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chassisui.example.LocalAppModel
import com.chassisui.example.R
import com.chassisui.example.chassis.ChassisIcon
import com.chassisui.example.chassis.ChassisImage
import com.chassisui.example.chassis.ChassisType
import com.chassisui.example.chassis.rememberPageTransitions
import com.chassisui.example.components.ActionBar
import com.chassisui.example.components.ButtonKind
import com.chassisui.example.components.ChassisButton
import com.chassisui.example.components.ChassisTextField
import com.chassisui.example.components.FieldStatus
import com.chassisui.example.components.InlineBar

private class OnboardingPage(val image: ChassisImage, val title: String, val message: String)

private val pages = listOf(
    OnboardingPage(
        ChassisImage.Parts,
        "One app, any brand",
        "A build setting chooses the brand. The screens stay the same.",
    ),
    OnboardingPage(
        ChassisImage.Tokens,
        "Tokens for every value",
        "Colors, type, spacing and motion all come from a versioned package.",
    ),
    OnboardingPage(
        ChassisImage.Platforms,
        "Assets by role",
        "Fonts, icons and images come from a release, under the same names.",
    ),
)

/** The first launch: pages to swipe, then a form. */
@Composable
fun OnboardingView() {
    val navController = rememberNavController()
    val transitions = rememberPageTransitions()
    NavHost(
        navController,
        startDestination = "pages",
        enterTransition = transitions.enter,
        exitTransition = transitions.exit,
        popEnterTransition = transitions.popEnter,
        popExitTransition = transitions.popExit,
    ) {
        composable("pages") {
            OnboardingPages(onStart = { navController.navigate("sign-in") })
        }
        composable("sign-in") {
            SignInView(navController)
        }
    }
}

@Composable
private fun OnboardingPages(onStart: () -> Unit) {
    val pager = rememberPagerState { pages.size }
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_page_small_gap)),
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.color_context_default_bg_main))
            .safeDrawingPadding()
            .padding(vertical = dimensionResource(R.dimen.space_page_small_padding_y)),
    ) {
        Image(
            painter = painterResource(ChassisImage.Logo.drawable),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.size_context_small)),
        )
        HorizontalPager(pager, Modifier.weight(1f)) { index ->
            val page = pages[index]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_medium), Alignment.CenterVertically),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.space_page_small_padding_x)),
            ) {
                Image(
                    painter = painterResource(page.image.drawable),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Text(
                    page.title,
                    style = ChassisType.h3.style,
                    color = colorResource(R.color.color_context_default_fg_main),
                    textAlign = TextAlign.Center,
                )
                Text(
                    page.message,
                    style = ChassisType.bodyMedium.style,
                    color = colorResource(R.color.color_context_default_fg_subtle),
                    textAlign = TextAlign.Center,
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_xsmall), Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth(),
        ) {
            pages.indices.forEach { index ->
                Box(
                    Modifier
                        .size(dimensionResource(R.dimen.space_unit_8))
                        .background(
                            colorResource(
                                if (index == pager.currentPage) {
                                    R.color.color_context_primary_base_color
                                } else {
                                    R.color.color_context_default_fg_slight
                                },
                            ),
                            CircleShape,
                        ),
                )
            }
        }
        ChassisButton(
            "Get started",
            Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.space_page_small_padding_x)),
            onClick = onStart,
        )
    }
}

/** A form with the states of the form tokens. */
@Composable
private fun SignInView(navController: NavController) {
    val model = LocalAppModel.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var hasTriedSubmit by rememberSaveable { mutableStateOf(false) }
    val emailIsValid = "@" in email && "." in email
    val emailStatus = when {
        emailIsValid -> FieldStatus.Success
        hasTriedSubmit -> FieldStatus.Error
        else -> FieldStatus.Regular
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = colorResource(R.color.color_context_default_bg_main),
        topBar = { InlineBar("Sign in", onBack = { navController.popBackStack() }) },
        bottomBar = {
            ActionBar {
                ChassisButton(
                    "Skip",
                    Modifier.weight(1f),
                    kind = ButtonKind.Outline,
                    onClick = { model.updateSignedIn(true) },
                )
                ChassisButton("Sign in", Modifier.weight(1f)) {
                    hasTriedSubmit = true
                    if (emailIsValid) {
                        model.updateSignedIn(true)
                    }
                }
            }
        },
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.space_context_large)),
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.space_page_small_padding_x)),
        ) {
            Text(
                "Sign in to continue",
                style = ChassisType.h3.style,
                color = colorResource(R.color.color_context_default_fg_main),
            )
            ChassisTextField(
                label = "Email",
                icon = ChassisIcon.Email,
                text = email,
                onTextChange = { email = it },
                placeholder = "name@example.com",
                help = if (emailStatus == FieldStatus.Error) "Enter an email address." else "",
                status = emailStatus,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                autofill = ContentType.Username,
            )
            ChassisTextField(
                label = "Password",
                icon = ChassisIcon.Lock,
                text = password,
                onTextChange = { password = it },
                placeholder = "Any password works here",
                help = "This example checks nothing and stores nothing.",
                isSecure = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                autofill = ContentType.Password,
            )
        }
    }
}
