package net.huray.omronsdk.common

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

open class BaseActivity : AppCompatActivity() {

    inline fun <reified T : ViewModel> viewModelsFactory(crossinline viewModelInitialization: () -> T): Lazy<T> {
        return viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return viewModelInitialization.invoke() as T
                }
            }
        }
    }
}