package com.edutrack

/**
 * Returns the platform-specific social sign-in button label.
 * Android: "Continue with Google" (Google Sign-In)
 * iOS: "Continue with Apple" (Apple Sign-In)
 */
expect fun getSocialSignInLabel(): String
