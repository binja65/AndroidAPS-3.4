package app.aaps.plugins.main.di

import app.aaps.plugins.main.general.remora.RemoraFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
@Suppress("unused")
abstract class RemoraModule {

    @ContributesAndroidInjector abstract fun contributesRemoraFragment(): RemoraFragment
}