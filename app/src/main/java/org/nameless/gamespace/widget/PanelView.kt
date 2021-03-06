/*
 * Copyright (C) 2021 Chaldeaprjkt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nameless.gamespace.widget

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.doOnLayout
import org.nameless.gamespace.R
import org.nameless.gamespace.utils.dp
import org.nameless.gamespace.utils.isPortrait
import kotlin.math.max
import kotlin.math.min

class PanelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var defaultY: Float? = null
    var relativeY = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.panel_view, this, true)
        isClickable = true
        isFocusable = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        applyRelativeLocation()
        updateMemoryAndTemp()
    }

    private fun updateMemoryAndTemp() {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        val availMB = (memInfo.availMem / 1048576L).toInt();
        val totalMB = (memInfo.totalMem / 1048576L).toInt();

        val intent: Intent =
                context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0).toFloat() / 10

        val info: TextView = findViewById(R.id.memory_temp_info)
        info.text = context.getString(R.string.memory_temperature_info, availMB, totalMB, temp)
    }

    private fun applyRelativeLocation() {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        layoutParams.height = LayoutParams.WRAP_CONTENT

        doOnLayout {
            if (defaultY == null)
                defaultY = y

            y = if (wm.isPortrait()) {
                val safeArea = rootWindowInsets.getInsets(WindowInsets.Type.systemBars())
                val minY = safeArea.top + 16.dp
                val maxY = safeArea.top + (parent as View).height - safeArea.bottom - height - 16.dp
                min(max(relativeY, minY), maxY).toFloat()
            } else {
                defaultY ?: 16f
            }

        }
    }

}
