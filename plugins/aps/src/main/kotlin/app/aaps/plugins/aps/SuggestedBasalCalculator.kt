package app.aaps.plugins.aps

import app.aaps.core.interfaces.profile.Profile
import javax.inject.Inject
import javax.inject.Singleton

data class SuggestedBasalSettings(
    val enabled: Boolean,
    val multiplier: Double
)

data class SuggestedBasalInput(
    val tdd: Double?,
    val profile: Profile,
    val settings: SuggestedBasalSettings
)

@Singleton
class SuggestedBasalCalculator @Inject constructor() {

    @Suppress("UNUSED_PARAMETER")
    fun calculateSuggestedBasalRate(input: SuggestedBasalInput): Double {
        // Placeholder implementation: real calculation will incorporate TDD, basal profile, and user settings.
        return 1.0
    }
}
