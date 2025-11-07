package com.example.tastebuddies;

import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

public class UiUtils {
    
    /**
     * Makes an ImageView circular by setting a circular outline provider.
     * This should be called after the view has been laid out.
     * 
     * @param imageView The ImageView to make circular
     */
    public static void makeCircular(ImageView imageView) {
        if (imageView == null) {
            return;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.post(() -> {
                if (imageView != null) {
                    imageView.setClipToOutline(true);
                    imageView.setOutlineProvider(new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, android.graphics.Outline outline) {
                            int size = Math.min(view.getWidth(), view.getHeight());
                            outline.setOval(0, 0, size, size);
                        }
                    });
                }
            });
        }
    }
    
    /**
     * Makes an ImageView have rounded corners with the specified corner radius.
     * This should be called after the view has been laid out.
     * 
     * @param imageView The ImageView to apply rounded corners to
     * @param cornerRadius The corner radius in pixels
     */
    public static void makeRoundedCorners(ImageView imageView, float cornerRadius) {
        if (imageView == null) {
            return;
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.post(() -> {
                if (imageView != null) {
                    imageView.setClipToOutline(true);
                    float finalRadius = cornerRadius;
                    imageView.setOutlineProvider(new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, android.graphics.Outline outline) {
                            outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), finalRadius);
                        }
                    });
                }
            });
        }
    }
}

