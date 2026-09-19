package com.astute.ai.service

import android.content.Intent
import android.os.Bundle
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService
import android.util.Log

class AstuteVoiceInteractionSessionService : VoiceInteractionSessionService() {
    
    companion object {
        private const val TAG = "VoiceSessionService"
    }
    
    override fun onPrepareSession(args: Bundle?) {
        super.onPrepareSession(args)
        Log.d(TAG, "onPrepareSession called")
    }
    
    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)
        Log.d(TAG, "onShow called with showFlags: $showFlags")
        
        try {
            // Launch the overlay activity with proper flags
            val intent = Intent(this, ui.AssistantOverlayActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_NO_HISTORY
                putExtras(args ?: Bundle())
            }
            
            startActivity(intent)
            finish()
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch overlay activity", e)
            // Fall back to showing the session if launch fails
            super.onShow(args, showFlags)
        }
    }
    
    override fun onActiveChanged(active: Boolean) {
        super.onActiveChanged(active)
        Log.d(TAG, "Session active changed: $active")
    }
    
    override fun onHidden() {
        super.onHidden()
        Log.d(TAG, "Session hidden")
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "VoiceInteractionSessionService created")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "VoiceInteractionSessionService destroyed")
    }
}
