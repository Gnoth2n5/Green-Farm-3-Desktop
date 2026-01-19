package com.greenfarm3.game;

/**
 * Camera/viewport system for following the player and scrolling the map.
 * Based on the original J2ME camera system (a[0][13] = camera X, a[0][14] = camera Y).
 */
public class Camera {
    
    private int x;  // Camera position (world coordinates, top-left)
    private int y;
    private int viewWidth;   // Viewport width in pixels
    private int viewHeight;  // Viewport height in pixels
    private int mapWidth;    // Map width in pixels (for boundary clamping)
    private int mapHeight;   // Map height in pixels (for boundary clamping)
    
    private boolean smoothFollow = false;
    private float followSpeed = 0.1f;  // For smooth following (0.0 to 1.0)
    
    /**
     * Create a new camera
     * @param viewWidth Viewport width in pixels
     * @param viewHeight Viewport height in pixels
     */
    public Camera(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.x = 0;
        this.y = 0;
        this.mapWidth = 0;
        this.mapHeight = 0;
    }
    
    /**
     * Set camera position directly
     * @param x World X coordinate (top-left of viewport)
     * @param y World Y coordinate (top-left of viewport)
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        clampToBounds();
    }
    
    /**
     * Set camera position to center on a point
     * @param centerX World X coordinate to center on
     * @param centerY World Y coordinate to center on
     */
    public void setCenter(int centerX, int centerY) {
        this.x = centerX - viewWidth / 2;
        this.y = centerY - viewHeight / 2;
        clampToBounds();
    }
    
    /**
     * Follow a target (center camera on target)
     * @param targetX Target X coordinate
     * @param targetY Target Y coordinate
     */
    public void follow(int targetX, int targetY) {
        if (smoothFollow) {
            // Smooth following with interpolation
            int targetCameraX = targetX - viewWidth / 2;
            int targetCameraY = targetY - viewHeight / 2;
            
            int dx = targetCameraX - x;
            int dy = targetCameraY - y;
            
            x += (int)(dx * followSpeed);
            y += (int)(dy * followSpeed);
        } else {
            // Instant following
            setCenter(targetX, targetY);
        }
        clampToBounds();
    }
    
    /**
     * Update camera (for smooth following)
     * Should be called every frame if using smooth follow
     */
    public void update() {
        // Currently nothing to update, but can add smooth following logic here
        clampToBounds();
    }
    
    /**
     * Clamp camera position to map boundaries
     */
    private void clampToBounds() {
        if (mapWidth > 0 && mapHeight > 0) {
            // Clamp X
            if (x < 0) {
                x = 0;
            } else if (x + viewWidth > mapWidth) {
                x = mapWidth - viewWidth;
            }
            
            // Clamp Y
            if (y < 0) {
                y = 0;
            } else if (y + viewHeight > mapHeight) {
                y = mapHeight - viewHeight;
            }
            
            // If viewport is larger than map, center it
            if (viewWidth > mapWidth) {
                x = (mapWidth - viewWidth) / 2;
            }
            if (viewHeight > mapHeight) {
                y = (mapHeight - viewHeight) / 2;
            }
        }
    }
    
    /**
     * Set map boundaries for clamping
     * @param mapWidth Map width in pixels
     * @param mapHeight Map height in pixels
     */
    public void setMapBounds(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        clampToBounds();
    }
    
    /**
     * Convert world coordinates to screen/viewport coordinates
     * @param worldX World X coordinate
     * @param worldY World Y coordinate
     * @return Array with [screenX, screenY]
     */
    public int[] worldToScreen(int worldX, int worldY) {
        return new int[]{worldX - x, worldY - y};
    }
    
    /**
     * Convert screen/viewport coordinates to world coordinates
     * @param screenX Screen X coordinate
     * @param screenY Screen Y coordinate
     * @return Array with [worldX, worldY]
     */
    public int[] screenToWorld(int screenX, int screenY) {
        return new int[]{screenX + x, screenY + y};
    }
    
    /**
     * Check if a world position is visible in the viewport
     * @param worldX World X coordinate
     * @param worldY World Y coordinate
     * @param width Width of the object
     * @param height Height of the object
     * @return true if visible
     */
    public boolean isVisible(int worldX, int worldY, int width, int height) {
        return !(worldX + width < x || worldX > x + viewWidth ||
                 worldY + height < y || worldY > y + viewHeight);
    }
    
    /**
     * Get viewport X coordinate (camera position)
     * @return Viewport X
     */
    public int getViewX() {
        return x;
    }
    
    /**
     * Get viewport Y coordinate (camera position)
     * @return Viewport Y
     */
    public int getViewY() {
        return y;
    }
    
    /**
     * Get viewport width
     * @return Viewport width
     */
    public int getViewWidth() {
        return viewWidth;
    }
    
    /**
     * Get viewport height
     * @return Viewport height
     */
    public int getViewHeight() {
        return viewHeight;
    }
    
    /**
     * Get camera center X in world coordinates
     * @return Center X
     */
    public int getCenterX() {
        return x + viewWidth / 2;
    }
    
    /**
     * Get camera center Y in world coordinates
     * @return Center Y
     */
    public int getCenterY() {
        return y + viewHeight / 2;
    }
    
    /**
     * Enable or disable smooth following
     * @param enabled true for smooth following
     */
    public void setSmoothFollow(boolean enabled) {
        this.smoothFollow = enabled;
    }
    
    /**
     * Set follow speed for smooth following (0.0 to 1.0)
     * @param speed Follow speed (0.0 = no movement, 1.0 = instant)
     */
    public void setFollowSpeed(float speed) {
        this.followSpeed = Math.max(0.0f, Math.min(1.0f, speed));
    }
}
