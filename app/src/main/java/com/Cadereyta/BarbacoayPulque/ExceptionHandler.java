package com.Cadereyta.BarbacoayPulque;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Context context;

    public ExceptionHandler(Context context){
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex){
        Log.e("MyExceptionHandler", "Caught exception:", ex);


        // Handle critical errors gracefully (e.g., network issues, data persistence)
        if (isCriticalError(ex)) {
            handleCriticalError(ex);
        }

        // Safely redirect to MessageActivity, avoiding potential crashes
        try {
            Intent intent = new Intent(context, ErrorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Ensure clean activity transition
            //intent.putExtra("error_message", ex.getMessage()); // Pass error message if needed
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("MyExceptionHandler", "Error while starting MessageActivity:", e);
            // Fallback option (e.g., log to file, show a generic error message)
        }

        System.exit(1); // Optional: If app termination is still necessary after redirection
    }

    private boolean isCriticalError(Throwable ex) {

        // Define your criteria for critical errors (e.g., specific exception types)
        return ex instanceof OutOfMemoryError || ex instanceof SecurityException;
    }

    private void handleCriticalError(Throwable ex) {
        // Perform necessary actions for critical errors (e.g., data recovery, user notification)
        Log.d("Chris", "Cachao", ex);
    }
}




