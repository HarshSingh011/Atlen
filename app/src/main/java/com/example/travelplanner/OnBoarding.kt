package com.example.travelplanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch

class OnBoarding : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                OnBoardingScreen(findNavController())
            }
        }
    }
}

data class OnBoardingPage(
    val image: Int,
    val title: String,
    val description: String
)

val onboardingPages = listOf(
    OnBoardingPage(
        image = R.drawable.onboarding1,
        title = "Travel Your Way",
        description = "Craft itineraries that perfectly suit your travel style and preferences. From must-see attractions to hidden gems, Trippin makes every journey uniquely yours."
    ),
    OnBoardingPage(
        image = R.drawable.onboarding2,
        title = "Discover New Destinations",
        description = "Your journey to unforgettable adventures starts here. From iconic landmarks to off-the-beaten-path treasures, explore the world your way."
    ),
    OnBoardingPage(
        image = R.drawable.onboarding3,
        title = "Plan Effortlessly",
        description = "All your travel needs in one place—smart, simple, and seamless. Customize your itinerary, book accommodations, and manage your trip all in one app."
    )
)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Skip button
        TextButton(
            onClick = { navController.navigate(R.id.action_onBoarding_to_frontPage) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
                .zIndex(1f)
        ) {
            Text(
                text = "Skip",
                color = Color(0xFF3D5AFE),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(top = 48.dp),
            ) { page ->
                OnBoardingPage(onboardingPages[page])
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 70.dp)
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(onboardingPages.size) { iteration ->
                        Box(
                            modifier = Modifier
                                .size(
                                    width = if (pagerState.currentPage == iteration) 24.dp else 8.dp,
                                    height = 8.dp
                                )
                                .clip(CircleShape)
                                .background(
                                    color = if (pagerState.currentPage == iteration) {
                                        Color(0xFF3D5AFE)
                                    } else {
                                        Color(0xFFE0E0E0)
                                    }
                                )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .zIndex(1f)
                ) {
                    Button(
                        onClick = {
                            if (pagerState.currentPage < onboardingPages.size - 1) {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage + 1,
                                        animationSpec = tween(300)
                                    )
                                }
                            } else {
                                navController.navigate(R.id.action_onBoarding_to_frontPage)
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(8.dp, CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF3D5AFE),
                                        Color(0xFF304FFE)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.whitearrow),
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnBoardingPage(page: OnBoardingPage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = page.image),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(horizontal = 16.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = page.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF666666),
            modifier = Modifier.padding(horizontal = 16.dp),
            lineHeight = 24.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun OnBoardingScreenPreview() {
    OnBoardingScreen(rememberNavController())
}

