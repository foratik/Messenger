package org.example.model;

import java.util.*;

public class Message {
    public User sender;
    public String text;
    public boolean isSeen;
    public String time;
    public HashMap<String,Boolean> getters;
    public boolean isNotDeleted;
    public boolean isNotDeletedForSelf;
    public HashMap<React , Integer> reactions;

    public Message(User sender, String text, HashMap<String, Boolean> getters) {
        this.sender = sender;
        this.text = text;
        this.getters = getters;

        setTime();
        isNotDeleted = true;
        isNotDeletedForSelf = true;
        reactions = new HashMap<>();
        reactions.put(React.DISLIKE,0);
        reactions.put(React.LAUGH,0);
        reactions.put(React.LIKE,0);
        isSeen = false;
    }

    private void setTime(){
        long unixTime = System.currentTimeMillis() / 1000L;
        Date time=new java.util.Date((long)unixTime*1000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        Formatter formatter  = new Formatter();
        this.time = String.valueOf(formatter.format("%02d:%02d",hours,minutes));
    }
}

