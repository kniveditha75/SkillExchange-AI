package com.simats.skillexchange.ui.theme

import androidx.compose.ui.graphics.Color

// ── Standard Professional Palette ───────────────────────────────────────────
// Moving away from the "Midnight/Premium" look to a clean, normal app theme.

val PrimaryBlue = Color(0xFF2563EB)   // Standard deep blue
val PrimaryDark = Color(0xFF1E40AF)
val AccentTeal  = Color(0xFF0D9488)

// Surface Colors (Premium Lavender Theme)
val BgLight     = Color(0xFFF3F4FF)   // Soft lavender background
val BgWhite     = Color(0xFFFFFFFF)
val BorderGray  = Color(0xFFE2E2F0)

// Typography Colors
val TextDark    = Color(0xFF0F172A)
val TextGray    = Color(0xFF64748B)
val TextLight   = Color(0xFF94A3B8)

// Legacy / Compatibility Colors (mapped to the new clean theme)
val Purple500   = PrimaryBlue
val Purple700   = PrimaryDark
val Teal200     = AccentTeal
val Teal500     = AccentTeal
val BgPremium   = BgLight
val CardDark    = BgWhite

// Utility Colors
val SuccessGreen = Color(0xFF10B981)
val ErrorRed     = Color(0xFFEF4444)
val Orange500    = Color(0xFFF59E0B)

// Typography Aliases
val OnBackground = TextDark
val OnSurface    = TextDark
val TextMuted    = TextGray
val LightOnSurface = TextDark
val LightTextGray  = TextGray
val TealAccent     = AccentTeal
val LightBorder    = BorderGray
val PurplePrimary  = PrimaryBlue
val OrangeAccent   = Orange500

