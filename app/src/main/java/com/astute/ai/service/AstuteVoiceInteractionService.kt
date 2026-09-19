package com.astute.ai.service

import android.service.voice.VoiceInteractionService

class AstuteVoiceInteractionService : VoiceInteractionService() {
    
    override fun onReady() {
        // Called when the service is ready to accept commands
    }
    
    override fun onLaunchVoiceCommand(keyEvent: android.view.KeyEvent) {
        // Handle direct voice command launches
    }
    
    override fun onCreateSessionService(): VoiceInteractionSessionService {
        return AstuteVoiceInteractionSessionService()
    }
}
