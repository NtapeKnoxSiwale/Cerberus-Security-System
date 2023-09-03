package dev.knox.cerberus.ui.home;

public class Trigger {
    private String triggerName;
    private String roomNumber;
    private String maxInput;
    private String minInput;
    private String notificationType;
    private String alertType;

    public Trigger() {
        // Default constructor required for Firebase
    }

    public Trigger(String triggerName, String roomNumber, String maxInput, String minInput, String notificationType, String alertType) {
        this.triggerName = triggerName;
        this.roomNumber = roomNumber;
        this.maxInput = maxInput;
        this.minInput = minInput;
        this.notificationType = notificationType;
        this.alertType = alertType;
    }

    // Getters and setters for the fields

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getMaxInput() {
        return maxInput;
    }

    public void setMaxInput(String maxInput) {
        this.maxInput = maxInput;
    }

    public String getMinInput() {
        return minInput;
    }

    public void setMinInput(String minInput) {
        this.minInput = minInput;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

}
