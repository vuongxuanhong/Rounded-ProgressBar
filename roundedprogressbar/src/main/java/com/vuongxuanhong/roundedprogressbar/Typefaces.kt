package com.vuongxuanhong.roundedprogressbar

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import java.util.*


/**
 * Created by vuongxuanhong on 1/10/19. HappyCoding!
 */
object Typefaces {
    private val TAG = "Typefaces"

    private val assetCache = mutableMapOf<String, Typeface>()
    private val resIDCache = mutableMapOf<Int, Typeface>()

    operator fun get(c: Context, assetPath: String): Typeface? {
        synchronized(assetCache) {
            if (!assetCache.containsKey(assetPath)) {
                try {
                    val t = Typeface.createFromAsset(c.assets,
                            assetPath)
                    assetCache.put(assetPath, t)
                    Log.e(TAG, "Loaded '$assetPath")
                } catch (e: Exception) {
                    Log.e(TAG, "Could not get typeface '" + assetPath
                            + "' because " + e.message)
                    return null
                }

            }
            return assetCache.get(assetPath)
        }
    }

    operator fun get(c: Context, fontResId: Int): Typeface? {
        synchronized(resIDCache) {
            if (!resIDCache.containsKey(fontResId)) {
                try {
                    val t = ResourcesCompat.getFont(c, fontResId)
                    t?.let {
                        resIDCache.put(fontResId, it)
                    }
                    Log.e(TAG, "Loaded '$fontResId")
                } catch (e: Exception) {
                    Log.e(TAG, "Could not get typeface '" + fontResId + "' because " + e.message)
                    return null
                }

            }
            return resIDCache[fontResId]
        }
    }

}