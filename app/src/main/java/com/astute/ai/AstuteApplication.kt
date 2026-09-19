package com.astute.ai

import android.app.Application
import android.app.ActivityManager
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class AstuteApplication : Application() {
    
    companion object {
        lateinit var instance: AstuteApplication
            private set
        
        val globalScope: CoroutineScope by lazy {
            CoroutineScope(SupervisorJob() + Dispatchers.Main)
        }
        
        fun isProcessRunning(context: Context, processName: String): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            return manager.runningAppProcesses.any { 
                it.processName == processName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND 
            }
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        initializeCrashHandler()
        initializeProcessMonitor()
    }
    
    private fun initializeCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val crashLog = buildString {
                appendLine("=== ASTUTE AI CRASH LOG ===")
                appendLine("Thread: ${thread.name}")
                appendLine("Timestamp: ${System.currentTimeMillis()}")
                appendLine("Exception: ${throwable.javaClass.canonicalName}")
                appendLine("Message: ${throwable.message}")
                appendLine("Stack Trace:")
                throwable.stackTrace.forEach { stackTraceElement ->
                    appendLine("\tat $stackTraceElement")
                }
            }
            
            android.util.Log.e("ASTUTE_CRASH", crashLog)
            android.os.Process.killProcess(android.os.Process.myPid())
            android.os.Process.exit(1)
        }
    }
    
    private fun initializeProcessMonitor() {
        if (!isMainThread()) {
            return
        }
        
        // Register lifecycle observers for process management
        // Implementation would go here for monitoring service lifecycles
    }
    
    private fun isMainThread(): Boolean {
        return Thread.currentThread() == android.os.Looper.getMainLooper().thread
    }
    
    override fun onTerminate() {
        super.onTerminate()
        globalScope.cancel()
    }
}
